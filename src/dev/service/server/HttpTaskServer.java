package dev.service.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dev.domain.Epic;
import dev.domain.Subtask;
import dev.domain.Task;
import dev.domain.TaskBase;
import dev.service.InvalidTaskDateException;
import dev.service.manager.TasksManager;
import dev.utils.KVServer;

import java.io.IOException;
import java.net.HttpRetryException;
import java.net.InetSocketAddress;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final TasksManager manager;
    private final Gson gson;

    public HttpTaskServer() throws IOException {
        manager = Managers.setHttpTaskManager("http://localhost", KVServer.PORT);
        gson = ((HttpTaskManager) manager).getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/task", this::getTasks);
        server.createContext("/tasks/epic", this::getEpics);
        server.createContext("/tasks/subtask", this::getSubtasks);
        server.createContext("/tasks/history", this::getHistory);
        server.createContext("/tasks/size", this::getAllSize);
        server.createContext("/tasks", this::rootAll);
    }

    private static int getId(HttpExchange h) {
        String params = h.getRequestURI().getQuery();
        if (params != null) {
            if (!params.isBlank()) {
                String paramName = params.substring(0, params.indexOf("="));
                if ("id".equals(paramName)) {
                    return Integer.parseInt(params.substring(params.indexOf("=") + 1));
                }
            }
        }
        return -1;
    }

    public void start() {
        System.out.println("Запускаем HttpTaskServer на порту " + PORT);
        server.start();
    }

    public void stop() {
        System.out.println("Останавливаем HttpTaskServer на порту " + PORT);
        server.stop(0);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    private int createTasks(TaskBase task) {
        try {
            manager.create(task);
            return 201;
        } catch (InvalidTaskDateException | IndexOutOfBoundsException | IOException ex) {
            return 409;
        }
    }

    private int updateTasks(TaskBase task) {
        try {
            manager.update(task);
            return 200;
        } catch (IndexOutOfBoundsException | IOException ex) {
            return 404;
        }
    }

    private void getTasks(HttpExchange h) throws IOException {
        try (h) {
            String path = h.getRequestURI().getPath().substring("/tasks/task".length());
            if ("GET".equals(h.getRequestMethod())) {
                try {
                    ((HttpTaskManager) manager).load();
                } catch (HttpRetryException e) {
                    h.sendResponseHeaders(502, 0);
                    return;
                }
                if ("/size".equals(path)) {
                    String gsonSize = gson.toJson(manager.taskSize());
                    sendText(h, gsonSize);
                } else {
                    int taskId = getId(h);
                    if (manager.containsTaskId(taskId)) {
                        String gsonTask = gson.toJson(manager.getTask(taskId));
                        sendText(h, gsonTask);
                    } else {
                        h.sendResponseHeaders(404, 0);
                    }
                }
            } else if ("POST".equals(h.getRequestMethod())) {
                String inputTask = readText(h);
                if (inputTask.isEmpty()) {
                    h.sendResponseHeaders(400, 0);
                    return;
                } else {
                    Task task = gson.fromJson(inputTask, Task.class);
                    h.sendResponseHeaders(createTasks(task), 0);
                }
            } else if ("PUT".equals(h.getRequestMethod())) {
                String gsonTask = readText(h);
                Task task = ((HttpTaskManager) manager).getGson().fromJson(gsonTask, Task.class);
                h.sendResponseHeaders(updateTasks(task), 0);
            } else if ("DELETE".equals(h.getRequestMethod())) {
                try {
                    ((HttpTaskManager) manager).load();
                } catch (HttpRetryException e) {
                    h.sendResponseHeaders(502, 0);
                    return;
                }
                int taskId = getId(h);
                if (taskId >= 0) {
                    if (manager.containsTaskId(taskId)) {
                        manager.removeTask(taskId);
                        h.sendResponseHeaders(204, 0);
                    } else {
                        h.sendResponseHeaders(404, 0);
                    }
                } else {
                    manager.removeAllTasks();
                    h.sendResponseHeaders(204, 0);
                }
            } else {
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void getEpics(HttpExchange h) throws IOException {
        try (h) {
            String path = h.getRequestURI().getPath().substring("/tasks/epic".length());
            if ("GET".equals(h.getRequestMethod())) {
                try {
                    ((HttpTaskManager) manager).load();
                } catch (HttpRetryException e) {
                    h.sendResponseHeaders(502, 0);
                    return;
                }
                if ("/size".equals(path)) {
                    String gsonSize = gson.toJson(manager.epicSize());
                    sendText(h, gsonSize);
                } else {
                    int epicId = getId(h);
                    if (manager.containsEpicId(epicId)) {
                        String gsonTask = gson.toJson(manager.getEpic(epicId));
                        sendText(h, gsonTask);
                    } else {
                        h.sendResponseHeaders(404, 0);
                    }
                }
            } else if ("POST".equals(h.getRequestMethod())) {
                String inputEpic = readText(h);
                if (inputEpic.isEmpty()) {
                    h.sendResponseHeaders(400, 0);
                    return;
                } else {
                    Epic epic = gson.fromJson(inputEpic, Epic.class);
                    h.sendResponseHeaders(createTasks(epic), 0);
                }
            } else if ("PUT".equals(h.getRequestMethod())) {
                String gsonTask = readText(h);
                Epic epic = ((HttpTaskManager) manager).getGson().fromJson(gsonTask, Epic.class);
                h.sendResponseHeaders(updateTasks(epic), 0);
            } else if ("DELETE".equals(h.getRequestMethod())) {
                try {
                    ((HttpTaskManager) manager).load();
                } catch (HttpRetryException e) {
                    h.sendResponseHeaders(502, 0);
                    return;
                }
                int epicId = getId(h);
                if (epicId >= 0) {
                    if (manager.containsEpicId(epicId)) {
                        manager.removeTask(epicId);
                        h.sendResponseHeaders(204, 0);
                    } else {
                        h.sendResponseHeaders(404, 0);
                    }
                } else {
                    manager.removeAllEpics();
                    h.sendResponseHeaders(204, 0);
                }
            } else {
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void getSubtasks(HttpExchange h) throws IOException {
        try (h) {
            String path = h.getRequestURI().getPath().substring("/tasks/subtask".length());
            if ("GET".equals(h.getRequestMethod())) {
                try {
                    ((HttpTaskManager) manager).load();
                } catch (HttpRetryException e) {
                    h.sendResponseHeaders(502, 0);
                    return;
                }
                if ("/size".equals(path)) {
                    String gsonSize = gson.toJson(manager.subtaskSize());
                    sendText(h, gsonSize);
                } else if ("/epic".equals(path)) {
                    int epicId = getId(h);
                    if (manager.containsEpicId(epicId)) {
                        Epic epic = manager.getEpic(epicId);
                        String gsonSubtasksId = gson.toJson(epic.subtaskIdList());
                        sendText(h, gsonSubtasksId);
                    } else {
                        h.sendResponseHeaders(404, 0);
                    }
                } else {
                    int subtaskId = getId(h);
                    if (manager.containsSubtaskId(subtaskId)) {
                        String gsonTask = gson.toJson(manager.getSubtask(subtaskId));
                        sendText(h, gsonTask);
                    } else {
                        h.sendResponseHeaders(404, 0);
                    }
                }
            } else if ("POST".equals(h.getRequestMethod())) {
                String inputSubtask = readText(h);
                if (inputSubtask.isEmpty()) {
                    h.sendResponseHeaders(400, 0);
                    return;
                } else {
                    Subtask subtask = gson.fromJson(inputSubtask, Subtask.class);
                    h.sendResponseHeaders(createTasks(subtask), 0);
                }
            } else if ("PUT".equals(h.getRequestMethod())) {
                String gsonSubtask = readText(h);
                Subtask subtask = ((HttpTaskManager) manager).getGson().fromJson(gsonSubtask, Subtask.class);
                h.sendResponseHeaders(updateTasks(subtask), 0);
            } else if ("DELETE".equals(h.getRequestMethod())) {
                try {
                    ((HttpTaskManager) manager).load();
                } catch (HttpRetryException e) {
                    h.sendResponseHeaders(502, 0);
                    return;
                }
                if ("/epic".equals(path)) {
                    int epicId = getId(h);
                    if (manager.containsEpicId(epicId)) {
                        Epic epic = manager.getEpic(epicId);
                        epic.removeAllTasks();
                        h.sendResponseHeaders(204, 0);
                    } else {
                        h.sendResponseHeaders(404, 0);
                    }
                } else {
                    int subtaskId = getId(h);
                    if (subtaskId >= 0) {
                        if (manager.containsSubtaskId(subtaskId)) {
                            manager.removeTask(subtaskId);
                            h.sendResponseHeaders(204, 0);
                        } else {
                            h.sendResponseHeaders(404, 0);
                        }
                    } else {
                        manager.removeAllSubtasks();
                        h.sendResponseHeaders(204, 0);
                    }
                }
            } else {
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void rootAll(HttpExchange h) throws IOException {
        try (h) {
            try {
                ((HttpTaskManager) manager).load();
            } catch (HttpRetryException e) {
                h.sendResponseHeaders(502, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String gsonIdCollection = ((HttpTaskManager) manager).getGson().toJson(manager.getAllTaskId());
                sendText(h, gsonIdCollection);
            } else if ("DELETE".equals(h.getRequestMethod())) {
                manager.removeAll();
                h.sendResponseHeaders(204, 0);
            } else {
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void getHistory(HttpExchange h) throws IOException {
        try (h) {
            try {
                ((HttpTaskManager) manager).load();
            } catch (HttpRetryException e) {
                h.sendResponseHeaders(502, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String gsonHistory = ((HttpTaskManager) manager).getGson().toJson(manager.getHistoryManager().getHistoryId());
                sendText(h, gsonHistory);
            } else {
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void getAllSize(HttpExchange h) throws IOException {
        System.out.println("/size");
        try (h) {
            try {
                ((HttpTaskManager) manager).load();
            } catch (HttpRetryException e) {
                h.sendResponseHeaders(502, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String gsonAllSize = ((HttpTaskManager) manager).getGson().toJson(manager.allSize());
                sendText(h, gsonAllSize);
            } else {
                h.sendResponseHeaders(405, 0);
            }
        }
    }
}
