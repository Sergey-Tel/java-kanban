package dev.service;

import dev.domain.Epic;
import dev.service.server.HttpTaskManager;
import dev.service.server.Managers;
import dev.utils.KVServer;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;

class HttpTaskManagerTest extends TaskManagerTestAbstract<HttpTaskManager> {

    @BeforeAll
    public static void initServer() {
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
    void load() throws IOException {
        int size = manager.allSize();
        Assertions.assertNotEquals(0, size);
        manager.save();
        manager = Managers.setHttpTaskManager("http://localhost", KVServer.PORT);
        Assertions.assertEquals(0, manager.allSize());
        try {
            manager.load();
        } catch (Exception ex) {
            assertNull(ex.getMessage());
        }
        Assertions.assertNotEquals(0, manager.allSize());
        Assertions.assertEquals(size, manager.allSize());
    }

    @Test
    void save() {
        try {
            manager.save();
            manager.load();
        } catch (Exception ex) {
            assertNull(ex.getMessage());
        }
    }

    @Override
    @BeforeEach
    void beforeEach() throws IOException {
        manager = Managers.setHttpTaskManager("http://localhost", KVServer.PORT);
        manager.createTask("Первая задача!");
        Epic epic = manager.createEpic("Эпик-задача");
        manager.createSubtask(epic.getTaskId(), "Подзадача 1");
        manager.createSubtask(epic.getTaskId(), "Подзадача 2");
    }
}