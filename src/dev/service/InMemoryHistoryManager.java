package dev.service;

import dev.domain.TaskBase;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<TaskBase> history;

    public InMemoryHistoryManager() {
        history = new CustomLinkedList<>();
    }


    @Override
    public void add(TaskBase task) {
        history.add(task);
    }


    @Override
    public void remove(int id) {
        history.remove(id);
    }


    @Override
    public List<TaskBase> getHistory() {
        return history.getTasks();
    }

    @Override
    public List<Integer> getHistoryId() {
        return history.getTasksId();
    }

    @Override
    public void clear() {
        history.clear();
    }
}
