package dev.domain;

import java.util.Comparator;

public class TaskComparator implements Comparator<TaskBase> {

    @Override
    public int compare(TaskBase o1, TaskBase o2) {
        return o1.compareTo(o2);
    }
}
