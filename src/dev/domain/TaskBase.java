package dev.domain;

public interface TaskBase extends Cloneable, Comparable<TaskBase> {
    int getTaskId();

    String getName();

    String getDescription();

    TaskStatusEnum getStatus();
}
