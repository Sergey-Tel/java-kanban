package dev.utils.menu;

import dev.domain.TaskBase;
import dev.service.Managers;
import dev.service.TaskManager;
import dev.utils.ReportUtils;
import dev.utils.TestUtil;

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

    public static void menu(TaskManager manager) {
        Scanner scanner = getScanner();

        while (true) {
            printMainMenu();
            String command = scanner.nextLine().trim();
            switch (command) {
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
                    getHistory();
                    break;
                case "8":
                    TestUtil.testSprint3(manager);
                    break;
                case "9":
                    TestUtil.testSprint4(manager);
                    break;
                case "10":
                    TestUtil.testSprint5(manager);
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

    static void getAllTasks(TaskManager manager) {
        if (isNotNullTasks(manager)) {
            System.out.println("Список всех задач:");
            List<TaskBase> tasks = manager.getHighLevelTasks();
            ReportUtils.printTasksCollection(tasks, true);
        }
    }

    static void removeAllTasks(TaskManager manager) {
        if (isNotNullTasks(manager)) {
            manager.removeAllTasks();
            System.out.println("Все задачи удалены!");
        }
    }

    static void getTask(TaskManager manager) {
        if (isNotNullTasks(manager)) {
            System.out.println("Получение задачи по ее идентификатору");
            TaskBase task = inputTask(manager);
            if (task != null) {
                ReportUtils.printTask(task, true);
                System.out.println("=".repeat(ReportUtils.LINE_LENGTH));
            }
        }
    }

    static void createTask(TaskManager manager) {
        System.out.println("Создание задачи");
        menuCreateTask(manager);
    }

    static void updateTask(TaskManager manager) {
        if (isNotNullTasks(manager)) {
            System.out.println("Обновление задачи");
            menuUpdateTask(manager);
        }
    }

    static void removeTask(TaskManager manager) {
        if (isNotNullTasks(manager)) {
            System.out.println("Удаление задачи по идентификатору");
            menuRemoveTask(manager);
        }
    }

    static void getHistory() {
        System.out.println("Вывод истории просмотра");
        List<TaskBase> tasks = Managers.getDefaultHistory().getHistory();
        ReportUtils.printTasksCollection(tasks, false);
    }

    static boolean isNotNullTasks(TaskManager manager) {
        if (manager.allSize() == 0) {
            System.out.println("Внимание! Задачи в трекере отсутствуют.");
            return false;
        } else {
            return true;
        }
    }
}
