package dev.service;

import dev.domain.Epic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    static InMemoryTasksManager manager;

    @BeforeAll
    static void beforeAll() {
        Managers.tasksManager = null;
        manager =(InMemoryTasksManager) Managers.getDefault();

    }

    @BeforeEach
    void beforeEach() {
        manager.removeAll();
        manager.createTask("Первая задача!");
        Epic epic = manager.createEpic("Эпик-задача");
        manager.createSubtask(epic.getTaskId(), "Подзадача 1");
        manager.createSubtask(epic.getTaskId(), "Подзадача 2");

        manager.getTaskBase(0);
        manager.getTaskBase(1);
        manager.getTaskBase(2);
        manager.getTaskBase(3);
        manager.getTaskBase(3);
        manager.getTaskBase(2);
    }

    @Test
    void testHistoryManager() {

        manager.removeAll();
        assertEquals(0, manager.getHistoryManager().getHistory().size());


        manager.createTask("Первая задача!");
        Epic epic = manager.createEpic("Эпик-задача");
        manager.createSubtask(epic.getTaskId(), "Подзадача 1");
        manager.createSubtask(epic.getTaskId(), "Подзадача 2");
        assertEquals(2, manager.getHistoryManager().getHistory().size());

        manager.getTaskBase(0);
        manager.getTaskBase(1);
        manager.getTaskBase(2);
        manager.getTaskBase(3);
        manager.getTaskBase(3);
        manager.getTaskBase(2);
        assertEquals(4, manager.getHistoryManager().getHistory().size());

        assertEquals(0, manager.getHistoryManager().getFirst().getTaskId());
        assertEquals(2, manager.getHistoryManager().getLast().getTaskId());



        manager.removeTask(0);
        assertEquals(3, manager.getHistoryManager().getHistory().size());
        assertEquals(1, manager.getHistoryManager().getFirst().getTaskId());

        manager.removeTask(2);
        assertEquals(2, manager.getHistoryManager().getHistory().size());
        assertEquals(1, manager.getHistoryManager().getFirst().getTaskId());
        assertEquals(3, manager.getHistoryManager().getLast().getTaskId());

        manager.removeTask(3);
        assertEquals(1, manager.getHistoryManager().getHistory().size());
        assertEquals(1, manager.getHistoryManager().getFirst().getTaskId());
        assertEquals(1, manager.getHistoryManager().getLast().getTaskId());
    }

    @Test
    void removeTasks() {

        manager.removeAllTasks();
        assertEquals(3, manager.getHistoryManager().getHistory().size());
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    void removeEpics() {

        manager.removeAllEpics();
        assertEquals(1, manager.getHistoryManager().getHistory().size());
        assertEquals(0, manager.getEpics().size());
    }

    @Test
    void removeSubtasks() {

        manager.removeAllSubtasks();
        assertEquals(2, manager.getHistoryManager().getHistory().size());
        assertEquals(0, manager.getSubtasks().size());
    }
}