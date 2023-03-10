package dev.utils;

import dev.domain.Epic;
import dev.domain.Subtask;
import dev.domain.Task;
import dev.domain.TaskBase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ReportUtils {
    public static final int LINE_LENGTH = 60;

    public static void printTask(TaskBase task, boolean printAttachSubtask) throws IOException {
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            System.out.println("=".repeat(LINE_LENGTH));
            printTask(epic, 0);
            List<Subtask> subtasks = epic.getAllSubtasks();
            if (printAttachSubtask && subtasks.size() > 0) {
                System.out.println("-".repeat(LINE_LENGTH));
                System.out.println(" ".repeat(4) + "Список подзадач:");
                for (TaskBase subtask : subtasks) {
                    System.out.println(" ".repeat(4) + "-".repeat(LINE_LENGTH - 4));
                    printTask(subtask, 4);
                }
            } else {
                System.out.println("Подзадачи отсутствуют.");
            }
        } else {
            System.out.println("=".repeat(LINE_LENGTH));
            printTask(task, 0);
        }
    }

    public static void printTask(TaskBase task, int margin) {
        System.out.print(" ".repeat(margin) + "Тип: ");
        if (task instanceof Epic) {
            System.out.println("Эпик-задача;");
        } else if (task instanceof Subtask) {
            System.out.println("Подзадача;");
            System.out.println(" ".repeat(margin) + "Идентификатор эпик-задачи: " +
                    ((Subtask) task).getEpicId() + ";");
        } else if (task instanceof Task) {
            System.out.println("Задача;");
        } else {
            System.out.println("-;");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        System.out.println(" ".repeat(margin) + "Название: " + task.getName() + ";");
        System.out.println(" ".repeat(margin) + "Описание: " + task.getDescription() + ";");
        System.out.println(" ".repeat(margin) + "Идентификатор: " + task.getTaskId() + ";");
        System.out.println(" ".repeat(margin) + "Статус: " + task.getStatus().title + ".");
        System.out.println(" ".repeat(margin) + "Дата начала: " + (task.getStartTime().isPresent() ?
                LocalDateTime.ofInstant(task.getStartTime().get(), ZoneId.systemDefault()).format(formatter) : "- ") + ".");
        System.out.println(" ".repeat(margin) + "Продолжительность: " + task.getDuration() + " мин.");
        System.out.println(" ".repeat(margin) + "Дата окончания: " + (task.getEndTime().isPresent() ?
                LocalDateTime.ofInstant(task.getEndTime().get(), ZoneId.systemDefault()).format(formatter) : "- ") + ".");
    }

    public static void printTasksCollection(List<TaskBase> tasks, boolean printAttachSubtask) throws IOException {
        if (tasks.size() > 0) {
            for (TaskBase task : tasks) {
                printTask(task, printAttachSubtask);
            }
            System.out.println("=".repeat(LINE_LENGTH));
        } else {
            System.out.println("Задачи в списке отсутствуют.");
        }
    }
}