package dev.service;

import dev.domain.TaskBase;

public class InvalidTaskDateException extends RuntimeException {
    private final TaskBase currentTask;
    private final TaskBase errorTask;
    public InvalidTaskDateException(final String message, TaskBase currentTask, TaskBase newTask) {
        super(message);
        this.errorTask = newTask;
        this.currentTask = currentTask;
    }

    public TaskBase getCurrentTask(){
        return currentTask;
    }

    public TaskBase getTask(){
        return errorTask;
    }
}
