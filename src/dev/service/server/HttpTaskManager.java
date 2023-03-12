package dev.service.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.domain.TaskBase;
import dev.service.manager.FileBackedTasksManager;
import dev.service.TaskAdapter;

import java.io.IOException;
import java.net.HttpRetryException;
import java.util.List;
import java.util.Optional;

public class HttpTaskManager extends FileBackedTasksManager {
    public static final String KEY = "ALIAS";
    private final KVTaskClient client;
    private final String host;
    private final Gson gson;
    private boolean isComplete;

    public Gson getGson() {
        return gson;
    }

    public HttpTaskManager(String host, int kvPort) {
        super();
        this.isComplete = true;
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(TaskBase.class, new TaskAdapter())
                .create();
        this.host = host;
        client = new KVTaskClient(this.host, kvPort);
        client.register();
    }

    public void load() throws IOException {
        client.register();
        Optional<String> jsonString = client.load(KEY);
        if (jsonString.isEmpty()) {
            throw new HttpRetryException("Ресурс с заданным ключом не обнаружен.", 404, this.host);
        } else {
            TasksCollectionPack packs = gson.fromJson(jsonString.get(), TasksCollectionPack.class);
            if (packs == null) return;
            isComplete = false;
            this.removeAll();
            for (TaskBase task : packs.tasks()) {
                this.create(task);
            }
            for (int id : packs.history()) {
                TaskBase task = this.getTaskBase(id);
                this.historyManager.add(task);
            }
            isComplete = true;
        }
    }

    @Override
    public void save() {
        if (!isComplete) return;
        TasksCollectionPack packs = new TasksCollectionPack(getAllTasks(), historyManager.getHistoryId());
        String jsonString = gson.toJson(packs);
        client.register();
        try{
            client.put(KEY, jsonString);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private record TasksCollectionPack(List<TaskBase> tasks, List<Integer> history) {
    }
}
