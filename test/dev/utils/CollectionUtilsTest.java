package dev.utils;

import dev.domain.Task;
import dev.domain.TaskStatusEnum;
import dev.service.Managers;
import dev.service.TasksManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class CollectionUtilsTest {

    @Test
    void getNextTaskId() throws IOException {
        Managers.setMemoryTasksManager();
        TasksManager manager = Managers.getDefault();
        int nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Assertions.assertEquals(0, nextTaskId);

        Task shoppingTask = new Task(nextTaskId, "Купить батарейки!", "Необходимо 4 шт.",
                TaskStatusEnum.NEW, 2020, 1, 1, 18, 5, 45);
        manager.create(shoppingTask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Assertions.assertEquals(1, nextTaskId);
    }
}