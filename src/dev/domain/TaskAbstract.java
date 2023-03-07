package dev.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public abstract class TaskAbstract implements TaskBase {
    private final int taskId;
    protected TaskStatusEnum status;
    protected Optional<Instant> startTime;
    protected Duration duration;
    protected Optional<Instant> endTime;
    private final String name;
    private final String description;
    protected TaskTypeEnum type;


    public TaskAbstract(int taskId, String name, String description, TaskStatusEnum status, long duration) {
        this.taskId = taskId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = Optional.empty();
        this.duration = Duration.ofMinutes(duration);
        this.endTime = Optional.empty();
    }
    @Override
    public TaskTypeEnum getType() {
        return type;
    }

    public TaskAbstract(int taskId, String name, String description) {
        this(taskId, name, description, TaskStatusEnum.NEW, 0);
    }

    @Override
    public int getTaskId() {
        return taskId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public TaskStatusEnum getStatus() {
        return status;
    }

    @Override
    public Optional<Instant> getStartTime() {
        return startTime;
    }

    @Override
    public long getDuration() {
        return duration.toMinutes();
    }

    @Override
    public Optional<Instant> getEndTime() {
        return endTime;
    }

    @Override
    public abstract String toString();

    @Override
    public abstract String toString(String separator);

    protected String toString(TaskTypeEnum type, String separator) {
        return toString(type, 0, separator);
    }

    protected String toString(TaskTypeEnum type, Integer epicTaskId, String separator) {
        return String.format(
                "%d" + separator +
                        "%s" + separator +
                        "%s" + separator +
                        "%s" + separator +
                        "%d" + separator +
                        "%d" + separator +
                        "%s" + separator +
                        "%d\n",
                getTaskId(),
                type,
                getName(),
                getStatus().key,
                getStartTime().isPresent() ? getStartTime().get().toEpochMilli() : 0,
                getDuration(),
                getDescription(),
                epicTaskId);
    }

    @Override
    public abstract Object clone();

    @Override
    public abstract Object clone(String name, String description);

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public int compareTo(TaskBase o) {
        if (this.getStartTime().isPresent() && o.getStartTime().isPresent()) {
            return this.getStartTime().get().compareTo(o.getStartTime().get());
        } else if (this.getStartTime().isEmpty() && o.getStartTime().isPresent()) {
            return 1;
        } else if (this.getStartTime().isPresent() && o.getStartTime().isEmpty()) {
            return -1;
        } else {
            return Integer.compare(this.taskId, o.getTaskId());
        }
    }

    @Override
    public int compareToEndTime(TaskBase o) {
        if (this.getEndTime().isPresent() == o.getEndTime().isPresent() &&
                this.getStatus() == o.getStatus()) {
            if (this.getEndTime().isPresent()) {
                return this.getEndTime().get().compareTo(o.getEndTime().get());
            } else {
                return 0;
            }
        } else if (this.getEndTime().isEmpty() && this.getStatus() == TaskStatusEnum.DONE) {
            return 1;
        } else if (this.getEndTime().isEmpty() && this.getStatus() != TaskStatusEnum.DONE) {
            return -1;
        } else {
            if (this.getStatus() == TaskStatusEnum.DONE && o.getStatus() == TaskStatusEnum.DONE) {
                return -1;
            } else if (this.getStatus() != TaskStatusEnum.DONE && o.getStatus() != TaskStatusEnum.DONE) {
                return 1;
            } else if (this.getStatus() == TaskStatusEnum.DONE) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}