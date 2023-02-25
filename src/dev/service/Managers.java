package dev.service;

import java.io.File;


public class Managers {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    public static TaskManager getDefault() {
        if (taskManager == null) {
            taskManager = new InMemoryTaskManager();
        }
        return taskManager;
    }

    public static void SetFileTasksManager(File file){
        taskManager = FileBackedTaskManager.loadFromFile(file);
    }

    public static void SetMemoryTasksManager(){
        taskManager = new InMemoryTaskManager();
    }


    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }
}
