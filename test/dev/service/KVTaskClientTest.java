package dev.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.domain.Task;
import dev.domain.TaskBase;
import dev.utils.KVServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;

class KVTaskClientTest {
    public static final String KEY = "ALIAS";
    static Gson gson;
    static KVTaskClient client;

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

    @BeforeEach
    void beforeEach() {
        client = new KVTaskClient("http://localhost", 8078);
        client.register();
    }

    @Test
    void testSaveAndLoad() throws IOException, InterruptedException {
        Task task = new Task(0, "Мое задание");
        String outString = gson.toJson(task);
        client.put(KEY, outString);
        Optional<String> inputString = client.load(KEY);
        Assertions.assertEquals(outString, inputString.get());
        inputString = client.load(KEY+"_err");
        Assertions.assertTrue(inputString.isEmpty());
    }

    @Test
    void register() {
        try {
            client = new KVTaskClient("http://err:", 8078);
            client.register();
        } catch (Exception ex) {
            Assertions.assertNotNull(ex.getMessage());
        }
        try {
            client = new KVTaskClient("http://localhost", 8078);
            client.register();
        } catch (Exception ex) {
            Assertions.assertNull(ex.getMessage());
        }
    }
}