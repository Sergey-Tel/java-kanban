package dev.service;

import dev.domain.TaskBase;

import java.util.List;

public interface HistoryManager {
    void add(TaskBase task);

    List<TaskBase> getHistory();
}
