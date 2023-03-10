package dev.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.domain.Task;
import dev.domain.TaskBase;
import dev.service.Managers;
import dev.service.TaskAdapter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class KVServerTest {
    private static final String URL = "http://localhost:8078";
    private final HttpClient client = HttpClient.newHttpClient();
    private static Gson gson;

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
        } catch (Exception ex) {
            assertNull(ex.getMessage());
        }
    }

    @Test
    void hasAuth() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest("/register");
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertNotNull(jsonElement.getAsString());

        response = sendPostRequest("/register", "");
        assertEquals(405, response.statusCode());
    }

    @Test
    void readText() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest("/load/ALIAS?API_TOKEN=DEBUG");

        createTask();
        response = sendGetRequest("/load/ALIAS?API_TOKEN=DEBUG");
        assertEquals(200, response.statusCode());

        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals("Наименование задачи 1", task.getName());
        assertEquals("Пояснение к задаче.", task.getDescription());

        response = sendGetRequest("/load/TEST?API_TOKEN=DEBUG");
        assertEquals(404, response.statusCode());

        response = sendGetRequest("/load/ALIAS?API_TOKEN=TEST");
        assertEquals(403, response.statusCode());

        response = sendPostRequest("/load/ALIAS?API_TOKEN=TEST", "");
        assertEquals(405, response.statusCode());
    }

    @Test
    void sendText() throws IOException, InterruptedException {
        HttpResponse<String> response = sendPostRequest("/save/ALIAS?API_TOKEN=TEST",
                gson.toJson(new Task(0,"Наименование", "Пояснение")));
        assertEquals(403, response.statusCode());

        response = sendPostRequest("/save/?API_TOKEN=DEBUG",
                gson.toJson(new Task(0,"Наименование", "Пояснение")));
        assertEquals(400, response.statusCode());

        response = sendPostRequest("/save/ALIAS?API_TOKEN=DEBUG","");
        assertEquals(400, response.statusCode());

        response = sendGetRequest("/save/ALIAS?API_TOKEN=DEBUG");
        assertEquals(405, response.statusCode());

        response = sendPostRequest("/save/ALIAS?API_TOKEN=DEBUG",
                gson.toJson(new Task(0,"Наименование тестовой задачи", "Пояснение к тестовой задаче.")));
        assertEquals(200, response.statusCode());

        response = sendGetRequest("/load/ALIAS?API_TOKEN=DEBUG");
        assertEquals(200, response.statusCode());

        Task task = gson.fromJson(response.body(), Task.class);
        assertEquals("Наименование тестовой задачи", task.getName());
        assertEquals("Пояснение к тестовой задаче.", task.getDescription());
    }

    private void createTask() throws IOException, InterruptedException {
        HttpResponse<String> response = sendPostRequest("/save/ALIAS?API_TOKEN=DEBUG",
                gson.toJson(new Task(0,"Наименование задачи 1", "Пояснение к задаче.")));
    }

    private HttpResponse<String> sendGetRequest(String addingPath) throws IOException, InterruptedException {
        return client.send(HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(URL + addingPath))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPostRequest(String addingPath, String json) throws IOException, InterruptedException {
        return client.send(HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(URL + addingPath))
                .build(), HttpResponse.BodyHandlers.ofString());
    }
}