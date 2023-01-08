package dev.domain;

public interface TaskBase extends  Comparable<TaskBase> {
    int getTaskId();

    String getName();

    String getDescription();

    TaskStatusEnum getStatus();
}
