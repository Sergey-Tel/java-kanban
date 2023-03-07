package dev.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

class TaskAbstractTest {

    public static final int ONE_MINUTE_MILLIS = 60_000;
    private static final int TASK_ID_1 = 0;
    private static final int TASK_ID_2 = 1;
    private static final String TASK_NAME_1 = "Задание 1";
    private static final String TASK_NAME_2 = "Задание 2";
    private static final String TASK_DESCRIPTION_1 = "Это задание 1";
    private static final String TASK_DESCRIPTION_2 = "Это задание 2";

    private static final Optional<Instant> START_TIME = Optional.of(LocalDateTime.of(2022, 7, 1, 12, 0)
            .atZone(ZoneId.systemDefault()).toInstant());

    static Task task1;
    static Task task2;

    @BeforeAll
    static void beforeAll() {
        task1 = new Task(TASK_ID_1, TASK_NAME_1, TASK_DESCRIPTION_1);
        task1 = (Task) task1.clone(START_TIME);
        task2 = new Task(TASK_ID_2, TASK_NAME_2, TASK_DESCRIPTION_2);
        task2 = (Task) task2.clone(START_TIME);
        new Task(2, "Задание 3");
    }

    @Test
    void getTaskId() {
        Task cloneTask = (Task) task1.clone();
        Assertions.assertEquals(TASK_ID_1, cloneTask.getTaskId());
    }

    @Test
    void getName() {
        Task cloneTask = (Task) task1.clone();
        Assertions.assertEquals(TASK_NAME_1, cloneTask.getName());
    }

    @Test
    void getDescription() {
        Task cloneTask = (Task) task1.clone();
        Assertions.assertEquals(TASK_DESCRIPTION_1, cloneTask.getDescription());
    }

    @Test
    void getStatus() {
        Task cloneTask = (Task) task1.clone();
        Assertions.assertEquals(TaskStatusEnum.NEW, cloneTask.getStatus());
        cloneTask = (Task) cloneTask.clone(TaskStatusEnum.IN_PROGRESS);
        Assertions.assertEquals(TaskStatusEnum.IN_PROGRESS, cloneTask.getStatus());
    }

    @Test
    void getStartTime() {
        Task cloneTask = (Task) task1.clone(START_TIME);
        Assertions.assertEquals(START_TIME, cloneTask.getStartTime());
    }

    @Test
    void getDuration() {
        Task cloneTask = (Task) task1.clone(40);
        Assertions.assertEquals(40, cloneTask.getDuration());
    }

    @Test
    void getEndTime() {
        Task cloneTask = (Task) task1.clone(START_TIME);
        cloneTask = (Task) cloneTask.clone(33);
        Optional<Instant> endTime = Optional.of(START_TIME.get().plusMillis(33 * ONE_MINUTE_MILLIS));
        Assertions.assertEquals(endTime, cloneTask.getEndTime());
    }

    @Test
    void testToString() {
        Assertions.assertEquals("Task{name='Задание 1', description='Это задание 1', taskId=0', " +
                        "status=Задача только создана, startTime=2022-07-01T12:00, duration=0, endTime=2022-07-01T12:00}",
                task1.toString());
    }

    @Test
    void testClone() {
        Task cloneTask = (Task) task1.clone();
        Assertions.assertNotNull(cloneTask);
        Assertions.assertTrue(task1.equals(cloneTask));
    }

    @Test
    void testEquals() {
        Assertions.assertTrue(task1.equals(task1));
        Assertions.assertTrue(task2.equals(task2));
        Assertions.assertFalse(task1.equals(task2));
    }

    @Test
    void compareTo() {
        Task cloneTask1;
        Task cloneTask2;

        cloneTask1 = (Task) task1.clone(2022, 1, 1, 12, 0);
        cloneTask2 = (Task) task2.clone(Optional.empty());
        Assertions.assertEquals(-1, cloneTask1.compareTo(cloneTask2));

        cloneTask1 = (Task) task1.clone(Optional.empty());
        cloneTask2 = (Task) task2.clone(2022, 1, 1, 12, 0);
        Assertions.assertEquals(1, cloneTask1.compareTo(cloneTask2));

        cloneTask1 = (Task) task1.clone(2022, 1, 1, 12, 0);
        cloneTask2 = (Task) task2.clone(2022, 1, 1, 12, 0);
        Assertions.assertEquals(0, cloneTask1.compareTo(cloneTask2));

        cloneTask1 = (Task) task1.clone(2022, 1, 1, 12, 0);
        cloneTask2 = (Task) task2.clone(2022, 1, 1, 12, 1);
        Assertions.assertEquals(-1, cloneTask1.compareTo(cloneTask2));

        cloneTask1 = (Task) task1.clone(2022, 1, 1, 12, 1);
        cloneTask2 = (Task) task2.clone(2022, 1, 1, 12, 0);
        Assertions.assertEquals(1, cloneTask1.compareTo(cloneTask2));
    }

