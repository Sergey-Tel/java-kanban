package dev.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;


public class Subtask extends Task {
    private final Integer parentEpicId;

    public Subtask(Integer parentEpicId, int taskId, String name, String description, TaskStatusEnum status,
                   int year, int month, int day, int hours, int minutes, long duration) {
        super(taskId, name, description, status, year, month, day, hours, minutes, duration);
        this.parentEpicId = parentEpicId;
        this.type = TaskTypeEnum.SUBTASK;
    }

    public Subtask(Integer parentEpicId, int taskId, String name, String description, TaskStatusEnum status,
                   Optional<Instant> startTime, long duration) {
        super(taskId, name, description, status, startTime, duration);
        this.parentEpicId = parentEpicId;
        this.type = TaskTypeEnum.SUBTASK;
    }

    public Subtask(Integer parentEpicId, int taskId, String name, String description) {
        super(taskId, name, description);
        this.parentEpicId = parentEpicId;
        this.type = TaskTypeEnum.SUBTASK;
    }

    public Subtask(Integer parentEpicId, int taskId, String name) {
        super(taskId, name);
        this.parentEpicId = parentEpicId;
        this.type = TaskTypeEnum.SUBTASK;
    }


    public Integer getEpicId() {
        return parentEpicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", taskId=" + this.getTaskId() + '\'' +
                ", status=" + this.getStatus().title +
                ", startTime=" + (this.getStartTime().isPresent() ?
                LocalDateTime.ofInstant(this.getStartTime().get(), ZoneId.systemDefault()) : "null") +
                ", duration=" + this.getDuration() +
                ", endTime=" + (this.getEndTime().isPresent() ?
                LocalDateTime.ofInstant(this.getEndTime().get(), ZoneId.systemDefault()) : "null") +
                ", epicTaskId=" + this.getEpicId() +
                '}';
    }

    @Override
    public String toString(String separator) {
        return toString(TaskTypeEnum.SUBTASK, getEpicId(), separator);
    }

    @Override
    public Object clone() {
        return new Subtask(this.parentEpicId, this.getTaskId(), this.getName(), this.getDescription(),
                this.status, this.startTime, this.duration.toMinutes());
    }

    @Override
    public Object clone(String name, String description) {
        return new Subtask(this.parentEpicId, this.getTaskId(), name, description,
                this.status, this.startTime, this.duration.toMinutes());
    }

    @Override
    public Object clone(TaskStatusEnum status) {
        return new Subtask(this.parentEpicId, this.getTaskId(), this.getName(), this.getDescription(), status,
                this.startTime, this.duration.toMinutes());
    }

    @Override
    public Object clone(String name, String description, TaskStatusEnum status) {
        return new Subtask(this.parentEpicId, this.getTaskId(), name, description,
                status, this.startTime, this.duration.toMinutes());
    }

    @Override
    public Object clone(long duration) {
        return new Subtask(this.parentEpicId, this.getTaskId(), this.getName(), this.getDescription(), this.status,
                this.startTime, duration);
    }

    @Override
    public Object clone(Optional<Instant> startTime) {
        return new Subtask(this.parentEpicId, this.getTaskId(), this.getName(), this.getDescription(), this.status,
                startTime, this.duration.toMinutes());
    }

    @Override
    public Object clone(int year, int month, int day, int hours, int minutes) {
        LocalDateTime startDateTime = LocalDateTime.of(year, month, day, hours, minutes, 0);
        return new Subtask(this.parentEpicId, this.getTaskId(), this.getName(), this.getDescription(), this.status,
                Optional.of(startDateTime.atZone(ZoneId.systemDefault()).toInstant()),
                this.duration.toMinutes());
    }

    @Override
    public Object clone(TaskStatusEnum status, int year, int month, int day, int hours, int minutes) {
        LocalDateTime startDateTime = LocalDateTime.of(year, month, day, hours, minutes, 0);
        return new Subtask(this.parentEpicId, this.getTaskId(), this.getName(), this.getDescription(), status,
                Optional.of(startDateTime.atZone(ZoneId.systemDefault()).toInstant()),
                this.duration.toMinutes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;

        Subtask subtask = (Subtask) o;

        if (getTaskId() != subtask.getTaskId()) return false;
        if (!getName().equals(subtask.getName())) return false;
        if (!getDescription().equals(subtask.getDescription())) return false;
        if (!getStatus().equals(subtask.getStatus())) return false;
        if (!getStartTime().equals(subtask.getStartTime())) return false;
        if (!this.duration.equals(subtask.duration)) return false;
        return parentEpicId.equals(subtask.parentEpicId);
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getTaskId();
        result = 31 * result + status.hashCode();
        result = 31 * result + startTime.hashCode();
        result = 31 * result + duration.hashCode();
        result = 31 * result + parentEpicId.hashCode();
        return result;
    }
}
