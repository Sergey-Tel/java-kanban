package dev.domain;

public class Task extends AbstractTask {

    public Task(int taskId, String name, String description, TaskStatusEnum status) {
        super(taskId, name, description);
        this.status = status;
    }

    public Task(int taskId, String name, String description) {
        this(taskId, name, description, TaskStatusEnum.NEW);
    }

    public Task(int taskId, String name) {
        this(taskId, name, "", TaskStatusEnum.NEW);
    }

    public void setStatus(TaskStatusEnum status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", taskId=" + this.getTaskId() + '\'' +
                ", status=" + status.title +
                '}';
    }

    @Override
    public Object clone() {
        return new Task(this.getTaskId(), this.getName(), this.getDescription(), this.status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        if (getTaskId() != task.getTaskId()) return false;
        if (!getName().equals(task.getName())) return false;
        if (!getDescription().equals(task.getDescription())) return false;
        return status.equals(task.status);
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getTaskId();
        result = 31 * result + status.hashCode();
        return result;
    }
}