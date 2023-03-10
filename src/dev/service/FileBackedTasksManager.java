package dev.service;

import dev.domain.*;

import java.io.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.domain.TaskTypeEnum.EPIC;
import static dev.domain.TaskTypeEnum.SUBTASK;

public class FileBackedTasksManager extends InMemoryTasksManager {

    private static final String PARAM_SEPARATOR = "|";
    private static final String COLUMN_HEADER =
            "id" + PARAM_SEPARATOR +
                    "type" + PARAM_SEPARATOR +
                    "name" + PARAM_SEPARATOR +
                    "status" + PARAM_SEPARATOR +
                    "start" + PARAM_SEPARATOR +
                    "duration" + PARAM_SEPARATOR +
                    "description" + PARAM_SEPARATOR +
                    "epic";
    private final File file;

    protected FileBackedTasksManager() {
        super();
        this.file = null;
    }

    private FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                if (!line.isBlank()) {
                    TaskBase task = fromString(line);
                    Optional<TaskBase> currentTask = manager.planner.getCurrentTask(task);
                    if (task instanceof Epic) {
                        manager.epics.put(task.getTaskId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        if (currentTask.isPresent()) {
                            throw new ManagerSaveException("Произошла ошибка во время чтения файла. " +
                                    "Обнаружен конфликт во времени исполнения задач",
                                    new Throwable(currentTask.get().toString()));
                        }
                        manager.subtasks.put(task.getTaskId(), (Subtask) task);
                        manager.prioritizedTasks.add(task);
                        manager.planner.add(task);
                        Epic epic = manager.getEpic(((Subtask) task).getEpicId());
                        epic.updateStatus();
                    } else {
                        if (currentTask.isPresent()) {
                            throw new ManagerSaveException("Произошла ошибка во время чтения файла. " +
                                    "Обнаружен конфликт во времени исполнения задач",
                                    new Throwable(currentTask.get().toString()));
                        }
                        manager.tasks.put(task.getTaskId(), (Task) task);
                        manager.prioritizedTasks.add(task);
                        manager.planner.add(task);
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
        return manager;
    }

    private static String toString(HistoryManager manager) {
        return manager.getHistoryId().stream().map(String::valueOf)
                .collect(Collectors.joining(PARAM_SEPARATOR));
    }


    private static TaskBase fromString(String value) {
        String[] param = value.split("[" + PARAM_SEPARATOR + "]");
        int taskId = Integer.parseInt(param[0]);
        TaskTypeEnum type = TaskTypeEnum.fromName(param[1]);
        TaskStatusEnum status = TaskStatusEnum.fromKey(param[3]);
        long startTime = Long.parseLong(param[4]);
        long duration = Long.parseLong(param[5]);
        int epicId = Integer.parseInt(param[7]);
        switch (type) {
            case EPIC: {
                return new Epic(taskId, param[2], param[6]);
            }
            case SUBTASK: {
                return new Subtask(epicId, taskId, param[2], param[6], status,
                        startTime == 0 ? Optional.empty() : Optional.of(Instant.ofEpochMilli(startTime)), duration);
            }
            default: {
                return new Task(taskId, param[2], param[6], status,
                        startTime == 0 ? Optional.empty() : Optional.of(Instant.ofEpochMilli(startTime)), duration);
            }
        }
    }

    private void createHistory(String history) {
        historyManager.clear();
        if (history != null) {
            String[] tasks = history.split("[" + PARAM_SEPARATOR + "]");
            for (String id : tasks) {
                int taskId = Integer.parseInt(id);
                historyManager.add(getTaskBase(taskId));
            }
        }
    }

    protected void save() {
        if (file == null) return;
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(COLUMN_HEADER + "\n");
            List<TaskBase> tasks = getAllTasks();
            for (TaskBase task : tasks) {
                writer.write(task.toString(PARAM_SEPARATOR));
            }
            writer.write("\n");
            String history = toString(historyManager);
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
    public Subtask createSubtask(int epicId, String name) throws IOException {
        Subtask subtask = super.createSubtask(epicId, name);
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
    public int create(Subtask subtask) throws IOException {
        int id = super.create(subtask);
        save();
        return id;
    }

    @Override
    public int create(TaskBase task) throws IOException {
        if (task.getType() == EPIC) {
            return create((Epic) task);
        } else if (task.getType() == SUBTASK) {
            return create((Subtask) task);
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
    public void update(Epic epic) throws IOException {
        super.update(epic);
        save();
    }

    @Override
    public void update(Subtask subtask) throws IOException {
        super.update(subtask);
        save();
    }

    @Override
    public void update(TaskBase task) throws IOException {
        if (task instanceof Epic) {
            update((Epic) task);
        } else if (task instanceof Subtask) {
            update((Subtask) task);
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
    public Subtask getSubtask(int taskId) {
        Subtask subtask = super.getSubtask(taskId);
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
    public void removeTask(int taskId) throws IOException {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }
    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAll() {
        super.removeAll();
        save();
    }
}
