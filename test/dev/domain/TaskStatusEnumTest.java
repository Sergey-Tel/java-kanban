package dev.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TaskStatusEnumTest {

    @Test
    void printCommands() {
        try {
            TaskStatusEnum.printCommands();
        } catch (Exception ex) {
            assertNull(ex.getMessage());
        }
    }

    @Test
    void compareEnum() {
        TaskStatusEnum status = null;
        status = TaskStatusEnum.compareEnum(status, null);
        assertEquals(null, status);

        status = TaskStatusEnum.compareEnum(status, TaskStatusEnum.NEW);
        assertEquals(TaskStatusEnum.NEW, status);

        status = TaskStatusEnum.compareEnum(status, null);
        assertEquals(TaskStatusEnum.NEW, status);

        status = TaskStatusEnum.compareEnum(status, TaskStatusEnum.NEW);
        assertEquals(TaskStatusEnum.NEW, status);

        status = TaskStatusEnum.compareEnum(status, TaskStatusEnum.IN_PROGRESS);
        assertEquals(TaskStatusEnum.IN_PROGRESS, status);

        status = TaskStatusEnum.compareEnum(status, TaskStatusEnum.DONE);
        assertEquals(TaskStatusEnum.IN_PROGRESS, status);

        status = TaskStatusEnum.DONE;
        status = TaskStatusEnum.compareEnum(status, TaskStatusEnum.DONE);
        assertEquals(TaskStatusEnum.DONE, status);

    }

    @Test
    void fromKey() {
        assertEquals(TaskStatusEnum.IN_PROGRESS, TaskStatusEnum.fromKey("2"));
        assertEquals(TaskStatusEnum.NEW, TaskStatusEnum.fromKey("99"));
    }
}