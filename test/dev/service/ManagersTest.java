package dev.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNull;

class ManagersTest {
    static final Path path = FileSystems.getDefault().getPath("java-kanban-managers-test.csv");

    @BeforeAll
    static void beforeAll() {
        Managers.tasksManager = null;
        try {
            path.toFile().createNewFile();
        } catch (IOException ex) {
            assertNull(ex.getMessage());
        }
    }

    @Test
    void setFileTasksManager() {
        Managers.setFileTasksManager(path.toFile());
        Assertions.assertTrue(Managers.getDefault() instanceof FileBackedTasksManager);
    }

    @Test
    void setMemoryTasksManager() {
        Managers.setMemoryTasksManager();
        Assertions.assertTrue(Managers.getDefault() instanceof InMemoryTasksManager);
    }

    @Test
    void getDefault() {
        Assertions.assertNotNull(Managers.getDefault());
    }
}