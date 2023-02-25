package dev.service;

import dev.domain.TaskBase;

import java.util.List;


public interface HistoryManager {
    void add(TaskBase task);
    void remove(int id);
    List<TaskBase> getHistory();
    List<Integer> getHistoryId();
    void clear();
}
