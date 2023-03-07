package dev.service;

import dev.domain.TaskBase;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

public class TaskPlanner extends LinkedList<TaskBase> {

    private static boolean getEquals(TaskBase o1, TaskBase o2) {
        return (
                o1.getStartTime().equals(o2.getStartTime())
                        || (
                        o1.getStartTime().get().isBefore(o2.getStartTime().get()) &&
                                o1.getEndTime().get().isAfter(o2.getStartTime().get()))
                        || (
                        o1.getStartTime().get().isAfter(o2.getStartTime().get()) &&
                                o1.getStartTime().get().isBefore(o2.getEndTime().get()))
        ) && (
                o1.getEndTime().equals(o2.getEndTime())
                        || (
                        o1.getEndTime().get().isAfter(o2.getEndTime().get()) &&
                                o1.getStartTime().get().isBefore(o2.getEndTime().get()))
                        || (
                        o1.getEndTime().get().isBefore(o2.getEndTime().get()) &&
                                o1.getEndTime().get().isAfter(o2.getStartTime().get()))
        );
    }

    @Override
    public boolean add(TaskBase task) {
        if (task.getStartTime().isPresent()) {
            return super.add(task);
        }
        return false;
    }

    public Optional<TaskBase> getCurrentTask(TaskBase o) {
        if (o.getStartTime().isEmpty()) return Optional.empty();
        Iterator<TaskBase> iterator = super.iterator();
        while (iterator.hasNext()) {
            TaskBase item = iterator.next();
            if (item.getTaskId() != o.getTaskId()) {
                if (getEquals(item, o)) {
                    return Optional.of(item);
                }
            }
        }
        return Optional.empty();
    }

    public boolean containsDate(TaskBase o) {
        Iterator<TaskBase> iterator = super.iterator();
        while (iterator.hasNext()) {
            TaskBase item = iterator.next();
            if (item.getTaskId() != o.getTaskId()) {
                if (getEquals(item, o)) {
                    return true;
                }
            }
        }
        return false;
    }
}