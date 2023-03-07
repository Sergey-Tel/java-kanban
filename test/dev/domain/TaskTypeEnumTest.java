package dev.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTypeEnumTest {

    @Test
    void fromName() {
        assertEquals(TaskTypeEnum.TASK, TaskTypeEnum.fromName("TASK"));
        assertEquals(TaskTypeEnum.EPIC, TaskTypeEnum.fromName("EPIC"));
        assertEquals(TaskTypeEnum.SUBTASK, TaskTypeEnum.fromName("SUBTASK"));
        assertEquals(TaskTypeEnum.TASK, TaskTypeEnum.fromName("ERR"));
    }
}