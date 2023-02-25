package dev.service;

import dev.domain.*;
import dev.utils.CollectionUtils;
import dev.utils.ReportUtils;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String PARAM_SEPARATOR = "|";
    private static final String COLUMN_HEADER =
            "id" + PARAM_SEPARATOR +
                    "type" + PARAM_SEPARATOR +
                    "name" + PARAM_SEPARATOR +
                    "status" + PARAM_SEPARATOR +
                    "description" + PARAM_SEPARATOR +
                    "epic";
    private final File file;

    private FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }


    public static void main(String[] args) {
        System.out.println("Тестирование приложения по условиям, заданным в техническом задании Спринта №6:");

        Path path = FileSystems.getDefault().getPath("java-kanban.csv");
        Managers.SetFileTasksManager(path.toFile());

        TaskManager manager = Managers.getDefault();
        manager.removeAllTasks();

        System.out.println("\n1.\tЗаведите несколько разных задач, эпиков и подзадач;");
        int nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Task task = new Task(nextTaskId, "Задача 1", "Создаю обычную задачу с индексом 0.");
        manager.create(task);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        task = new Task(nextTaskId, "Задача 2", "Создаю обычную задачу с индексом 1.");
        manager.create(task);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Epic epic = new Epic(nextTaskId,
                "Эпик-задача 1", "Создаю эпик-задачу с индексом 2, в которой будет создано три подзадачи.");
        manager.create(epic);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        SubTask subtask = new SubTask(epic.getTaskId(), nextTaskId,
                "Подзадача 1", "Создаю подзадачу с индексом 3.");
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new SubTask(epic.getTaskId(), nextTaskId,
                "Подзадача 2", "Создаю подзадачу с индексом 4.");
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new SubTask(epic.getTaskId(), nextTaskId,
                "Подзадача 3", "Создаю подзадачу с индексом 5.");
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        epic = new Epic(nextTaskId,
                "Эпик-задача 2", "Создаю эпик-задачу с индексом 6 без подзадач.");
        manager.create(epic);

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks(), false);

        System.out.println("\n2.\tЗапросите некоторые из них, чтобы заполнилась история просмотра;");
        System.out.println("\nВызываю задачи 20 раз в случайном порядке.");
        for (int i = 0; i < 20; i++) {
            int randomId = (int) (Math.random() * 7);
            TaskBase randomTask = manager.getTaskBase(randomId);
            System.out.print((i + 1) + ") ");
            ReportUtils.printTask(randomTask, false);
        }
        System.out.println("\nПечатаем историю просмотра.");
        ReportUtils.printTasksCollection(Managers.getDefaultHistory().getHistory(), false);

        System.out.println("\n3.\tСоздаем новый FileBackedTasksManager менеджер из этого же файла.;");
        manager = FileBackedTaskManager.loadFromFile(path.toFile());

        System.out.println("\nПечатаем историю просмотра.");
        ReportUtils.printTasksCollection(Managers.getDefaultHistory().getHistory(), false);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Managers.getDefaultHistory().clear();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                reader.readLine();
                while (reader.ready()) {
                    String line = reader.readLine();
                    if (!line.isBlank()) {
                        TaskBase task = fromString(line);
                        if (task instanceof Epic) {
                            manager.epics.put(task.getTaskId(), (Epic) task);
                        } else if (task instanceof SubTask) {
                            manager.subtasks.put(task.getTaskId(),(SubTask) task);
                            Epic epic = manager.getEpic(((SubTask) task).getEpicId());
                            epic.updateStatus();
                        } else {
                            manager.tasks.put(task.getTaskId(), (Task) task);
                        }
                    } else {
                        break;
                    }
                }
                String history = reader.readLine();
                manager.createHistory(history);
            } catch (IOException e) {
                throw new ManagerSaveException("Произошла ошибка во время чтения файла.", e.getCause());
            }
        }
        return manager;
    }

    private static String toString(HistoryManager manager) {
        return manager.getHistoryId().stream().map(String::valueOf)
                .collect(Collectors.joining(PARAM_SEPARATOR));
    }


    private static TaskBase fromString(String value) {
        String[] param = value.split("[" + PARAM_SEPARATOR + "]");
        int taskId = Integer.parseInt(param[0]);
        TaskTypeEnum type = TaskTypeEnum.fromKey(param[1]);
        TaskStatusEnum status = TaskStatusEnum.fromKey(param[3]);
        int epicId = Integer.parseInt(param[5]);
        switch (type) {
            case EPIC: {
                return new Epic(taskId, param[2], param[4]);
            }
            case SUBTASK: {
                return new SubTask(epicId, taskId, param[2], param[4], status);
            }
            default: {
                return new Task(taskId, param[2], param[4], status);
            }
        }
    }

    private void createHistory(String history) {
        if (history != null) {
            String[] tasks = history.split("[" + PARAM_SEPARATOR + "]");
            for (String id : tasks) {
                int taskId = Integer.parseInt(id);
                Managers.getDefaultHistory().add(getTaskBase(taskId));
            }
        }
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(COLUMN_HEADER + "\n");
            List<TaskBase> tasks = getAllTasks();
            for (TaskBase task : tasks) {
                writer.write(task.toString(PARAM_SEPARATOR));
            }
            writer.write("\n");
            String history = toString(Managers.getDefaultHistory());
            writer.write(history);
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи в файл.", e.getCause());
        }
    }

    @Override
    public Task createTask(String name) {
        Task task = super.createTask(name);
        save();
        return task;
    }

    @Override
    public Epic createEpic(String name) {
        Epic epic = super.createEpic(name);
        save();
        return epic;
    }

    @Override
    public SubTask createSubtask(int epicId, String name) {
        SubTask subtask = super.createSubtask(epicId, name);
        save();
        return subtask;
    }

    @Override
    public int create(Task task) {
        int id = super.create(task);
        save();
        return id;
    }

    @Override
    public int create(Epic epic) {
        int id = super.create(epic);
        save();
        return id;
    }

    @Override
    public int create(SubTask subtask) {
        int id = super.create(subtask);
        save();
        return id;
    }

    @Override
    public int create(TaskBase task) {
        if (task instanceof Epic) {
            return create((Epic) task);
        } else if (task instanceof SubTask) {
            return create((SubTask) task);
        } else {
            return create((Task) task);
        }
    }

    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public void update(Epic epic) {
        super.update(epic);
        save();
    }

    @Override
    public void update(SubTask subtask) {
        super.update(subtask);
        save();
    }

    @Override
    public void update(TaskBase task) {
        if (task instanceof Epic) {
            update((Epic) task);
        } else if (task instanceof SubTask) {
            update((SubTask) task);
        } else {
            update((Task) task);
        }
    }

    @Override
    public Task getTask(int taskId) {
        Task task = super.getTask(taskId);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int taskId) {
        Epic epic = super.getEpic(taskId);
        save();
        return epic;
    }

    @Override
    public SubTask getSubtask(int taskId) {
        SubTask subtask = super.getSubtask(taskId);
        save();
        return subtask;
    }

    @Override
    public TaskBase getTaskBase(int taskId) {
        TaskBase task = super.getTaskBase(taskId);
        save();
        return task;
    }

    @Override
    public void removeTask(int taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }
}
