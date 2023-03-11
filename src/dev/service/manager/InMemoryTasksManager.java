package dev.service.manager;

import dev.domain.*;
import dev.service.InvalidTaskDateException;
import dev.service.server.Managers;
import dev.service.TaskPlanner;
import dev.service.history.HistoryManager;
import dev.utils.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTasksManager implements TasksManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final Set<TaskBase> prioritizedTasks;
    protected final TaskPlanner planner;
    protected final HistoryManager historyManager;

    public InMemoryTasksManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>(new TaskComparator());
        historyManager = Managers.getDefaultHistory();
        planner = new TaskPlanner();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
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
    public List<Subtask> getSubtasks() {
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
    public List<TaskBase> getPrioritizedTasks() {
        return new LinkedList<TaskBase>(prioritizedTasks);
    }

    @Override
    public Epic createEpic(String name) {
        int newTaskId = CollectionUtils.getNextTaskId(getAllTaskId());
        Epic addingEpic = new Epic(newTaskId, name);
        epics.put(newTaskId, addingEpic);
        return addingEpic;
    }

    @Override
    public Task createTask(String name) {
        int newTaskId = CollectionUtils.getNextTaskId(getAllTaskId());
        Task task = new Task(newTaskId, name);
        tasks.put(newTaskId, task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Subtask createSubtask(int epicId, String name) throws IOException {
        if (epics.containsKey(epicId)) {
            int newTaskId = CollectionUtils.getNextTaskId(getAllTaskId());
            Subtask subtask = new Subtask(epicId, newTaskId, name);
            subtasks.put(newTaskId, subtask);
            prioritizedTasks.add(subtask);
            Epic epic = epics.get(epicId);
            epic.updateStatus();
            return subtask;
        } else {
            throw new IndexOutOfBoundsException("Идентификационный номер эпик-задачи отсутствует в коллекции.");
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
    public int create(Task task) {
        Optional<TaskBase> currentTask = planner.getCurrentTask(task);
        if (tasks.containsKey(task.getTaskId())) {
            throw new IndexOutOfBoundsException("Задача с идентификационным номером "
                    + task.getTaskId() + " уже была создана ранее.");
        }
        if (currentTask.isPresent()) {
            throw new InvalidTaskDateException("Конфликт времени исполнения задач.",
                    currentTask.get(), task);
        } else {
            tasks.put(task.getTaskId(), task);
            prioritizedTasks.add(task);
            planner.add(task);
            return task.getTaskId();
        }
    }

    @Override
    public int create(TaskBase task) throws IOException {
        if (task instanceof Epic) {
            return create((Epic) task);
        } else if (task instanceof Subtask) {
            return create((Subtask) task);
        } else {
            return create((Task) task);
        }
    }


    @Override
    public int create(Subtask subtask) throws IOException {
        Optional<TaskBase> currentTask = planner.getCurrentTask(subtask);
        if (subtasks.containsKey(subtask.getTaskId())) {
            throw new IndexOutOfBoundsException("Подзадача с идентификационным номером " +
                    subtask.getTaskId() + " уже была создана ранее.");
        } else if (currentTask.isPresent()) {
            throw new InvalidTaskDateException("Конфликт времени исполнения задач.",
                    currentTask.get(), subtask);
        } else if (!epics.containsKey(subtask.getEpicId())) {
            throw new IndexOutOfBoundsException("Идентификационный номер эпик-задачи отсутствует в коллекции.");
        } else {
            subtasks.put(subtask.getTaskId(), subtask);
            prioritizedTasks.add(subtask);
            planner.add(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) epic.updateStatus();
            return subtask.getTaskId();
        }
    }


    @Override
    public void update(Task task) {
        if (tasks.containsKey(task.getTaskId())) {
            Optional<TaskBase> currentTask = planner.getCurrentTask(task);
            if (currentTask.isPresent()) {
                throw new InvalidTaskDateException("Конфликт времени исполнения задач.",
                        currentTask.get(), task);
            }
            Task oldTask = tasks.get(task.getTaskId());
            prioritizedTasks.remove(oldTask);
            planner.remove(oldTask);
            tasks.put(task.getTaskId(), task);
            prioritizedTasks.add(task);
            planner.add(task);
        } else {
            throw new IndexOutOfBoundsException("Задача с заданным идентификационным номером отсутствует" +
                    " в коллекции.");
        }
    }


    @Override
    public void update(Epic epic) throws IOException {
        if (epics.containsKey(epic.getTaskId())) {
            epics.put(epic.getTaskId(), epic);
            epic.updateStatus();
        } else {
            throw new IndexOutOfBoundsException("Эпик-задача с заданным идентификационным номером отсутствует" +
                    " в коллекции.");
        }
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
    public void update(Subtask subtask) throws IOException {
        if (subtasks.containsKey(subtask.getTaskId())) {
            Optional<TaskBase> currentTask = planner.getCurrentTask(subtask);
            if (currentTask.isPresent()) {
                throw new InvalidTaskDateException("Конфликт времени исполнения задач.",
                        currentTask.get(), subtask);
            }
            Subtask oldSubtask = subtasks.get(subtask.getTaskId());
            prioritizedTasks.remove(oldSubtask);
            planner.remove(oldSubtask);
            subtasks.put(subtask.getTaskId(), subtask);
            prioritizedTasks.add(subtask);
            planner.add(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.updateStatus();
        } else {
            throw new IndexOutOfBoundsException("Подзадача с заданным идентификационным номером отсутствует" +
                    " в коллекции.");
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
            historyManager.add(task);
            return task;
        } else {
            throw new IndexOutOfBoundsException("Задача с заданным идентификационным номером отсутствует в коллекции.");
        }
    }

    @Override
    public Epic getEpic(int taskId) {
        if (epics.containsKey(taskId)) {
            Epic epic = epics.get(taskId);
            historyManager.add(epic);
            return epic;
        } else {
            throw new IndexOutOfBoundsException("Эпик-задача с заданным идентификационным номером "
                    + "отсутствует в коллекции.");
        }
    }

    @Override
    public Subtask getSubtask(int taskId) {
        if (subtasks.containsKey(taskId)) {
            Subtask subtask = subtasks.get(taskId);
            historyManager.add(subtask);
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
    public void removeTask(int taskId) throws IOException {
        if (tasks.containsKey(taskId)) {
            Task task = tasks.get(taskId);
            tasks.remove(taskId);
            prioritizedTasks.remove(task);
            planner.remove(task);
            historyManager.remove(taskId);
        } else if (epics.containsKey(taskId)) {
            for (Integer subtaskId : epics.get(taskId).subtaskIdList()) {
                Subtask subtask = subtasks.get(subtaskId);
                prioritizedTasks.remove(subtask);
                planner.remove(subtask);
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epics.remove(taskId);
            historyManager.remove(taskId);
        } else if (subtasks.containsKey(taskId)) {
            Subtask subtask = subtasks.get(taskId);
            prioritizedTasks.remove(subtask);
            planner.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            subtasks.remove(taskId);
            historyManager.remove(taskId);
            epic.updateStatus();
        } else {
            throw new IndexOutOfBoundsException("Идентификационный номер (эпик/под)задачи отсутствует в коллекции.");
        }
    }

    @Override
    public void removeAllTasks() {
        for (Task task: this.getTasks()) {
            prioritizedTasks.remove(task);
            planner.remove(task);
            historyManager.remove(task.getTaskId());
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        removeAllSubtasks();
        for (Epic epic: this.getEpics()) {
            prioritizedTasks.remove(epic);
            planner.remove(epic);
            historyManager.remove(epic.getTaskId());
        }
        epics.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask: this.getSubtasks()) {
            prioritizedTasks.remove(subtask);
            planner.remove(subtask);
            historyManager.remove(subtask.getTaskId());
        }
        subtasks.clear();
    }

    @Override
    public void removeAll() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        prioritizedTasks.clear();
        planner.clear();
        historyManager.clear();
    }
}