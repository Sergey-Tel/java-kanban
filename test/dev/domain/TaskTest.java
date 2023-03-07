package dev.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskTest extends TaskTestAbstract<Task> {

    @Override
    @BeforeEach
    void beforeEach() {
        task1 = new Task(TASK_ID_1, TASK_NAME_1, TASK_DESCRIPTION_1);
        task1 = (Task) task1.clone(START_TIME);
        task2 = new Task(TASK_ID_2, TASK_NAME_2, TASK_DESCRIPTION_2, TaskStatusEnum.IN_PROGRESS,
                2022, 7, 1, 12, 0, 45);
    }

    @Override
    @Test
    void testToString() {
        Assertions.assertEquals("Task{name='Задание 1', description='Это задание 1', taskId=0', " +
                        "status=Задача только создана, startTime=2022-07-01T12:00, duration=0, " +
                        "endTime=2022-07-01T12:00}",
                task1.toString());
    }

    @Override
    @Test
    void testToStringWithSeparator() {
        Assertions.assertEquals("0|TASK|Задание 1|1|1656666000000|0|Это задание 1|0\n", task1.toString("|"));
    }

    @Override
    @Test
    void testHashCode() {
        Assertions.assertTrue(task1.hashCode() != 0);
    }
}