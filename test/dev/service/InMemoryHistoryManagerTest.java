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
        // ТЗ №7: a. Пустая история задач.
        manager.removeAll();
        assertEquals(0, manager.getHistoryManager().getHistory().size());

        // ТЗ №7: b. Дублирование.
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

        // ТЗ №7: с. Удаление из истории: начало, середина, конец.

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
        // ТЗ №7: a. Пустая история задач.
        manager.removeAllTasks();
        assertEquals(3, manager.getHistoryManager().getHistory().size());
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    void removeEpics() {
        // ТЗ №7: a. Пустая история эпик-задач.
        manager.removeAllEpics();
        assertEquals(1, manager.getHistoryManager().getHistory().size());
        assertEquals(0, manager.getEpics().size());
    }

    @Test
    void removeSubtasks() {
        // ТЗ №7: a. Пустая история подзадач.
        manager.removeAllSubtasks();
        assertEquals(2, manager.getHistoryManager().getHistory().size());
        assertEquals(0, manager.getSubtasks().size());
    }
}