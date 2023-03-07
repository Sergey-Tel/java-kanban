package dev.domain;

import java.time.Instant;
import java.util.Optional;

public interface TaskBase extends Cloneable, Comparable<TaskBase> {
    int getTaskId();

    String getName();

    String getDescription();

    TaskStatusEnum getStatus();

    Optional<Instant> getStartTime();

    long getDuration();

    Optional<Instant> getEndTime();

    String toString(String separator);

    int compareToEndTime(TaskBase o);

    Object clone(String name, String description);
    TaskTypeEnum getType();
}
