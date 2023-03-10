package dev.service;

import dev.domain.Epic;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

class InMemoryTasksManagerTest extends TaskManagerTestAbstract<InMemoryTasksManager> {

    @Override
    @BeforeEach
    void beforeEach() throws IOException {
        manager = Managers.setMemoryTasksManager();
        manager.removeAll();
        manager.createTask("Первая задача!");
        Epic epic = manager.createEpic("Эпик-задача");
        manager.createSubtask(epic.getTaskId(), "Подзадача 1");
        manager.createSubtask(epic.getTaskId(), "Подзадача 2");
    }
}