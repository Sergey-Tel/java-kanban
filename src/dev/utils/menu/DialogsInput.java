package dev.utils.menu;

import dev.domain.*;
import dev.service.InvalidTaskDateException;
import dev.service.manager.TasksManager;
import dev.utils.CollectionUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;

import static dev.utils.menu.DialogsPrint.printTaskStatusMenu;
import static dev.utils.menu.MainMenu.getScanner;
import static dev.utils.menu.SubMenu.printIndexOutWarningMessage;
import static java.lang.System.out;

public class DialogsInput {
    public static final String EXIT_KEYS = "exit";
    public static final String BACK_KEYS = "back";
    public static final String WARNING_MESSAGE = "Внимание! Команды задаются числовыми значениями от 0 до 13,\n"
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

    public static Long inputTaskDuration() {
        Scanner scanner = getScanner();
        out.print("Укажите продолжительность задачи: ");
        if (scanner.hasNextInt()) {
            return Long.parseLong(scanner.nextLine());
        } else {
            scanner.nextLine();
            out.println("Внимание! Продолжительность задачи выражается только числовым значением в минутах!");
            return null;
        }
    }

    public static Optional<Instant> inputTaskStartTime(Optional<Instant> defaultTime) {
        Scanner scanner = getScanner();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String formatDateTime = now.format(formatter);
        out.print("Укажите дату предполагаемого начала выполнения задачи (в формате " +
                formatDateTime + ") или нажмите `Ввод` для сохранения прежнего значения:");
        String resultText = scanner.nextLine().trim();
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(resultText, formatter);
        } catch (Exception ex) {
            out.print("Дату предполагаемого начала выполнения задачи не определена!");
            return Optional.empty();
        }
        if (resultText.length() == 0) {
            return defaultTime;
        } else {
            return Optional.of(dateTime.atZone(ZoneId.systemDefault()).toInstant());
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

    public static TaskBase inputTask(TasksManager manager) {
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

    public static void inputCreateTask(TasksManager manager) {
        String name = DialogsInput.inputText("Название задачи: ");
        String description = DialogsInput.inputText(INPUT_DESCRIPTION_CAPTION);
        int taskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Task task = new Task(taskId, name, description);
        try {
            manager.create(task);
            System.out.println(INPUT_STATUS_CAPTION + task.getStatus().title + ".");
            out.println("Созданной задаче присвоен идентификационный номер: " + task.getTaskId());
        } catch (InvalidTaskDateException ex) {
            out.println(ex.getMessage());
        }
    }

    public static void inputUpdateTask(TasksManager manager) {
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

    public static void inputUpdateTask(TasksManager manager, Task task) {
        String name = DialogsInput.inputText("Название (предыдущее значение): ", task.getName());
        String description = DialogsInput.inputText("Описание (предыдущее значение): ", task.getDescription());
        TaskStatusEnum status = inputTaskStatus(task.getStatus());
        Optional<Instant> startTime = inputTaskStartTime(task.getStartTime());
        long duration = inputTaskDuration();
        Task updaterTask = (Task) task.clone(name, description, status);
        updaterTask = (Task) updaterTask.clone(startTime);
        updaterTask = (Task) updaterTask.clone(duration);
        try {
            manager.update(updaterTask);
            System.out.println("Задача № " + task.getTaskId() + " успешно отредактирована.");
        } catch (InvalidTaskDateException ex) {
            out.println(ex.getMessage());
        }
    }

    public static void inputCreateEpic(TasksManager manager) {
        String name = DialogsInput.inputText("Название эпик-задачи: ");
        String description = DialogsInput.inputText(INPUT_DESCRIPTION_CAPTION);
        int epicId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Epic epic = new Epic(epicId, name, description);
        manager.create(epic);
        System.out.println(INPUT_STATUS_CAPTION + epic.getStatus().title + ".");
        out.println("Созданной эпик-задаче присвоен идентификационный номер: " + epic.getTaskId());
    }

    public static void inputUpdateEpic(TasksManager manager) throws IOException {
        Integer taskId = inputTaskId();
        if (taskId != null) {
            if (manager.containsEpicId(taskId)) {
                inputUpdateEpic(manager, taskId);
            } else {
                printIndexOutWarningMessage(taskId);
            }
        }
    }

    public static void inputUpdateEpic(TasksManager manager, Integer taskId) throws IOException {
        if (manager.getEpic(taskId) != null) {
            Epic epic = manager.getEpic(taskId);
            String name = DialogsInput.inputText("Название (предыдущее значение): ", epic.getName());
            String description = DialogsInput.inputText("Описание (предыдущее значение): ", epic.getDescription());
            manager.update((Epic) epic.clone(name, description));
            System.out.println("Эпик-задача № " + taskId + " успешно отредактирована.");
        } else {
            out.println("Внимание! Задание с идентификационным номером "
                    + taskId + " не является эпик-задачей.");
        }
    }

    public static void inputCreateSubtask(TasksManager manager) {
        Integer epicTaskId = inputTaskId();
        if (epicTaskId != null) {
            if (manager.containsEpicId(epicTaskId)) {
                inputCreateSubtask(manager, epicTaskId);
            } else {
                printIndexOutWarningMessage(epicTaskId);
            }
        }
    }

    public static void inputCreateSubtask(TasksManager manager, Integer epicId) {
        String name = DialogsInput.inputText("Название подзадачи: ");
        String description = DialogsInput.inputText(INPUT_DESCRIPTION_CAPTION);
        int taskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Subtask subtask = new Subtask(epicId, taskId, name, description);
        try {
            manager.create(subtask);
            System.out.println(INPUT_STATUS_CAPTION + subtask.getStatus().title + ".");
            out.println("Созданной подзадаче присвоен идентификационный номер: " + subtask.getTaskId());
        } catch (InvalidTaskDateException | IOException ex) {
            out.println(ex.getMessage());
        }
    }

    public static void inputUpdateSubtask(TasksManager manager) {
        Integer epicTaskId = inputTaskId();
        if (epicTaskId != null) {
            if (manager.containsEpicId(epicTaskId)) {
                Integer taskId = inputTaskId();
                if (taskId != null) {
                    if (manager.containsSubtaskId(taskId)) {
                        Subtask subtask = manager.getSubtask(taskId);
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

    public static void inputUpdateSubtask(TasksManager manager, Subtask subtask) {
        String name = DialogsInput.inputText("Название (предыдущее значение): ",
                subtask.getName());
        String description = DialogsInput.inputText("Описание (предыдущее значение): ",
                subtask.getDescription());
        TaskStatusEnum status = inputTaskStatus(subtask.getStatus());
        Optional<Instant> startTime = inputTaskStartTime(subtask.getStartTime());
        long duration = inputTaskDuration();
        Subtask updaterSubtask = (Subtask) subtask.clone(name, description, status);
        updaterSubtask = (Subtask) updaterSubtask.clone(startTime);
        updaterSubtask = (Subtask) updaterSubtask.clone(duration);
        try {
            manager.update(updaterSubtask);
            System.out.println("Подзадача № " + subtask.getTaskId() + " успешно отредактирована.");
        } catch (InvalidTaskDateException | IOException ex) {
            out.println(ex.getMessage());
        }
    }
}