package dev.service;

import dev.domain.Epic;
import dev.domain.Subtask;
import dev.domain.Task;
import dev.domain.TaskBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTestAbstract<T extends TasksManager> {

    T manager;

    @BeforeEach
    abstract void beforeEach();

    @AfterEach
    void afterEach() {
        manager.removeAll();
    }

    @Test
    void testGetTasks() {
        List<Task> tasks = manager.getTasks();
        assertEquals(1, tasks.size());
        Assertions.assertTrue(tasks.size() == manager.taskSize());
    }

    @Test
    void testGetEpics() {
        List<Epic> epics = manager.getEpics();
        assertEquals(1, epics.size());
        Assertions.assertTrue(epics.size() == manager.epicSize());
    }

    @Test
    void testGetSubtasks() {
        List<Subtask> subtasks = manager.getSubtasks();
        assertEquals(2, subtasks.size());
        Assertions.assertTrue(subtasks.size() == manager.subtaskSize());
    }

    @Test
    void testGetAllTasks() {
        List<TaskBase> tasks = manager.getAllTasks();
        assertEquals(4, tasks.size());
        Assertions.assertTrue(tasks.size() == manager.allSize());
    }

    @Test
    void testGetHighLevelTasks() {
        List<TaskBase> highLevelTasks = manager.getHighLevelTasks();
        assertEquals(2, highLevelTasks.size());
        Assertions.assertTrue(highLevelTasks.size() == manager.epicSize() + manager.taskSize());
    }

    @Test
    void testGetPrioritizedTasks() {
        List<TaskBase> prioritizedTasks = manager.getPrioritizedTasks();
        assertEquals(3, prioritizedTasks.size());
        Assertions.assertTrue(prioritizedTasks.size() == manager.taskSize() + manager.subtaskSize());
    }

    @Test
    void testCreateTask() {
        Task task = new Task(55, "Создаваемая задача");
        task = (Task) task.clone(10);
        task = (Task) task.clone(2022, 5, 1, 12, 0);
        manager.create(task);

        final IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                Task task = new Task(55, "Создаваемая задача c существующим номером");
                manager.create((TaskBase)task);
            }
        });
        assertEquals("Задача с идентификационным номером 55 уже была создана ранее.", exception.getMessage());

        final InvalidTaskDateException dateException = assertThrows(InvalidTaskDateException.class, new Executable() {
            @Override
            public void execute() {
                Task task = new Task(77, "Создаваемая задача c существующей датой");
                task = (Task) task.clone(10);
                task = (Task) task.clone(2022, 5, 1, 12, 0);
                manager.create(task);
            }
        });
        assertEquals("Конфликт времени исполнения задач.", dateException.getMessage());
        assertEquals(55, dateException.getCurrentTask().getTaskId());
        assertEquals(77, dateException.getTask().getTaskId());
    }

    @Test
    void testCreateSubtask() {
        Subtask subtask = new Subtask(1, 55, "Создаваемая подзадача");
        subtask = (Subtask) subtask.clone(10);
        subtask = (Subtask) subtask.clone(2022, 5, 1, 12, 0);
        manager.create(subtask);

        final IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                Subtask subtask = new Subtask(1, 55, "Создаваемая подзадача c существующим номером");
                manager.create(subtask);
            }
        });
        assertEquals("Подзадача с идентификационным номером 55 уже была создана ранее.", exception.getMessage());

        final IndexOutOfBoundsException absentEpicException = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                Subtask subtask = new Subtask(99, 44, "Создаваемая подзадача c не существующим номером эпик-задачи");
                manager.create((TaskBase)subtask);
            }
        });
        assertEquals("Идентификационный номер эпик-задачи отсутствует в коллекции.", absentEpicException.getMessage());

        final InvalidTaskDateException dateException = assertThrows(InvalidTaskDateException.class, new Executable() {
            @Override
            public void execute() {
                Subtask subtask = new Subtask(1, 77, "Создаваемая подзадача c существующей датой");
                subtask = (Subtask) subtask.clone(10);
                subtask = (Subtask) subtask.clone(2022, 5, 1, 12, 0);
                manager.create(subtask);
            }
        });
        assertEquals("Конфликт времени исполнения задач.", dateException.getMessage());

        assertEquals(55, dateException.getCurrentTask().getTaskId());
        assertEquals(77, dateException.getTask().getTaskId());
    }

    @Test
    void testCreateEpic() {
        Epic epic = new Epic(55, "Создаваемая эпик-задача");
        manager.create(epic);
        final IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                Epic epic = new Epic(55, "Эпик-задача с существующим номером");
                manager.create((TaskBase)epic);
            }
        });
        assertEquals("Эпик-задача с идентификационным номером 55 уже была создана ранее.", exception.getMessage());
    }

    @Test
    void testUpdate() {
        Task task = new Task(101, "Создаваемая задача");
        manager.create(task);

        task =(Task) manager.getTaskBase(101);
        task = (Task) task.clone(10);
        task = (Task) task.clone(2022, 5, 1, 14, 0);
        manager.update(task);

        Epic epic = new Epic(102, "Создаваемая эпик-задача");
        manager.create(epic);

        epic =(Epic) manager.getTaskBase(102);
        epic = (Epic) epic.clone("Новый заголовок", "Новое пояснение");
        manager.update(epic);

        Subtask subtask = new Subtask(102, 103, "Создаваемая подзадача");
        manager.create(subtask);

        subtask =(Subtask) manager.getTaskBase(103);
        subtask = (Subtask) subtask.clone(10);
        subtask = (Subtask) subtask.clone(2022, 5, 1, 16, 0);
        manager.update(subtask);

        final IndexOutOfBoundsException absentTaskException = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                Task task = new Task(103,"Создаваемая задача c отсутствующим номером");
                manager.update((TaskBase)task);
            }
        });
        assertEquals("Задача с заданным идентификационным номером отсутствует в коллекции.", absentTaskException.getMessage());

        final IndexOutOfBoundsException absentEpicException = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                Epic epic = new Epic(103, "Эпик-задача с отсутствующим номером");
                manager.update((TaskBase)epic);
            }
        });
        assertEquals("Эпик-задача с заданным идентификационным номером отсутствует в коллекции.", absentEpicException.getMessage());

        final IndexOutOfBoundsException absentSubtaskException = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                Subtask subtask = new Subtask(103, 55, "Создаваемая подзадача c существующим номером");
                manager.update((TaskBase)subtask);
            }
        });
        assertEquals("Эпик-задача с заданным идентификационным номером отсутствует в коллекции.", absentEpicException.getMessage());

        final InvalidTaskDateException taskDateException = assertThrows(InvalidTaskDateException.class, new Executable() {
            @Override
            public void execute() {
                Task task =(Task) manager.getTaskBase(101);
                task = (Task) task.clone(2022, 5, 1, 16, 0);
                manager.update(task);
            }
        });
        assertEquals("Конфликт времени исполнения задач.", taskDateException.getMessage());

        final InvalidTaskDateException subtaskDateException = assertThrows(InvalidTaskDateException.class, new Executable() {
            @Override
            public void execute() {
                Subtask subtask =(Subtask) manager.getTaskBase(103);
                subtask = (Subtask) subtask.clone(2022, 5, 1, 14, 0);
                manager.update(subtask);
            }
        });
        assertEquals("Конфликт времени исполнения задач.", subtaskDateException.getMessage());
    }

    @Test
    void getTask() {
        Task task = new Task(101, "Создаваемая задача");
        manager.create(task);

        assertTrue(manager.containsTaskBaseId(101));
        assertEquals(task, manager.getTaskBase(101));

        final IndexOutOfBoundsException absentException = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                manager.getTaskBase(102);
            }
        });
        assertEquals("Идентификационный номер (эпик/под) задачи отсутствует в коллекции.", absentException.getMessage());

        final IndexOutOfBoundsException absentTaskException = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                manager.getTask(102);
            }
        });
        assertEquals("Задача с заданным идентификационным номером отсутствует в коллекции.", absentTaskException.getMessage());

        final IndexOutOfBoundsException absentEpicException = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                manager.getEpic(102);
            }
        });
        assertEquals("Эпик-задача с заданным идентификационным номером отсутствует в коллекции.", absentEpicException.getMessage());

        final IndexOutOfBoundsException absentSubtaskException = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                manager.getSubtask(102);
            }
        });
        assertEquals("Подзадача с заданным идентификационным номером отсутствует в коллекции.", absentSubtaskException.getMessage());
    }

    @Test
    void removeTask() {
        int size = manager.allSize();

        int taskId = manager.createTask("Задача для удаления").getTaskId();
        Assertions.assertTrue(manager.containsTaskId(taskId));
        assertEquals(size + 1, manager.allSize());

        int epicId = manager.createEpic("Эпик-задача для удаления").getTaskId();
        Assertions.assertTrue(manager.containsEpicId(epicId));
        assertEquals(size + 2, manager.allSize());

        int subtaskId = manager.createSubtask(epicId,"Подзадача для удаления").getTaskId();
        Assertions.assertTrue(manager.containsSubtaskId(subtaskId));
        assertEquals(size + 3, manager.allSize());

        manager.createSubtask(epicId,"Вторая подзадача для удаления");
        assertEquals(size + 4, manager.allSize());

        final IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                manager.createSubtask(911,"Подзадача для несуществующей эпик-задачи");
            }
        });
        assertEquals("Идентификационный номер эпик-задачи отсутствует в коллекции.", exception.getMessage());

        manager.removeTask(subtaskId);
        Assertions.assertFalse(manager.containsSubtaskId(subtaskId));

        manager.removeTask(epicId);
        Assertions.assertFalse(manager.containsEpicId(epicId));

        manager.removeTask(taskId);
        Assertions.assertFalse(manager.containsTaskId(taskId));

        assertEquals(size, manager.allSize());

        final IndexOutOfBoundsException absentEpicException = assertThrows(IndexOutOfBoundsException.class, new Executable() {
            @Override
            public void execute() {
                manager.removeTask(77);
            }
        });
        assertEquals("Идентификационный номер (эпик/под)задачи отсутствует в коллекции.", absentEpicException.getMessage());
    }

    @Test
    void removeAllTasks() {
        assertEquals(4, manager.allSize());
        manager.removeAllTasks();
        assertEquals(3, manager.allSize());
    }

    @Test
    void removeAllEpics() {
        assertEquals(4, manager.allSize());
        manager.removeAllEpics();
        assertEquals(1, manager.allSize());
    }

    @Test
    void removeAllSubtask() {
        assertEquals(4, manager.allSize());
        manager.removeAllSubtasks();
        assertEquals(2, manager.allSize());
    }

    @Test
    void removeAll() {
        assertEquals(4, manager.allSize());
        manager.removeAll();
        assertEquals(0, manager.allSize());
    }
}
