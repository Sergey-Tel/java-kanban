package dev.service;

import dev.domain.TaskBase;

import java.util.LinkedList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {
    private static final int RECORD_COUNT = 10;
    private final LinkedList<TaskBase> history;

    public InMemoryHistoryManager() {
        history = new LinkedList<>();
    }

    @Override
    public void add(TaskBase task) {
        history.add(task);
        trimHistory();
    }

    @Override
    public List<TaskBase> getHistory() {
        return history;
    }

    private void trimHistory() {
        while (history.size() > RECORD_COUNT) {
            history.removeFirst();
        }
    }
}
