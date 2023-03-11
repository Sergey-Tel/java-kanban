package dev.service.history;

import dev.domain.TaskBase;

import java.util.List;

public interface HistoryManager {
    void add(TaskBase task);

    void remove(int id);

    TaskBase getFirst();

    TaskBase getLast();

    List<TaskBase> getHistory();

    List<Integer> getHistoryId();

    void clear();
}
