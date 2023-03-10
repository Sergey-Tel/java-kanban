package dev.service;

import dev.domain.Epic;
import dev.domain.Subtask;
import dev.domain.Task;
import dev.domain.TaskBase;

import java.io.IOException;
import java.util.List;

public interface TasksManager {

    HistoryManager getHistoryManager();

    List<Epic> getEpics();

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<TaskBase> getAllTasks();

    List<TaskBase> getPrioritizedTasks();

    List<TaskBase> getHighLevelTasks();

    Task createTask(String name);

    Epic createEpic(String name);

    Subtask createSubtask(int epicId, String name) throws IOException;

    int create(Task task);

    int create(Epic epic);

    int create(Subtask subtask) throws IOException;

    int create(TaskBase task) throws IOException;

    void update(Task task);

    void update(Epic epic) throws IOException;

    void update(Subtask subtask) throws IOException;

    void update(TaskBase task) throws IOException;

    List<Integer> getAllTaskId();

    boolean containsTaskId(int taskId);

    boolean containsEpicId(int taskId);

    boolean containsSubtaskId(int taskId);

    boolean containsTaskBaseId(int taskId);

    Task getTask(int taskId);

    Subtask getSubtask(int taskId);

    Epic getEpic(int taskId);

    TaskBase getTaskBase(int taskId);

    int taskSize();

    int epicSize();

    int subtaskSize();

    int allSize();

    void removeTask(int taskId) throws IOException;

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    void removeAll();
}