    @Test
    void compareToEndTime() {
        Task cloneTask1 = (Task) task1.clone(Optional.empty());
        cloneTask1 = (Task)cloneTask1.clone(TaskStatusEnum.NEW);
        Task cloneTask2 = (Task) task2.clone(Optional.empty());
        cloneTask2 = (Task)cloneTask2.clone(TaskStatusEnum.NEW);
        Assertions.assertEquals(0, cloneTask1.compareToEndTime(cloneTask2));

        cloneTask1 = (Task) cloneTask1.clone(Optional.empty());
        cloneTask1 = (Task)cloneTask1.clone(TaskStatusEnum.DONE);
        cloneTask2 = (Task) cloneTask2.clone(Optional.empty());
        cloneTask2 = (Task)cloneTask2.clone(TaskStatusEnum.IN_PROGRESS);
        Assertions.assertEquals(1, cloneTask1.compareToEndTime(cloneTask2));
        cloneTask1 = (Task) cloneTask1.clone(Optional.empty());
        cloneTask1 = (Task)cloneTask1.clone(TaskStatusEnum.IN_PROGRESS);
        cloneTask2 = (Task) cloneTask2.clone(Optional.empty());
        cloneTask2 = (Task)cloneTask2.clone(TaskStatusEnum.DONE);
        Assertions.assertEquals(-1, cloneTask1.compareToEndTime(cloneTask2));

        cloneTask1 = (Task) task1.clone(Optional.empty());
        cloneTask1 = (Task)cloneTask1.clone(TaskStatusEnum.DONE);
        cloneTask2 = (Task) task2.clone(2022, 1, 1, 12, 0);
        Assertions.assertEquals(1, cloneTask1.compareToEndTime(cloneTask2));
        cloneTask1 = (Task) task1.clone(2022, 1, 1, 12, 0);
        cloneTask2 = (Task) task2.clone(Optional.empty());
        cloneTask2 = (Task)cloneTask2.clone(TaskStatusEnum.DONE);
        Assertions.assertEquals(-1, cloneTask1.compareToEndTime(cloneTask2));
        cloneTask1 = (Task) task1.clone(2022, 1, 1, 12, 0);
        cloneTask1 = (Task)cloneTask1.clone(TaskStatusEnum.DONE);
        cloneTask2 = (Task) task2.clone(Optional.empty());
        cloneTask2 = (Task)cloneTask2.clone(TaskStatusEnum.DONE);
        Assertions.assertEquals(-1, cloneTask1.compareToEndTime(cloneTask2));

        cloneTask1 = (Task) task1.clone(2022, 1, 1, 12, 0);
        cloneTask1 = (Task)cloneTask1.clone(TaskStatusEnum.NEW);
        cloneTask2 = (Task) task2.clone(2022, 1, 1, 12, 0);
        cloneTask2 = (Task)cloneTask2.clone(TaskStatusEnum.NEW);
        Assertions.assertEquals(0, cloneTask1.compareToEndTime(cloneTask2));

        cloneTask1 = (Task) task1.clone(2022, 1, 1, 12, 0);
        cloneTask1 = (Task)cloneTask1.clone(TaskStatusEnum.DONE);
        cloneTask2 = (Task) task2.clone(2022, 1, 1, 12, 0);
        cloneTask2 = (Task)cloneTask2.clone(TaskStatusEnum.IN_PROGRESS);
        Assertions.assertEquals(1, cloneTask1.compareToEndTime(cloneTask2));
        cloneTask1 = (Task) task1.clone(2022, 1, 1, 12, 0);
        cloneTask1 = (Task)cloneTask1.clone(TaskStatusEnum.IN_PROGRESS);
        cloneTask2 = (Task) task2.clone(2022, 1, 1, 12, 0);
        cloneTask2 = (Task)cloneTask2.clone(TaskStatusEnum.DONE);
        Assertions.assertEquals(-1, cloneTask1.compareToEndTime(cloneTask2));

        cloneTask1 = (Task) task1.clone(2022, 1, 1, 12, 1);
        cloneTask1 = (Task)cloneTask1.clone(TaskStatusEnum.NEW);
        cloneTask2 = (Task) task2.clone(2022, 1, 1, 12, 0);
        cloneTask2 = (Task)cloneTask2.clone(TaskStatusEnum.NEW);
        Assertions.assertEquals(1, cloneTask1.compareToEndTime(cloneTask2));
        cloneTask1 = (Task) task1.clone(2022, 1, 1, 12, 0);
        cloneTask1 = (Task)cloneTask1.clone(TaskStatusEnum.NEW);
        cloneTask2 = (Task) task2.clone(2022, 1, 1, 12, 1);
        cloneTask2 = (Task)cloneTask2.clone(TaskStatusEnum.NEW);
        Assertions.assertEquals(-1, cloneTask1.compareToEndTime(cloneTask2));

        cloneTask1 = (Task) task1.clone(2022, 1, 1, 12, 0);
        cloneTask1 = (Task)cloneTask1.clone(TaskStatusEnum.IN_PROGRESS);
        cloneTask2 = (Task) task2.clone(Optional.empty());
        cloneTask2 = (Task)cloneTask2.clone(TaskStatusEnum.IN_PROGRESS);
        Assertions.assertEquals(1, cloneTask1.compareToEndTime(cloneTask2));
        cloneTask1 = (Task) task1.clone(Optional.empty());
        cloneTask1 = (Task)cloneTask1.clone(TaskStatusEnum.IN_PROGRESS);
        cloneTask2 = (Task) task2.clone(2022, 1, 1, 12, 0);
        cloneTask2 = (Task)cloneTask2.clone(TaskStatusEnum.IN_PROGRESS);
        Assertions.assertEquals(-1, cloneTask1.compareToEndTime(cloneTask2));
    }
}