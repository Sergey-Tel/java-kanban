package dev.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.domain.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

public class TaskAdapter extends TypeAdapter<TaskBase> {
    private final Gson gson;

    public TaskAdapter() {
        this.gson = new Gson();
    }

    @Override
    public void write(final JsonWriter jsonWriter, final TaskBase task) throws IOException {
        TaskTypeEnum type;
        String jsonTask;
        int epicTaskId = 0;
        if (task instanceof Epic) {
            type = TaskTypeEnum.EPIC;
            jsonTask = String.format(
                    "{\"id\":%d,\"type\":\"%s\",\"name\":%s,\"status\":\"%s\",\"start\":\"%s\",\"duration\":%d,\"description\":%s}",
                    task.getTaskId(),
                    type,
                    gson.toJson(task.getName()),
                    task.getStatus(),
                    task.getStartTime().isPresent() ? task.getStartTime().get().toEpochMilli() : "null",
                    task.getDuration(),
                    gson.toJson(task.getDescription()));
        } else if (task instanceof Subtask) {
            type = TaskTypeEnum.SUBTASK;
            epicTaskId = ((Subtask) task).getEpicId();
            jsonTask = String.format(
                    "{\"id\":%d,\"type\":\"%s\",\"name\":%s,\"status\":\"%s\",\"start\":\"%s\",\"duration\":%d,\"description\":%s,\"epic\":%d}",
                    task.getTaskId(),
                    type,
                    gson.toJson(task.getName()),
                    task.getStatus(),
                    task.getStartTime().isPresent() ? task.getStartTime().get().toEpochMilli() : "null",
                    task.getDuration(),
                    gson.toJson(task.getDescription()),
                    epicTaskId);
        } else {
            type = TaskTypeEnum.TASK;
            jsonTask = String.format(
                    "{\"id\":%d,\"type\":\"%s\",\"name\":%s,\"status\":\"%s\",\"start\":\"%s\",\"duration\":%d,\"description\":%s}",
                    task.getTaskId(),
                    type,
                    gson.toJson(task.getName()),
                    task.getStatus(),
                    task.getStartTime().isPresent() ? task.getStartTime().get().toEpochMilli() : "null",
                    task.getDuration(),
                    gson.toJson(task.getDescription()));
        }
        jsonWriter.jsonValue(jsonTask);
    }

    @Override
    public TaskBase read(final JsonReader jsonReader) {
        com.google.gson.JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(jsonReader).getAsJsonObject();
        int id = object.getAsJsonPrimitive("id").getAsInt();
        String type = object.getAsJsonPrimitive("type").getAsString();
        String name = object.getAsJsonPrimitive("name").getAsString();
        String statusString = object.getAsJsonPrimitive("status").getAsString();
        String startString = object.getAsJsonPrimitive("start").getAsString();
        long duration = object.getAsJsonPrimitive("duration").getAsLong();
        String description = object.getAsJsonPrimitive("description").getAsString();

        int epicId = 0;
        if("SUBTASK".equals(type))
        {
            epicId = object.getAsJsonPrimitive("epic").getAsInt();
        }
        TaskStatusEnum status = TaskStatusEnum.fromString(statusString);

        Optional<Instant> startTime;
        if ("null".equals(startString)) {
            startTime = Optional.empty();
        } else {
            startTime = Optional.of(Instant.ofEpochMilli(Long.parseLong(startString)));
        }

        switch (type) {
            case "EPIC":
                return new Epic(id, name, description);
            case "SUBTASK":
                return new Subtask(epicId, id, name, description, status, startTime, duration);
            case "TASK":
            default:
                return new Task(id, name, description, status, startTime, duration);
        }
    }
}