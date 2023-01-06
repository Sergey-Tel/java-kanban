package dev.utils.menu;

import dev.domain.*;
import dev.service.TaskManager;
import dev.utils.CollectionUtils;

import java.util.Scanner;

import static dev.utils.menu.DialogsPrint.printTaskStatusMenu;
import static dev.utils.menu.MainMenu.getScanner;
import static dev.utils.menu.SubMenu.printIndexOutWarningMessage;
import static java.lang.System.out;

public class DialogsInput {
    public static final String EXIT_KEYS = "exit";
    public static final String BACK_KEYS = "back";
    public static final String WARNING_MESSAGE = "Внимание! Команды задаются числовыми значениями от 1 до 9,\n"
            + "либо последовательностью символов \"" + EXIT_KEYS + "\". Попробуйте еще раз!";
    public static final String INPUT_DESCRIPTION_CAPTION = "Описание: ";
    public static final String INPUT_STATUS_CAPTION = "Статус: ";
    static final String SUBMENU_THREE_WARNING_MESSAGE
            = "Внимание! Команды задаются числовыми значениями от 1 до 3,\n"
            + "либо последовательностью символов \"" + BACK_KEYS + "\". Попробуйте еще раз!";
    static final String SUBMENU_FIVE_WARNING_MESSAGE
            = "Внимание! Команды задаются числовыми значениями от 1 до 5,\n"
            + "либо последовательностью символов \"" + BACK_KEYS + "\". Попробуйте еще раз!";
    private static final String SUBMENU_THREE_ENTER_WARNING_MESSAGE
            = "Внимание! Команды задаются числовыми значениями от 1 до 3,\n"
            + "либо последовательностью символов \"" + BACK_KEYS + "\", либо командой `Ввод`. Попробуйте еще раз!";

    public static String inputText(String caption) {
        Scanner scanner = getScanner();
        out.print(caption);
        return scanner.nextLine().trim();
    }

    public static String inputText(String caption, String defaultText) {
        Scanner scanner = getScanner();
        out.println(caption + defaultText);
        out.println("Укажите новое значение или нажмите `Ввод` для сохранения прежнего:");
        String resultText = scanner.nextLine().trim();
        if (resultText.length() == 0) {
            return defaultText;
        } else {
            return resultText;
        }
    }

    public static Integer inputTaskId() {
        Scanner scanner = getScanner();
        out.print("Укажите идентификатор: ");
        if (scanner.hasNextInt()) {
            return Integer.parseInt(scanner.nextLine());
        } else {
            scanner.nextLine();
            out.println("Внимание! Идентификаторы задач задаются только числовыми значениями!");
            return null;
        }
    }

    public static TaskStatusEnum inputTaskStatus(TaskStatusEnum defaultStatus) {
        Scanner scanner = getScanner();
        while (true) {
            printTaskStatusMenu(defaultStatus);
            String command = scanner.nextLine().trim();
            switch (command) {
                case "1":
                    return TaskStatusEnum.NEW;
                case "2":
                    return TaskStatusEnum.IN_PROGRESS;
                case "3":
                    return TaskStatusEnum.DONE;
                case BACK_KEYS:
                case "":
                    return defaultStatus;
                default:
                    out.println(SUBMENU_THREE_ENTER_WARNING_MESSAGE);
                    out.println();
                    break;
            }
        }
    }

    public static TaskBase inputTask(TaskManager manager) {
        Integer taskId = inputTaskId();
        if (taskId != null) {
            if (manager.containsTaskBaseId(taskId)) {
                return manager.getTaskBase(taskId);
            } else {
                printIndexOutWarningMessage(taskId);
            }
        }
        return null;
    }

    public static void inputCreateTask(TaskManager manager) {
        String name = DialogsInput.inputText("Название задачи: ");
        String description = DialogsInput.inputText(INPUT_DESCRIPTION_CAPTION);
        int taskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Task task = new Task(taskId, name, description);
        manager.create(task);
        System.out.println(INPUT_STATUS_CAPTION + task.getStatus().title + ".");
        out.println("Созданной задаче присвоен идентификационный номер: " + task.getTaskId());
    }

    public static void inputUpdateTask(TaskManager manager) {
        Integer taskId = inputTaskId();
        if (taskId != null) {
            if (manager.containsTaskId(taskId)) {
                Task task = manager.getTask(taskId);
                inputUpdateTask(manager, task);
            } else {
                printIndexOutWarningMessage(taskId);
            }
        }
    }

