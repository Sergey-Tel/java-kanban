package dev.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

class TaskComparatorTest {
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
    }

    @Test
    void compare() {
        task1 = (Task) task1.clone(2022, 1, 1, 12, 0);
        task2 = (Task) task2.clone(Optional.empty());
        Assertions.assertEquals(-1, new TaskComparator().compare(task1, task2));

        task1 = (Task) task1.clone(Optional.empty());
        task2 = (Task) task2.clone(2022, 1, 1, 12, 0);
        Assertions.assertEquals(1, new TaskComparator().compare(task1, task2));

        task1 = (Task) task1.clone(2022, 1, 1, 12, 0);
        task2 = (Task) task2.clone(2022, 1, 1, 12, 0);
        Assertions.assertEquals(0, new TaskComparator().compare(task1, task2));

        task1 = (Task) task1.clone(2022, 1, 1, 12, 0);
        task2 = (Task) task2.clone(2022, 1, 1, 12, 1);
        Assertions.assertEquals(-1, new TaskComparator().compare(task1, task2));

        task1 = (Task) task1.clone(2022, 1, 1, 12, 1);
        task2 = (Task) task2.clone(2022, 1, 1, 12, 0);
        Assertions.assertEquals(1, new TaskComparator().compare(task1, task2));
    }
}