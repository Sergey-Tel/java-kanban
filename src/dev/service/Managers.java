package dev.service;

import java.io.File;


public class Managers {
    static TasksManager tasksManager;

    private Managers(){}

    public static TasksManager getDefault() {
        if (tasksManager == null) {
            tasksManager = new InMemoryTasksManager();
        }
        return tasksManager;
    }

    public static FileBackedTasksManager setFileTasksManager(File file) {
        tasksManager = FileBackedTasksManager.loadFromFile(file);
        return (FileBackedTasksManager) tasksManager;
    }

    public static InMemoryTasksManager setMemoryTasksManager() {
        tasksManager = new InMemoryTasksManager();
        return (InMemoryTasksManager) tasksManager;
    }


    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
