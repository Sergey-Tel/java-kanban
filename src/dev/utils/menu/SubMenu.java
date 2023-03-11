package dev.utils.menu;

import dev.domain.*;
import dev.service.manager.TasksManager;

import java.io.IOException;
import java.util.Scanner;

import static dev.utils.menu.DialogsInput.*;
import static dev.utils.menu.DialogsPrint.printEditMenu;
import static dev.utils.menu.DialogsPrint.printTaskTypeMenu;
import static dev.utils.menu.MainMenu.getScanner;
import static java.lang.System.out;

public class SubMenu {

    public static void menuUpdateTask(TasksManager manager) throws IOException {
        Integer taskId = inputTaskId();
        if (taskId != null) {
            if (manager.containsTaskBaseId(taskId)) {
                TaskBase taskBase = manager.getTaskBase(taskId);
                if (taskBase instanceof Epic) {
                    Epic epic = (Epic) ((Epic) taskBase).clone();
                    menuEditEpic(epic, manager);
                    manager.update(epic);
                } else if (taskBase instanceof Subtask) {
                    DialogsInput.inputUpdateSubtask(manager, (Subtask) taskBase);
                } else if (taskBase instanceof Task) {
                    DialogsInput.inputUpdateTask(manager, (Task) taskBase);
                }
            } else {
                printIndexOutWarningMessage(taskId);
            }
        }
    }

    public static void menuCreateTask(TasksManager manager) {
        Scanner scanner = getScanner();
        while (true) {
            printTaskTypeMenu();
            String command = scanner.nextLine().trim();
            switch (command) {
                case "1":
                    DialogsInput.inputCreateTask(manager);
                    return;
                case "2":
                    DialogsInput.inputCreateEpic(manager);
                    return;
                case "3":
                    if (manager.epicSize() == 0) {
                        out.println("Внимание! Подзадача является частью эпик-задачи," +
                                " вместе с тем эпик-задачи в трекере отсутствуют.");
                        break;
                    } else {
                        out.println("Выбор эпик-задачи:");
                        DialogsInput.inputCreateSubtask(manager);
                        return;
                    }
                case BACK_KEYS:
                    return;
                default:
                    out.println(SUBMENU_THREE_WARNING_MESSAGE);
                    out.println();
                    break;
            }
        }
    }

    public static void menuEditEpic(Epic epic, TasksManager manager) throws IOException {
        Scanner scanner = getScanner();
        while (true) {
            printEditMenu();
            String command = scanner.nextLine().trim();
            switch (command) {
                case "1": {
                    String name = DialogsInput.inputText("Название эпик-задачи (предыдущее значение): ", epic.getName());
                    String description = DialogsInput.inputText("Описание (предыдущее значение): ", epic.getDescription());
                    manager.update((Epic) epic.clone(name, description));
                    out.println("Эпик-задача № " + epic.getTaskId() + " успешно отредактирована.");
                    return;
                }
                case "2":
                    if (epic.size() == 0) {
                        out.println("Внимание! Подзадачи в эпик-задаче отсутствуют.");
                    } else {
                        out.println("Выбор подзадачи:");
                        Integer taskId = inputTaskId();
                        if (taskId == null) break;
                        if (epic.containsSubtaskId(taskId)) {
                            Subtask subtask = epic.getSubtask(taskId);
                            String name = DialogsInput.inputText("Название подзадачи (предыдущее значение): ", subtask.getName());
                            String description = DialogsInput.inputText("Описание (предыдущее значение): ", subtask.getDescription());
                            TaskStatusEnum status = inputTaskStatus(subtask.getStatus());
                            subtask =(Subtask) subtask.clone(name, description, status);
                            epic.update(subtask);
                            System.out.println("Подзадача номер: " + subtask.getTaskId() + " успешно отредактирована.");
                            return;
                        } else {
                            printIndexOutWarningMessage(taskId);
                        }
                    }
                    break;
                case "3":
                    DialogsInput.inputCreateSubtask(manager, epic.getTaskId());
                    return;
                case "4":
                    if (epic.size() == 0) {
                        out.println("Внимание! Подзадачи в эпик-задаче отсутствуют.");
                    } else {
                        Integer taskId = inputTaskId();
                        if (taskId != null) {
                            if (epic.containsSubtaskId(taskId)) {
                                epic.removeSubtask(taskId);
                                System.out.println("Подзадача № " + taskId + " успешно удалена!");
                                return;
                            } else {
                                printIndexOutWarningMessage(taskId);
                            }
                        }
                    }
                    break;
                case "5":
                    if (epic.size() == 0) {
                        out.println("Внимание! Подзадачи в эпик-задаче отсутствуют.");
                        break;
                    } else {
                        epic.removeAllTasks();
                        System.out.println("Все подзадачи удалены!");
                        return;
                    }
                case BACK_KEYS:
                    return;
                default:
                    out.println(SUBMENU_FIVE_WARNING_MESSAGE);
                    out.println();
                    break;
            }
        }
    }

    public static void menuRemoveTask(TasksManager manager) throws IOException {
        Integer taskId = inputTaskId();
        if (taskId != null) {
            if (manager.containsTaskBaseId(taskId)) {
                manager.removeTask(taskId);
                System.out.println("Задача № " + taskId + " успешно удалена!");
            } else {
                printIndexOutWarningMessage(taskId);
            }
        }
    }

    public static void printIndexOutWarningMessage(int taskId) {
        out.println("Внимание! Заданный идентификационный номер задачи ("
                + taskId + ") отсутствует в трекере.");
    }
}
