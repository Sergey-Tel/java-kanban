package dev.service;

import dev.domain.Epic;
import dev.domain.SubTask;
import dev.domain.Task;
import dev.domain.TaskBase;

import java.util.List;

public interface TaskManager {
    List<Epic> getEpics();

    List<Task> getTasks();

    List<SubTask> getSubtasks();

    List<TaskBase> getAllTasks();

    List<TaskBase> getHighLevelTasks();

    Task createTask(String name);

    Epic createEpic(String name);

    SubTask createSubtask(int epicId, String name);

    int create(Task task);

    int create(Epic epic);

    int create(SubTask subtask);

    int create(TaskBase task);

    void update(Task task);

    void update(Epic epic);

    void update(SubTask subtask);

    void update(TaskBase task);

    List<Integer> getAllTaskId();

    boolean containsTaskId(int taskId);

    boolean containsEpicId(int taskId);

    boolean containsSubtaskId(int taskId);

    boolean containsTaskBaseId(int taskId);

    Task getTask(int taskId);

    SubTask getSubtask(int taskId);

    Epic getEpic(int taskId);

    TaskBase getTaskBase(int taskId);

    int taskSize();

    int epicSize();

    int subtaskSize();

    int allSize();

    void removeTask(int taskId);

    void removeAllTasks();
}