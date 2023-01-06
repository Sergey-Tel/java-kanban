package dev.service;


public class Managers {
    static dev.service.TaskManager taskManager;
    static HistoryManager historyManager;

    public static dev.service.TaskManager getDefault() {
        if (taskManager == null) {
            taskManager = new InMemoryTaskManager();
        }
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }
}
