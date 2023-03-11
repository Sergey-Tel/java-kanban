package dev.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.domain.*;
import dev.service.server.HttpTaskServer;
import dev.service.server.Managers;
import dev.utils.KVServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpTaskServerTest {
    static Gson gson;
    public static HttpTaskServer httpServer;

    @BeforeAll
    public static void initServer() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(TaskBase.class, new TaskAdapter())
                .create();
        try {
            if (Managers.kvServer == null) {
                Managers.kvServer = new KVServer();
                Managers.kvServer.start();
            }
            if (httpServer == null) {
                httpServer = new HttpTaskServer();
                httpServer.start();
            }
        } catch (Exception ex) {
            assertNull(ex.getMessage());
        }
    }

    @BeforeEach
    void beforeEach() throws IOException, InterruptedException {
        if (Managers.kvServer == null) {
            Managers.kvServer = new KVServer();
            Managers.kvServer.start();
        }

        removeAll();
        createTask(new Task(0, "Моя задача 1"));
        createTask(new Task(1, "Моя задача 2"));
        createTask(new Task(2, "Моя задача 3"));
        createEpic(new Epic(3, "Моя эпик-задача 1"));
        createSubtask(new Subtask(3, 4, "Моя подзадача 1.1"));
        createSubtask(new Subtask(3, 5, "Моя подзадача 1.2"));
        createSubtask(new Subtask(3, 6, "Моя подзадача 1.3"));
        createEpic(new Epic(7, "Моя эпик-задача 2"));
        getTask(2);
        getEpic(3);
        getTask(0);
        getSubtask(5);
        getSubtask(4);
    }

    @Test
    void testCreateTask() throws IOException, InterruptedException {
        Task task = getTask(8);
        Assertions.assertNull(task);

        int code = createTask(new Task(8, "Моя тестовая задача"));
        Assertions.assertEquals(201, code);
        task = getTask(8);
        Assertions.assertNotNull(task);
        Assertions.assertEquals(8, task.getTaskId());
        Assertions.assertEquals("Моя тестовая задача", task.getName());

        code = createTask(new Task(8, "Моя тестовая задача"));
        Assertions.assertEquals(409, code);
    }

    @Test
    void testCreateEpic() throws IOException, InterruptedException {
        Epic epic = getEpic(8);
        Assertions.assertNull(epic);

        int code = createEpic(new Epic(8, "Моя тестовая эпик-задача"));
        Assertions.assertEquals(201, code);
        epic = getEpic(8);
        Assertions.assertNotNull(epic);
        Assertions.assertEquals(8, epic.getTaskId());
        Assertions.assertEquals("Моя тестовая эпик-задача", epic.getName());

        code = createEpic(new Epic(8, "Моя тестовая эпик-задача"));
        Assertions.assertEquals(409, code);
    }

    @Test
    void testCreateSubtask() throws IOException, InterruptedException {
        createEpic(new Epic(8, "Моя тестовая эпик-задача"));

        Subtask subtask = getSubtask(9);
        Assertions.assertNull(subtask);

        int code = createSubtask(new Subtask(8,9, "Моя тестовая подзадача"));
        Assertions.assertEquals(201, code);
        subtask = getSubtask(9);
        Assertions.assertNotNull(subtask);
        Assertions.assertEquals(9, subtask.getTaskId());
        Assertions.assertEquals("Моя тестовая подзадача", subtask.getName());

        code = createSubtask(new Subtask(8,9, "Моя тестовая подзадача"));
        Assertions.assertEquals(409, code);
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        createTask(new Task(3, "Моя тестовая задача"));
        Task task = getTask(3);
        Assertions.assertEquals(TaskStatusEnum.NEW, task.getStatus());
        task = (Task) task.clone(TaskStatusEnum.DONE);
        task = (Task) task.clone(2022, 7, 20, 10, 0);
        Assertions.assertEquals(TaskStatusEnum.DONE, task.getStatus());
        int code = updateTask(task);
        Assertions.assertEquals(200, code);

        Task oldTask = getTask(3);
        Assertions.assertNotNull(oldTask);
        Assertions.assertEquals(TaskStatusEnum.DONE, oldTask.getStatus());

        task = new Task(4, "Моя тестовая задача");
        code = updateTask(task);
        Assertions.assertEquals(404, code);
    }

    @Test
    void testUpdateEpic() throws IOException, InterruptedException {
        createEpic(new Epic(8, "Моя тестовая эпик-задача"));
        Epic epic = getEpic(8);
        Assertions.assertEquals(TaskStatusEnum.NEW, epic.getStatus());
        epic = (Epic) epic.clone("Моя тестовая эпик-задача", "Пояснение");

        Assertions.assertEquals("Пояснение", epic.getDescription());
        int code = updateEpic(epic);
        Assertions.assertEquals(200, code);

        Epic oldEpic = getEpic(8);
        Assertions.assertNotNull(oldEpic);
        Assertions.assertEquals("Пояснение", epic.getDescription());

        epic = new Epic(9, "Моя новая тестовая эпик-задача");
        code = updateEpic(epic);
        Assertions.assertEquals(404, code);
    }

    @Test
    void testUpdateSubtask() throws IOException, InterruptedException {
        createEpic(new Epic(8, "Моя тестовая эпик-задача"));
        Epic epic = getEpic(8);
        Assertions.assertEquals(TaskStatusEnum.NEW, epic.getStatus());

        createSubtask(new Subtask(8, 9, "Моя тестовая подзадача"));
        Subtask subtask = getSubtask(9);
        Assertions.assertEquals(TaskStatusEnum.NEW, subtask.getStatus());

        subtask = (Subtask) subtask.clone(TaskStatusEnum.DONE);
        subtask = (Subtask) subtask.clone(2022, 7, 20, 10, 0);
        Assertions.assertEquals(TaskStatusEnum.DONE, subtask.getStatus());
        int code = updateSubtask(subtask);
        Assertions.assertEquals(200, code);

        Subtask oldSubtask = getSubtask(9);
        Assertions.assertNotNull(oldSubtask);
        Assertions.assertEquals(TaskStatusEnum.DONE, oldSubtask.getStatus());

        Epic oldEpic = getEpic(8);
        Assertions.assertEquals(TaskStatusEnum.DONE, oldEpic.getStatus());

        subtask = new Subtask(8,9, "Моя тестовая подзадача");
        code = updateTask(subtask);
        Assertions.assertEquals(404, code);
    }

    @Test
    void testRemoveTask() throws IOException, InterruptedException {
        int code = removeTask(3);
        Assertions.assertEquals(404, code);

        createTask(new Task(3, "Моя тестовая задача"));
        Task task = getTask(3);
        Assertions.assertNotNull(task);

        code = removeTask(3);
        Assertions.assertEquals(204, code);
        task = getTask(3);
        Assertions.assertNull(task);

        code = removeAll();
        Assertions.assertEquals(204, code);

        code = removeAllTasks();
        Assertions.assertEquals(204, code);
    }

    @Test
    void testRemoveEpic() throws IOException, InterruptedException {
        int code = removeEpic(8);
        Assertions.assertEquals(404, code);

        createEpic(new Epic(8, "Моя тестовая эпик-задача"));
        Epic epic = getEpic(8);
        Assertions.assertNotNull(epic);

        createSubtask(new Subtask(8,9, "Моя тестовая подзадача 1"));
        createSubtask(new Subtask(8,10, "Моя тестовая подзадача 2"));
        Subtask subtask = getSubtask(9);
        Assertions.assertNotNull(subtask);
        subtask = getSubtask(10);
        Assertions.assertNotNull(subtask);

        code = removeEpic(8);
        Assertions.assertEquals(204, code);
        epic = getEpic(8);
        Assertions.assertNull(epic);

        subtask = getSubtask(9);
        Assertions.assertNull(subtask);
        subtask = getSubtask(10);
        Assertions.assertNull(subtask);

        code = removeAll();
        Assertions.assertEquals(204, code);

        code = removeAllEpics();
        Assertions.assertEquals(204, code);
    }

    @Test
    void testRemoveSubtask() throws IOException, InterruptedException {
        int code = removeSubtask(9);
        Assertions.assertEquals(404, code);

        createEpic(new Epic(8, "Моя тестовая эпик-задача"));
        createSubtask(new Subtask(8,9, "Моя тестовая подзадача"));

        Subtask subtask = getSubtask(9);
        Assertions.assertNotNull(subtask);

        code = removeSubtask(9);
        Assertions.assertEquals(204, code);
        subtask = getSubtask(9);
        Assertions.assertNull(subtask);

        Epic epic = getEpic(3);
        int childSubtaskSize = epic.size();

        code = sendDeleteCommand("/subtask/epic?id=3").statusCode();
        Assertions.assertEquals(204, code);

        Epic oldEpic = getEpic(3);
        Assertions.assertEquals(0, oldEpic.size());
        Assertions.assertNotEquals(childSubtaskSize, oldEpic.size());

        code = removeAll();
        Assertions.assertEquals(204, code);

        code = removeAllSubtasks();
        Assertions.assertEquals(204, code);
    }

    @Test
    void testGetAllTaskId() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetCommand("");
        List<Double> allId = gson.fromJson(response.body(), ArrayList.class);
        Assertions.assertEquals(8, allId.size());
        Assertions.assertEquals(0, allId.get(0));
        Assertions.assertEquals(4, allId.get(4));
        Assertions.assertEquals(7, allId.get(7));

        response = sendGetCommand("/subtask/epic?id=3");
        List<Double> childSubtasksId = gson.fromJson(response.body(), ArrayList.class);
        Assertions.assertEquals(3, childSubtasksId.size());
        Assertions.assertEquals(4, childSubtasksId.get(0));
        Assertions.assertEquals(5, childSubtasksId.get(1));
        Assertions.assertEquals(6, childSubtasksId.get(2));
    }

    @Test
    void testGetSize() throws IOException, InterruptedException {
        HttpResponse<String> response = sendDeleteCommand("/size");
        Assertions.assertEquals(405, response.statusCode());

        response = sendGetCommand("/size");
        int oldAllSize = Integer.parseInt(response.body());
        Assertions.assertEquals(8, oldAllSize);

        response = sendGetCommand("/task/size");
        int oldTaskSize = Integer.parseInt(response.body());
        Assertions.assertEquals(3, oldTaskSize);

        response = sendGetCommand("/epic/size");
        int oldEpicSize = Integer.parseInt(response.body());
        Assertions.assertEquals(2, oldEpicSize);

        response = sendGetCommand("/subtask/size");
        int oldSubtaskSize = Integer.parseInt(response.body());
        Assertions.assertEquals(3, oldSubtaskSize);

        createTask(new Task(8, "Моя тестовая задача"));
        createEpic(new Epic(9, "Моя тестовая эпик-задача"));
        createSubtask(new Subtask(9,10, "Моя тестовая подзадача"));

        response = sendGetCommand("/size");
        int allSize = Integer.parseInt(response.body());
        Assertions.assertEquals( oldAllSize + 3, allSize);

        response = sendGetCommand("/task/size");
        int taskSize = Integer.parseInt(response.body());
        Assertions.assertEquals(oldTaskSize + 1, taskSize);

        response = sendGetCommand("/epic/size");
        int epicSize = Integer.parseInt(response.body());
        Assertions.assertEquals(oldEpicSize + 1, epicSize);

        response = sendGetCommand("/subtask/size");
        int subtaskSize = Integer.parseInt(response.body());
        Assertions.assertEquals(oldSubtaskSize + 1, subtaskSize);

        Assertions.assertEquals(taskSize+ epicSize+subtaskSize, allSize);
    }

    @Test
    void testGetHistory() throws IOException, InterruptedException {
        HttpResponse<String> response = sendDeleteCommand("/history");
        Assertions.assertEquals(405, response.statusCode());

        response = sendGetCommand("/history");
        final List<Double> history = gson.fromJson(response.body(), ArrayList.class);
        Assertions.assertEquals(6, history.size());
        Assertions.assertEquals(6, history.get(0));
        Assertions.assertEquals(0, history.get(3));
        Assertions.assertEquals(4, history.get(5));
    }

    HttpResponse<String> sendGetCommand(String command) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks" + command);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> sendDeleteCommand(String command) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks" + command);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    int createTask(Task task) throws IOException, InterruptedException {
        return createTaskBase("task/", task);
    }

    int createEpic(Epic epic) throws IOException, InterruptedException {
        return createTaskBase("epic/", epic);
    }

    int createSubtask(Subtask subtask) throws IOException, InterruptedException {
        return createTaskBase("subtask/", subtask);
    }

    int createTaskBase(String command, TaskBase taskBase) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + command);
        String json = gson.toJson(taskBase);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
    }

    int updateTask(Task task) throws IOException, InterruptedException {
        return updateTaskBase("task/", task);
    }

    int updateEpic(Epic epic) throws IOException, InterruptedException {
        return updateTaskBase("epic/", epic);
    }

    int updateSubtask(Subtask subtask) throws IOException, InterruptedException {
        return updateTaskBase("subtask/", subtask);
    }

    int updateTaskBase(String command, TaskBase taskBase) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + command);
        String json = gson.toJson(taskBase);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(body)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).statusCode();
    }


    Task getTask(int id) throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetCommand("/task/?id=" + id);
        return gson.fromJson(response.body(), Task.class);
    }

    Epic getEpic(int id) throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetCommand("/epic/?id=" + id);
        return gson.fromJson(response.body(), Epic.class);
    }

    Subtask getSubtask(int id) throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetCommand("/subtask/?id=" + id);
        return gson.fromJson(response.body(), Subtask.class);
    }

    int removeAll() throws IOException, InterruptedException {
        return sendDeleteCommand("").statusCode();
    }

    int removeAllTasks() throws IOException, InterruptedException {
        return sendDeleteCommand("/task/").statusCode();
    }

    int removeAllEpics() throws IOException, InterruptedException {
        return sendDeleteCommand("/epic/").statusCode();
    }

    int removeAllSubtasks() throws IOException, InterruptedException {
        return sendDeleteCommand("/subtask/").statusCode();
    }

    int removeTask(int id) throws IOException, InterruptedException {
        HttpResponse<String> response = sendDeleteCommand("/task/?id=" + id);
        return response.statusCode();
    }

    int removeEpic(int id) throws IOException, InterruptedException {
        HttpResponse<String> response = sendDeleteCommand("/epic/?id=" + id);
        return response.statusCode();
    }

    int removeSubtask(int id) throws IOException, InterruptedException {
        HttpResponse<String> response = sendDeleteCommand("/subtask/?id=" + id);
        return response.statusCode();
    }
}