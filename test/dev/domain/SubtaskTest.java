package dev.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubtaskTest extends TaskTestAbstract<Subtask> {

    @Override
    @BeforeEach
    void beforeEach() {
        task1 = new Subtask(0, TASK_ID_1, TASK_NAME_1, TASK_DESCRIPTION_1,
                TaskStatusEnum.NEW, 2022, 7, 1, 12, 0, 0);
        task2 = new Subtask(0, TASK_ID_2, TASK_NAME_2, TASK_DESCRIPTION_2);
        new Subtask(0, 3, "Задача 3");
    }

    @Test
    void testToStringWithSeparator() {
        Assertions.assertEquals("0|SUBTASK|Задание 1|1|1656666000000|0|Это задание 1|0\n",
                task1.toString("|"));
    }

    @Test
    void testToString() {
        Assertions.assertEquals("Subtask{name='Задание 1', description='Это задание 1', " +
                        "taskId=0', status=Задача только создана, startTime=2022-07-01T12:00, duration=0, " +
                        "endTime=2022-07-01T12:00, epicTaskId=0}",
                task1.toString());
    }

    @Override
    @Test
    void testHashCode() {
        Assertions.assertTrue(task1.hashCode()!=0);
    }
}