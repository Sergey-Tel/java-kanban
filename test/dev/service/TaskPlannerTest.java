package dev.service;

import dev.domain.Task;
import dev.service.manager.InMemoryTasksManager;
import dev.service.manager.TasksManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

class TaskPlannerTest {

    static TaskPlanner planner;
    static TasksManager manager;

    private static Optional<Instant> getTime(int year, int month, int day, int hours, int minutes) {
        return Optional.of(LocalDateTime
                .of(year, month, day, hours, minutes)
                .atZone(ZoneId.systemDefault()).toInstant());

    }

    @BeforeEach
    void beforeEach() {
        manager = new InMemoryTasksManager();
        planner = new TaskPlanner();
    }

    @Test
    void addTask() {
        Task firstTask = manager.createTask("Первое задание");
        firstTask = (Task) firstTask.clone(35);
        firstTask = (Task) firstTask.clone(getTime(2022, 7, 1, 12, 0));
        planner.add(firstTask);
        Assertions.assertTrue(planner.contains(firstTask));
    }

    @Test
    void removeTask() {
        Task task = manager.createTask("Задание 1");
        task = (Task) task.clone(32);
        task = (Task) task.clone(getTime(1970, 1, 1, 3, 1));
        planner.add(task);

        Assertions.assertTrue(planner.contains(task));
        planner.remove(task);
        Assertions.assertFalse(planner.contains(task));
    }

    @Test
    void containsDate() {
        Task firstTask = manager.createTask("Первое задание (3-7)");
        firstTask = (Task) firstTask.clone(4);
        firstTask = (Task) firstTask.clone(getTime(2000, 1, 1, 12, 3));
        planner.add(firstTask);
        Assertions.assertFalse(planner.containsDate(firstTask)); // Нельзя самому себя определять!

        Task secondTask = manager.createTask("Проверочное задание 1 (3-7) и (3-7)");
        secondTask = (Task) secondTask.clone(4);
        secondTask = (Task) secondTask.clone(getTime(2000, 1, 1, 12, 3));
        Assertions.assertTrue(planner.containsDate(secondTask));

        secondTask = manager.createTask("Проверочное задание 2 (3-7) и (3-5)");
        secondTask = (Task) secondTask.clone(3);
        secondTask = (Task) secondTask.clone(getTime(2000, 1, 1, 12, 3));
        Assertions.assertTrue(planner.containsDate(secondTask));

        secondTask = manager.createTask("Проверочное задание 3 (3-7) и (4-6)");
        secondTask = (Task) secondTask.clone(3);
        secondTask = (Task) secondTask.clone(getTime(2000, 1, 1, 12, 4));
        Assertions.assertTrue(planner.containsDate(secondTask));

        secondTask = manager.createTask("Проверочное задание 4 (3-7) и (2-5)");
        secondTask = (Task) secondTask.clone(4);
        secondTask = (Task) secondTask.clone(getTime(2000, 1, 1, 12, 2));
        Assertions.assertTrue(planner.containsDate(secondTask));

        secondTask = manager.createTask("Проверочное задание 5 (3-7) и (4-7)");
        secondTask = (Task) secondTask.clone(4);
        secondTask = (Task) secondTask.clone(getTime(2000, 1, 1, 12, 4));
        Assertions.assertTrue(planner.containsDate(secondTask));

        secondTask = manager.createTask("Проверочное задание 6 (3-7) и (2-7)");
        secondTask = (Task) secondTask.clone(6);
        secondTask = (Task) secondTask.clone(getTime(2000, 1, 1, 12, 2));
        Assertions.assertTrue(planner.containsDate(secondTask));

        secondTask = manager.createTask("Проверочное задание 7 (3-7) и (4-5)");
        secondTask = (Task) secondTask.clone(2);
        secondTask = (Task) secondTask.clone(getTime(2000, 1, 1, 12, 4));
        Assertions.assertTrue(planner.containsDate(secondTask));

        secondTask = manager.createTask("Проверочное задание 8 (3-7) и (1-2)");
        secondTask = (Task) secondTask.clone(2);
        secondTask = (Task) secondTask.clone(getTime(2000, 1, 1, 12, 1));
        Assertions.assertFalse(planner.containsDate(secondTask));

        secondTask = manager.createTask("Проверочное задание 9 (3-7) и (7-8)");
        secondTask = (Task) secondTask.clone(2);
        secondTask = (Task) secondTask.clone(getTime(2000, 1, 1, 12, 7));
        Assertions.assertFalse(planner.containsDate(secondTask));
    }

    @Test
    void contains() {
        Task firstTask = manager.createTask("Первое задание");
        firstTask = (Task) firstTask.clone(35);
        firstTask = (Task) firstTask.clone(getTime(2022, 7, 1, 12, 0));
        planner.add(firstTask);
        Assertions.assertTrue(planner.contains(firstTask));

        Task secondTask = manager.createTask("Второе задание");
        secondTask = (Task) secondTask.clone(35);
        secondTask = (Task) secondTask.clone(getTime(2022, 7, 1, 12, 0));
        Assertions.assertFalse(planner.contains(secondTask));
    }

    @Test
    void getCurrentTask() {
        Task firstTask = manager.createTask("Первое задание");
        firstTask = (Task) firstTask.clone(35);
        firstTask = (Task) firstTask.clone(getTime(2022, 7, 1, 12, 0));
        planner.add(firstTask);

        Task secondTask = manager.createTask("Второе задание");
        secondTask = (Task) secondTask.clone(35);
        secondTask = (Task) secondTask.clone(getTime(2022, 7, 1, 12, 0));
        Assertions.assertEquals(firstTask, planner.getCurrentTask(secondTask).get());

        Task thirdTask = manager.createTask("Третье задание");
        thirdTask = (Task) thirdTask.clone(35);
        thirdTask = (Task) thirdTask.clone(getTime(2020, 7, 1, 12, 0));
        Assertions.assertTrue(planner.getCurrentTask(thirdTask).isEmpty());
    }

    @Test
    void clear() {
        Task firstTask = manager.createTask("Первое задание");
        firstTask = (Task) firstTask.clone(35);
        firstTask = (Task) firstTask.clone(getTime(2022, 7, 1, 12, 0));

        planner.add(firstTask);
        Assertions.assertTrue(planner.contains(firstTask));

        planner.clear();
        Assertions.assertFalse(planner.contains(firstTask));
    }
}