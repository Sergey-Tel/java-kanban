package dev.domain;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;



public class Task extends TaskAbstract implements TaskCloneable {

    public Task(int taskId, String name, String description, TaskStatusEnum status, Optional<Instant> startTime,
                long duration) {
        super(taskId, name, description, status, duration);
        this.status = status;
        this.startTime = startTime;
        endTime = startTime.map(instant -> instant.plusMillis(this.duration.toMillis()));
        this.type = TaskTypeEnum.TASK;
    }

    public Task(int taskId, String name, String description, TaskStatusEnum status,
                int year, int month, int day, int hours, int minutes,
                long duration) {
        this(taskId, name, description, status,
                Optional.of(LocalDateTime.of(year, month, day, hours, minutes)
                        .atZone(ZoneId.systemDefault()).toInstant()), duration);
        this.type = TaskTypeEnum.TASK;
    }

    public Task(int taskId, String name, String description) {
        this(taskId, name, description, TaskStatusEnum.NEW, Optional.empty(), 0);
        this.type = TaskTypeEnum.TASK;
    }

    public Task(int taskId, String name) {
        this(taskId, name, "", TaskStatusEnum.NEW, Optional.empty(), 0);
        this.type = TaskTypeEnum.TASK;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", taskId=" + this.getTaskId() + '\'' +
                ", status=" + this.getStatus().title +
                ", startTime=" + (this.getStartTime().isPresent() ?
                LocalDateTime.ofInstant(this.getStartTime().get(), ZoneId.systemDefault()) : "null") +
                ", duration=" + this.getDuration() +
                ", endTime=" + (this.getEndTime().isPresent() ?
                LocalDateTime.ofInstant(this.getEndTime().get(), ZoneId.systemDefault()) : "null") +
                '}';
    }

    @Override
    public String toString(String separator) {
        return toString(TaskTypeEnum.TASK, separator);
    }

    @Override
    public Object clone() {
        return new Task(this.getTaskId(), this.getName(), this.getDescription(), this.status, this.startTime,
                this.duration.toMinutes());
    }

    @Override
    public Object clone(String name, String description) {
        return new Task(this.getTaskId(), name, description, this.status, this.startTime,
                this.duration.toMinutes());
    }

    @Override
    public Object clone(TaskStatusEnum status) {
        return new Task(this.getTaskId(), this.getName(), this.getDescription(), status, this.startTime,
                this.duration.toMinutes());
    }

    @Override
    public Object clone(String name, String description, TaskStatusEnum status) {
        return new Task(this.getTaskId(), name, description, status, this.startTime,
                this.duration.toMinutes());
    }

    @Override
    public Object clone(long duration) {
        return new Task(this.getTaskId(), this.getName(), this.getDescription(), this.status, this.startTime,
                duration);
    }

    @Override
    public Object clone(Optional<Instant> startTime) {
        return new Task(this.getTaskId(), this.getName(), this.getDescription(), this.status, startTime,
                this.duration.toMinutes());
    }

    @Override
    public Object clone(int year, int month, int day, int hours, int minutes) {
        LocalDateTime startDateTime = LocalDateTime.of(year, month, day, hours, minutes, 0);
        return new Task(this.getTaskId(), this.getName(), this.getDescription(), this.status,
                Optional.of(startDateTime.atZone(ZoneId.systemDefault()).toInstant()),
                this.duration.toMinutes());
    }

    @Override
    public Object clone(TaskStatusEnum status, int year, int month, int day, int hours, int minutes) {
        LocalDateTime startDateTime = LocalDateTime.of(year, month, day, hours, minutes, 0);
        return new Task(this.getTaskId(), this.getName(), this.getDescription(), status,
                Optional.of(startDateTime.atZone(ZoneId.systemDefault()).toInstant()),
                this.duration.toMinutes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        if (getTaskId() != task.getTaskId()) return false;
        if (!getName().equals(task.getName())) return false;
        if (!getDescription().equals(task.getDescription())) return false;
        if (!getStatus().equals(task.getStatus())) return false;
        if (!getStartTime().equals(task.getStartTime())) return false;
        return this.duration.equals(task.duration);
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getTaskId();
        result = 31 * result + status.hashCode();
        result = 31 * result + startTime.hashCode();
        result = 31 * result + duration.hashCode();
        return result;
    }
}