package dev.domain;

import dev.service.Managers;
import dev.utils.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static final int EPIC_ID_1 = 0;
    private static final int EPIC_ID_2 = 1;
    private static final String EPIC_NAME_1 = "Эпик-задание 1";
    private static final String EPIC_NAME_2 = "Эпик-задание 2";
    private static final String EPIC_DESCRIPTION_1 = "Это эпик-задание 1";

    private static final Optional<Instant> START_TIME = Optional.of(LocalDateTime.of(2022, 7, 1, 12, 0)
            .atZone(ZoneId.systemDefault()).toInstant());

    static Epic epic1;
    static Epic epic2;
    static Subtask subtask1_1;
    static Subtask subtask1_2;

    @BeforeAll
    static void beforeAll() {
        Managers.setMemoryTasksManager();
        epic1 = new Epic(EPIC_ID_1, EPIC_NAME_1, EPIC_DESCRIPTION_1);
        Managers.getDefault().create(epic1);
        epic2 = new Epic(EPIC_ID_2, EPIC_NAME_2);
        Managers.getDefault().create(epic2);

        subtask1_1 = new Subtask(EPIC_ID_1, 2, "Подзадача 1", "Это подзадача 1",
                TaskStatusEnum.DONE, START_TIME, 3);
        epic1.create(subtask1_1);

        subtask1_2 = new Subtask(EPIC_ID_1, 3, "Подзадача 2");
        subtask1_2 = (Subtask) subtask1_2.clone(5);
        epic1.create(subtask1_2);

        epic2.create(4, "Подзадача 2.1", "Это подзадача 2.1");
        epic2.create(5, "Подзадача 2.2");
    }

    @Test
    void create() {
        final IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                Subtask subtask = new Subtask(epic1.getTaskId(), 3, "Подзадача 4");
                epic1.create(subtask);
            }
        });
        assertEquals("Подзадача с идентификационным номером 3 уже присутствует в коллекции.", exception.getMessage());
    }

    @Test
    void update() {
        Subtask subtask = new Subtask(epic1.getTaskId(), 2, "Подзадача 2");
        epic1.update(subtask);

        final IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                Subtask subtask = new Subtask(epic1.getTaskId(), 77, "Подзадача 77");
                epic1.update(subtask);
            }
        });
        assertEquals("Подзадача с идентификационным номером 77 отсутствует в коллекции.", exception.getMessage());
    }

    @Test
    void updateStatus() {
        Epic epic = new Epic(CollectionUtils.getNextTaskId(Managers.getDefault().getAllTaskId()),
                "Эпик", "Это эпик");
        Managers.getDefault().create(epic);

        // ТЗ №7: a. Пустой список подзадач.
        assertEquals(0, epic.size());
        assertEquals(TaskStatusEnum.NEW, epic.getStatus());

        // ТЗ №7: b. Все подзадачи со статусом NEW.
        int taskId1 = CollectionUtils.getNextTaskId(Managers.getDefault().getAllTaskId());
        epic.create(taskId1, "Подзадача 1", "Это подзадача 1");
        int taskId2 = CollectionUtils.getNextTaskId(Managers.getDefault().getAllTaskId());
        epic.create(taskId2, "Подзадача 2");
        int taskId3 = CollectionUtils.getNextTaskId(Managers.getDefault().getAllTaskId());
        epic.create(taskId3, "Подзадача 3");
        assertEquals(TaskStatusEnum.NEW, epic.getStatus());

        // ТЗ №7: c. Все подзадачи со статусом DONE.
        epic.update((Subtask) epic.getSubtask(taskId1).clone(TaskStatusEnum.DONE));
        epic.update((Subtask) epic.getSubtask(taskId2).clone(TaskStatusEnum.DONE));
        epic.update((Subtask) epic.getSubtask(taskId3).clone(TaskStatusEnum.DONE));
        assertEquals(TaskStatusEnum.DONE, epic.getStatus());

        // ТЗ №7: d. Подзадачи со статусами NEW и DONE.
        epic.update((Subtask) epic.getSubtask(taskId1).clone(TaskStatusEnum.DONE));
        epic.update((Subtask) epic.getSubtask(taskId2).clone(TaskStatusEnum.NEW));
        epic.update((Subtask) epic.getSubtask(taskId3).clone(TaskStatusEnum.DONE));
        assertEquals(TaskStatusEnum.IN_PROGRESS, epic.getStatus());

        // ТЗ №7: e. Подзадачи со статусом IN_PROGRESS.
        epic.update((Subtask) epic.getSubtask(taskId1).clone(TaskStatusEnum.IN_PROGRESS));
        epic.update((Subtask) epic.getSubtask(taskId2).clone(TaskStatusEnum.IN_PROGRESS));
        epic.update((Subtask) epic.getSubtask(taskId3).clone(TaskStatusEnum.IN_PROGRESS));
        assertEquals(TaskStatusEnum.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void getSubtask() {
        final IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                epic1.getSubtask(2);
                epic1.getSubtask(1);
            }
        });
        assertEquals("Идентификационный номер задачи 1 отсутствует в коллекции.", exception.getMessage());
    }

    @Test
    void containsSubtaskId() {
        Assertions.assertTrue(epic1.containsSubtaskId(2));
    }

    @Test
    void subtaskIdList() {
        List<Integer> subTaskIdList = epic1.subtaskIdList();
        assertNotEquals(0, subTaskIdList.size());
        Assertions.assertTrue(subTaskIdList.size() == epic1.size());
        Assertions.assertTrue(subTaskIdList.get(0) == epic1.getSubtask(subTaskIdList.get(0)).getTaskId());
    }

    @Test
    void getAllSubtasks() {
        List<Subtask> subTasks = epic1.getAllSubtasks();
        assertNotEquals(0, subTasks.size());
        Assertions.assertTrue(subTasks.size() == epic1.size());
    }

    @Test
    void removeSubtask() {
        int size = epic2.size();
        epic2.create(77, "Подзадача для удаления");
        Assertions.assertTrue(epic2.containsSubtaskId(77));
        assertEquals(size + 1, epic2.size());
        epic2.removeSubtask(77);
        Assertions.assertFalse(epic2.containsSubtaskId(77));
        assertEquals(size, epic2.size());

        final IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                epic2.removeSubtask(77);
            }
        });
        assertEquals("Идентификационный номер задачи 77 отсутствует в коллекции.", exception.getMessage());
    }

    @Test
    void removeAllTasks() {
        assertEquals(2, epic2.size());
        epic2.removeAllTasks();
        assertEquals(0, epic2.size());
    }

    @Test
    void testToStringWithSeparator() {
        assertEquals("0|EPIC|Эпик-задание 1|2|0|8|Это эпик-задание 1|0\n",
                epic1.toString("|"));
    }

    @Test
    void testClone() {
        Epic cloneEpic = (Epic) epic1.clone();
        Assertions.assertTrue(epic1.equals(cloneEpic));
    }

    @Test
    void testCloneWithNameAndDescription() {
        Epic cloneEpic = (Epic) epic2.clone("Клон эпик-задачи", "Это клон эпик-задачи");
        Assertions.assertFalse(epic2.equals(cloneEpic));
        Assertions.assertEquals("Клон эпик-задачи", cloneEpic.getName());
        Assertions.assertEquals("Это клон эпик-задачи", cloneEpic.getDescription());
        assertNotEquals(epic2.getName(), cloneEpic.getName());
        assertNotEquals(epic2.getDescription(), cloneEpic.getDescription());
    }

    @Test
    void testToString() {
        Epic epic = new Epic(CollectionUtils.getNextTaskId(Managers.getDefault().getAllTaskId()),
                "Эпик", "Это эпик");
        Managers.getDefault().create(epic);
        Assertions.assertEquals("Epic{name='Эпик', description='Это эпик', taskId=6', " +
                        "status=Задача только создана, startTime=null, duration=0, endTime=null, size=0}",
                epic.toString());

        int taskId1 = CollectionUtils.getNextTaskId(Managers.getDefault().getAllTaskId());
        epic.create(taskId1, "Подзадача 1", "Это подзадача 1");
        int taskId2 = CollectionUtils.getNextTaskId(Managers.getDefault().getAllTaskId());
        epic.create(taskId2, "Подзадача 2");

        Assertions.assertEquals("Epic{name='Эпик', description='Это эпик', taskId=6', " +
                        "status=Задача только создана, startTime=null, duration=0, endTime=null, size=2}",
                epic.toString());

        epic.update((Subtask) epic.getSubtask(taskId1).clone(2022, 5, 9, 22, 0));
        epic.update((Subtask) epic.getSubtask(taskId1).clone(10));
        epic.update((Subtask) epic.getSubtask(taskId2).clone(2022, 5, 9, 12, 0));
        epic.update((Subtask) epic.getSubtask(taskId1).clone(30));

        Assertions.assertEquals("Epic{name='Эпик', description='Это эпик', taskId=6', " +
                        "status=Задача только создана, startTime=2022-05-09T22:00, duration=30, " +
                        "endTime=2022-05-09T22:30, size=2}",
                epic.toString());
    }

    @Test
    void testEquals() {
        Assertions.assertTrue(epic1.equals(epic1));
        Assertions.assertTrue(epic2.equals(epic2));
        Assertions.assertFalse(epic1.equals(epic2));
    }

    @Test
    void testHashCode() {
        Assertions.assertTrue(epic1.hashCode() != 0);
    }
}