    public static void inputUpdateTask(TaskManager manager, Task task) {
        Task cloneableTask = (Task) task.clone();
        String name = DialogsInput.inputText("Название (предыдущее значение): ", cloneableTask.getName());
        String description = DialogsInput.inputText("Описание (предыдущее значение): ", cloneableTask.getDescription());
        TaskStatusEnum status = inputTaskStatus(cloneableTask.getStatus());
        cloneableTask.setName(name);
        cloneableTask.setDescription(description);
        cloneableTask.setStatus(status);
        manager.update(cloneableTask);
        System.out.println("Задача № " + cloneableTask.getTaskId() + " успешно отредактирована.");
    }

    public static void inputCreateEpic(TaskManager manager) {
        String name = DialogsInput.inputText("Название эпик-задачи: ");
        String description = DialogsInput.inputText(INPUT_DESCRIPTION_CAPTION);
        int epicId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Epic epic = new Epic(epicId, name, description);
        manager.create(epic);
        System.out.println(INPUT_STATUS_CAPTION + epic.getStatus().title + ".");
        out.println("Созданной эпик-задаче присвоен идентификационный номер: " + epic.getTaskId());
    }

    public static void inputUpdateEpic(TaskManager manager) {
        Integer taskId = inputTaskId();
        if (taskId != null) {
            if (manager.containsEpicId(taskId)) {
                inputUpdateEpic(manager, taskId);
            } else {
                printIndexOutWarningMessage(taskId);
            }
        }
    }

    public static void inputUpdateEpic(TaskManager manager, Integer taskId) {
        if (manager.getEpic(taskId) != null) {
            Epic epic = (Epic) manager.getEpic(taskId).clone();
            String name = DialogsInput.inputText("Название (предыдущее значение): ", epic.getName());
            String description = DialogsInput.inputText("Описание (предыдущее значение): ", epic.getDescription());
            epic.setName(name);
            epic.setDescription(description);
            manager.update(epic);
            System.out.println("Эпик-задача № " + taskId + " успешно отредактирована.");
        } else {
            out.println("Внимание! Задание с идентификационным номером "
                    + taskId + " не является эпик-задачей.");
        }
    }

    public static void inputCreateSubtask(TaskManager manager) {
        Integer epicTaskId = inputTaskId();
        if (epicTaskId != null) {
            if (manager.containsEpicId(epicTaskId)) {
                inputCreateSubtask(manager, epicTaskId);
            } else {
                printIndexOutWarningMessage(epicTaskId);
            }
        }
    }

    public static void inputCreateSubtask(TaskManager manager, Integer epicId) {
        String name = DialogsInput.inputText("Название подзадачи: ");
        String description = DialogsInput.inputText(INPUT_DESCRIPTION_CAPTION);
        int taskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        SubTask subtask = new SubTask(epicId, taskId, name, description);
        manager.create(subtask);
        System.out.println(INPUT_STATUS_CAPTION + subtask.getStatus().title + ".");
        out.println("Созданной подзадаче присвоен идентификационный номер: " + subtask.getTaskId());
    }

    public static void inputUpdateSubtask(TaskManager manager) {
        Integer epicTaskId = inputTaskId();
        if (epicTaskId != null) {
            if (manager.containsEpicId(epicTaskId)) {
                Integer taskId = inputTaskId();
                if (taskId != null) {
                    if (manager.containsSubtaskId(taskId)) {
                        SubTask subtask = manager.getSubtask(taskId);
                        inputUpdateSubtask(manager, subtask);
                    } else {
                        printIndexOutWarningMessage(taskId);
                    }
                }
            } else {
                printIndexOutWarningMessage(epicTaskId);
            }
        }
    }

    public static void inputUpdateSubtask(TaskManager manager, SubTask subtask) {
        Task cloneableSubtask = (SubTask) subtask.clone();
        String name = DialogsInput.inputText("Название (предыдущее значение): ",
                cloneableSubtask.getName());
        String description = DialogsInput.inputText("Описание (предыдущее значение): ",
                cloneableSubtask.getDescription());
        TaskStatusEnum status = inputTaskStatus(cloneableSubtask.getStatus());
        cloneableSubtask.setName(name);
        cloneableSubtask.setDescription(description);
        cloneableSubtask.setStatus(status);
        manager.update(cloneableSubtask);
        System.out.println("Подзадача № " + cloneableSubtask.getTaskId() + " успешно отредактирована.");
    }
}
