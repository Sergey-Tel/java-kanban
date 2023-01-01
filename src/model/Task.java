package model;

import java.util.Objects;

import static model.StatusTracker.NEW;

public class Task {
    protected String name;
    protected String description;
    protected Integer id;
    protected String status;

    public Task(String name, String description, Integer id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = NEW;
    }

    public Task(String name, String description, Integer id, String status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        ;
        if (!(o instanceof Task)) {
            return false;
        }
        ;
        Task task = (Task) o;
        return Objects.equals(getName(), task.getName())
                && Objects.equals(getDescription(), task.getDescription())
                && Objects.equals(getId(), task.getId())
                && Objects.equals(getStatus(), task.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getId(), getStatus());
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }


}