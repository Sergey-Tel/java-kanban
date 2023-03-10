package dev.utils.menu;

import dev.domain.TaskBase;
import dev.service.TasksManager;
import dev.utils.ReportUtils;
import dev.utils.TestUtil;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static dev.utils.menu.DialogsInput.*;
import static dev.utils.menu.DialogsPrint.printMainMenu;
import static dev.utils.menu.SubMenu.*;
import static java.lang.System.in;

public class MainMenu {
    private static Scanner scanner;

    public static Scanner getScanner() {
        if (scanner == null) {
            scanner = new Scanner(in);
        }
        return scanner;
    }

    public static void menu(TasksManager manager) throws IOException {
        Scanner scanner = getScanner();

        while (true) {
            printMainMenu();
            String command = scanner.nextLine().trim();
            switch (command) {
                case "0":
                    getPrioritizedTasks(manager);
                    break;
                case "1":
                    getAllTasks(manager);
                    break;
                case "2":
                    removeAllTasks(manager);
                    break;
                case "3":
                    getTask(manager);
                    break;
                case "4":
                    createTask(manager);
                    break;
                case "5":
                    updateTask(manager);
                    break;
                case "6":
                    removeTask(manager);
                    break;
                case "7":
                    getHistory(manager);
                    break;
                case "8":
                    TestUtil.testSprint3();
                    break;
                case "9":
                    TestUtil.testSprint4();
                    break;
                case "10":
                    TestUtil.testSprint5();
                case "11":
                    TestUtil.testSprint6();
                    break;
                case "12":
                    TestUtil.testSprint7();
                    break;
                case "13":
                    TestUtil.testSprint8();
                    break;
                case EXIT_KEYS:
                    System.out.println("Завершение работы приложения");
                    scanner.close();
                    return;
                default:
                    System.out.println(WARNING_MESSAGE);
                    System.out.println();
                    break;
            }
        }
    }

    static void getPrioritizedTasks(TasksManager manager) throws IOException {
        if (isNotNullTasks(manager)) {
            System.out.println("Список задач по приоритету:");
            List<TaskBase> tasks = manager.getPrioritizedTasks();
            ReportUtils.printTasksCollection(tasks, false);
        }
    }

    static void getAllTasks(TasksManager manager) throws IOException {
        if (isNotNullTasks(manager)) {
            System.out.println("Список всех задач:");
            List<TaskBase> tasks = manager.getHighLevelTasks();
            ReportUtils.printTasksCollection(tasks, true);
        }
    }

    static void removeAllTasks(TasksManager manager) {
        if (isNotNullTasks(manager)) {
            manager.removeAllTasks();
            System.out.println("Все задачи удалены!");
        }
    }

    static void getTask(TasksManager manager) throws IOException {
        if (isNotNullTasks(manager)) {
            System.out.println("Получение задачи по ее идентификатору");
            TaskBase task = inputTask(manager);
            if (task != null) {
                ReportUtils.printTask(task, true);
                System.out.println("=".repeat(ReportUtils.LINE_LENGTH));
            }
        }
    }

    static void createTask(TasksManager manager) {
        System.out.println("Создание задачи");
        menuCreateTask(manager);
    }

    static void updateTask(TasksManager manager) throws IOException {
        if (isNotNullTasks(manager)) {
            System.out.println("Обновление задачи");
            menuUpdateTask(manager);
        }
    }

    static void removeTask(TasksManager manager) throws IOException {
        if (isNotNullTasks(manager)) {
            System.out.println("Удаление задачи по идентификатору");
            menuRemoveTask(manager);
        }
    }

    static void getHistory(TasksManager manager) throws IOException {
        System.out.println("Вывод истории просмотра");
        List<TaskBase> tasks = manager.getHistoryManager().getHistory();
        ReportUtils.printTasksCollection(tasks, false);
    }

    static boolean isNotNullTasks(TasksManager manager) {
        if (manager.allSize() == 0) {
            System.out.println("Внимание! Задачи в трекере отсутствуют.");
            return false;
        } else {
            return true;
        }
    }
}
