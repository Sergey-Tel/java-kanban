package dev.service;

import dev.domain.Epic;
import dev.domain.SubTask;
import dev.domain.Task;
import dev.domain.TaskBase;
import dev.utils.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, SubTask> subtasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<TaskBase> getAllTasks() {
        return Stream.of(epics.values(),
                        subtasks.values(),
                        tasks.values())
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(TaskBase::getTaskId))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskBase> getHighLevelTasks() {
        return Stream.concat(epics.values().stream(),
                        tasks.values().stream())
                .sorted(Comparator.comparing(TaskBase::getTaskId))
                .collect(Collectors.toList());
    }

    @Override
    public Task createTask(String name) {
        int newTaskId = CollectionUtils.getNextTaskId(getAllTaskId());
        Task addingTask = new Task(newTaskId, name);
        tasks.put(newTaskId, addingTask);
        return addingTask;
    }

    @Override
    public Epic createEpic(String name) {
        int newTaskId = CollectionUtils.getNextTaskId(getAllTaskId());
        Epic addingEpic = new Epic(newTaskId, name);
        addingEpic.setName(name);
        epics.put(newTaskId, addingEpic);
        return addingEpic;
    }

    @Override
    public SubTask createSubtask(int epicId, String name) {
        if (epics.containsKey(epicId)) {
            int newTaskId = CollectionUtils.getNextTaskId(getAllTaskId());
            SubTask subtask = new SubTask(epicId, newTaskId, name);
            subtasks.put(newTaskId, subtask);
            Epic epic = epics.get(epicId);
            epic.updateStatus();
            return subtask;
        } else {
            throw new IndexOutOfBoundsException("Идентификационный номер эпик-задачи отсутствует в коллекции.");
        }
    }


    @Override
    public int create(Task task) {
        if (tasks.containsKey(task.getTaskId())) {
            throw new IndexOutOfBoundsException("Задача с идентификационным номером "
                    + task.getTaskId() + " уже была создана ранее.");
        } else {
            tasks.put(task.getTaskId(), task);
            return task.getTaskId();
        }
    }


    @Override
    public int create(Epic epic) {
        if (epics.containsKey(epic.getTaskId())) {
            throw new IndexOutOfBoundsException("Эпик-задача с идентификационным номером "
                    + epic.getTaskId() + " уже была создана ранее.");
        } else {
            epics.put(epic.getTaskId(), epic);
            return epic.getTaskId();
        }
    }


    @Override
    public int create(SubTask subtask) {
        if (subtasks.containsKey(subtask.getTaskId())) {
            throw new IndexOutOfBoundsException("Подзадача с идентификационным номером " +
                    subtask.getTaskId() + " уже была создана ранее.");
        } else {
            subtasks.put(subtask.getTaskId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic !=null) epic.updateStatus();
            return subtask.getTaskId();
        }
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
        if (tasks.containsKey(task.getTaskId())) {
            tasks.put(task.getTaskId(), task);
        } else {
            throw new IndexOutOfBoundsException("Задача с заданным идентификационным номером отсутствует" +
                    " в коллекции.");
        }
    }


    @Override
    public void update(Epic epic) {
        if (epics.containsKey(epic.getTaskId())) {
            epics.put(epic.getTaskId(), epic);
        } else {
            throw new IndexOutOfBoundsException("Эпик-задача с заданным идентификационным номером отсутствует" +
                    " в коллекции.");
        }
    }


    @Override
    public void update(SubTask subtask) {
        if (subtasks.containsKey(subtask.getTaskId())) {
            subtasks.put(subtask.getTaskId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.updateStatus();
        } else {
            throw new IndexOutOfBoundsException("Подзадача с заданным идентификационным номером отсутствует" +
                    " в коллекции.");
        }
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
    public List<Integer> getAllTaskId() {
        return Stream.of(epics.values(), subtasks.values(), tasks.values())
                .flatMap(Collection::stream)
                .map(TaskBase::getTaskId)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsTaskId(int taskId) {
        return tasks.containsKey(taskId);
    }

    @Override
    public boolean containsEpicId(int taskId) {
        return epics.containsKey(taskId);
    }

    @Override
    public boolean containsSubtaskId(int taskId) {
        return subtasks.containsKey(taskId);
    }

    @Override
    public boolean containsTaskBaseId(int taskId) {
        List<Integer> allTasksId = getAllTaskId();
        return allTasksId.contains(taskId);
    }

    @Override
    public Task getTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            Task task = tasks.get(taskId);
            Managers.getDefaultHistory().add(task);
            return task;
        } else {
            throw new IndexOutOfBoundsException("Задача с заданным идентификационным номером отсутствует в коллекции.");
        }
    }

    @Override
    public Epic getEpic(int taskId) {
        if (epics.containsKey(taskId)) {
            Epic epic = epics.get(taskId);
            Managers.getDefaultHistory().add(epic);
            return epic;
        } else {
            throw new IndexOutOfBoundsException("Эпик-задача с заданным идентификационным номером "
                    + "отсутствует в коллекции.");
        }
    }

    @Override
    public SubTask getSubtask(int taskId) {
        if (subtasks.containsKey(taskId)) {
            SubTask subtask = subtasks.get(taskId);
            Managers.getDefaultHistory().add(subtask);
            return subtask;
        } else {
            throw new IndexOutOfBoundsException("Подзадача с заданным идентификационным номером "
                    + "отсутствует в коллекции.");
        }
    }

    @Override
    public TaskBase getTaskBase(int taskId) {
        if (tasks.containsKey(taskId)) {
            return getTask(taskId);
        } else if (epics.containsKey(taskId)) {
            return getEpic(taskId);
        } else if (subtasks.containsKey(taskId)) {
            return getSubtask(taskId);
        } else {
            throw new IndexOutOfBoundsException("Идентификационный номер (эпик/под) задачи отсутствует в коллекции.");
        }
    }

    @Override
    public int taskSize() {
        return tasks.size();
    }

    @Override
    public int epicSize() {
        return epics.size();
    }

    @Override
    public int subtaskSize() {
        return subtasks.size();
    }

    @Override
    public int allSize() {
        return taskSize() + epicSize() + subtaskSize();
    }

    @Override
    public void removeTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            Managers.getDefaultHistory().remove(taskId);
        } else if (epics.containsKey(taskId)) {
            for (Integer subtaskId : epics.get(taskId).subtaskIdList()) {
                subtasks.remove(subtaskId);
                Managers.getDefaultHistory().remove(subtaskId);
            }
            epics.remove(taskId);
            Managers.getDefaultHistory().remove(taskId);
        } else if (subtasks.containsKey(taskId)) {
            SubTask subtask = subtasks.get(taskId);
            Epic epic = epics.get(subtask.getEpicId());
            subtasks.remove(taskId);
            Managers.getDefaultHistory().remove(taskId);
            epic.updateStatus();
        } else {
            throw new IndexOutOfBoundsException("Идентификационный номер (эпик/под)задачи отсутствует в коллекции.");
        }
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        Managers.getDefaultHistory().clear();
    }
}