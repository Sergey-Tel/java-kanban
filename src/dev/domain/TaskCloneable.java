package dev.domain;

import java.time.Instant;
import java.util.Optional;

public interface TaskCloneable {

    Object clone(TaskStatusEnum status);

    Object clone(String name, String description, TaskStatusEnum status);

    Object clone(long duration);

    Object clone(Optional<Instant> startTime);

    Object clone(int year, int month, int day, int hours, int minutes);

    Object clone(TaskStatusEnum status, int year, int month, int day, int hours, int minutes);

}
