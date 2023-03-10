package dev.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public abstract class TaskTestAbstract<T extends Task> {

    static final int TASK_ID_1 = 0;
    static final int TASK_ID_2 = 1;
    static final String TASK_NAME_1 = "Задание 1";
    static final String TASK_NAME_2 = "Задание 2";
    static final String TASK_DESCRIPTION_1 = "Это задание 1";
    static final String TASK_DESCRIPTION_2 = "Это задание 2";

    static final Optional<Instant> START_TIME = Optional.of(LocalDateTime.of(2022, 7, 1, 12, 0)
            .atZone(ZoneId.systemDefault()).toInstant());
    T task1;
    T task2;

    @BeforeEach
    abstract void beforeEach() ;

    @Test
    abstract void testToString();

    @Test
    abstract void testToStringWithSeparator();

    @Test
    abstract void testHashCode() ;

    @Test
    void testClone() {
        T cloneTask =(T) task1.clone();
        Assertions.assertNotNull(cloneTask);
        Assertions.assertEquals(task1, cloneTask);
    }

    @Test
    void testCloneWithNameAndDescription() {
        T cloneTask =(T) task1.clone(TASK_NAME_2, TASK_DESCRIPTION_2);
        Assertions.assertNotEquals(task1, cloneTask);
        Assertions.assertEquals(TASK_NAME_2, cloneTask.getName());
        Assertions.assertEquals(TASK_DESCRIPTION_2, cloneTask.getDescription());
        Assertions.assertNotEquals(task1.getName(), cloneTask.getName());
        Assertions.assertNotEquals(task1.getDescription(), cloneTask.getDescription());
    }

    @Test
    void testCloneWithStatus() {
        T cloneTask =(T) task1.clone(TaskStatusEnum.DONE);
        Assertions.assertNotEquals(task1, cloneTask);
        Assertions.assertEquals(TaskStatusEnum.DONE, cloneTask.getStatus());
        Assertions.assertNotEquals(task1.getStatus(), cloneTask.getStatus());
    }

    @Test
    void testCloneWithNameDescriptionAndStatus() {
        T cloneTask =(T) task1.clone(TASK_NAME_2, TASK_DESCRIPTION_2, TaskStatusEnum.DONE);
        Assertions.assertNotEquals(task1, cloneTask);
        Assertions.assertEquals(TASK_NAME_2, cloneTask.getName());
        Assertions.assertEquals(TASK_DESCRIPTION_2, cloneTask.getDescription());
        Assertions.assertEquals(TaskStatusEnum.DONE, cloneTask.getStatus());
        Assertions.assertNotEquals(task1.getName(), cloneTask.getName());
        Assertions.assertNotEquals(task1.getDescription(), cloneTask.getDescription());
        Assertions.assertNotEquals(task1.getStatus(), cloneTask.getStatus());
    }

    @Test
    void testCloneWithDuration() {
        T cloneTask =(T) task1.clone(40);
        Assertions.assertNotEquals(task1, cloneTask);
        Assertions.assertEquals(40, cloneTask.getDuration());
    }

    @Test
    void testCloneWithStartTime() {
        Optional<Instant> startTime = Optional.of(LocalDateTime.of(2022, 5, 9, 22, 0)
                .atZone(ZoneId.systemDefault()).toInstant());
        T cloneTask =(T) task1.clone(startTime);
        Assertions.assertNotEquals(task1, cloneTask);
        Assertions.assertEquals(startTime, cloneTask.getStartTime());
        Assertions.assertNotEquals(task1.getStartTime(), cloneTask.getStartTime());
    }

    @Test
    void testCloneWithFullStartTime() {
        Optional<Instant> startTime = Optional.of(LocalDateTime.of(2022, 5, 9, 22, 0)
                .atZone(ZoneId.systemDefault()).toInstant());
        T cloneTask =(T) task1.clone(2022,5,9,22,0);
        Assertions.assertNotEquals(task1, cloneTask);
        Assertions.assertEquals(startTime, cloneTask.getStartTime());
        Assertions.assertNotEquals(task1.getStartTime(), cloneTask.getStartTime());
    }

    @Test
    void testCloneWithStatusAndFullStartTime() {
        Optional<Instant> startTime = Optional.of(LocalDateTime.of(2022, 5, 9, 22, 0)
                .atZone(ZoneId.systemDefault()).toInstant());
        T cloneTask =(T) task1.clone(TaskStatusEnum.IN_PROGRESS,2022,5,9,22,0);
        Assertions.assertNotEquals(task1, cloneTask);
        Assertions.assertEquals(TaskStatusEnum.IN_PROGRESS, cloneTask.getStatus());
        Assertions.assertEquals(startTime, cloneTask.getStartTime());
        Assertions.assertNotEquals(task1.getStatus(), cloneTask.getStatus());
        Assertions.assertNotEquals(task1.getStartTime(), cloneTask.getStartTime());
    }

    @Test
    void testEquals() {
        Assertions.assertEquals(task1, task1);
        Assertions.assertEquals(task2, task2);
        Assertions.assertNotEquals(task1, task2);
    }
}
