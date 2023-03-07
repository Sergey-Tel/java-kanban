package dev.utils.menu;

import dev.domain.TaskStatusEnum;
import dev.domain.TaskTypeEnum;

import java.time.Instant;
import java.util.Optional;

import static dev.utils.menu.DialogsInput.BACK_KEYS;
import static dev.utils.menu.DialogsInput.EXIT_KEYS;
import static java.lang.System.out;

public class DialogsPrint {

    static void printMainMenu() {
        System.out.println();
        System.out.println("Основное меню:");
        System.out.println("0\tПолучение списка задач по приоритету;");
        System.out.println("1\tПолучение списка всех задач;");
        System.out.println("2\tУдаление всех задач;");
        System.out.println("3\tПолучение задачи по идентификатору;");
        System.out.println("4\tСоздание задачи;");
        System.out.println("5\tОбновление задачи по идентификатору;");
        System.out.println("6\tУдаление задачи по идентификатору;");
        System.out.println("7\tВывести историю просмотра;");
        System.out.println("8\tТестирование приложения по ТЗ (Спринт №3);");
        System.out.println("9\tТестирование приложения по ТЗ (Спринт №4);");
        System.out.println("10 \tТестирование приложения по ТЗ (Спринт №5);");
        System.out.println("11 \tТестирование приложения по ТЗ (Спринт №6);");
        System.out.println("12 \tТестирование приложения по ТЗ (Спринт №7);");
        System.out.println(EXIT_KEYS + "\tЗавершение работы приложения.");
    }

    public static void printEditMenu() {
        out.println();
        out.println("Выбор объекта для редактирования:");
        out.println("1\tРедактирование эпик-задачи;");
        out.println("2\tРедактирование подзадачи;");
        out.println("3\tСоздание подзадачи;");
        out.println("4\tУдаление подзадачи;");
        out.println("5\tУдаление всех подзадач;");
        out.println(BACK_KEYS + "\tВозврат в основное меню.");
    }

    public static void printTaskTypeMenu() {
        out.println();
        out.println("Укажите тип создаваемой задачи:");
        out.printf("%s\t%s;%n", TaskTypeEnum.TASK.key, TaskTypeEnum.TASK.title);
        out.printf("%s\t%s;%n", TaskTypeEnum.EPIC.key, TaskTypeEnum.EPIC.title);
        out.printf("%s\t%s;%n", TaskTypeEnum.SUBTASK.key, TaskTypeEnum.SUBTASK.title);
        out.println(BACK_KEYS + "\tВозврат в основное меню.");
    }

    static void printTaskStatusMenu(TaskStatusEnum defaultStatus) {
        out.println();
        out.println("Укажите статус задачи или нажмите `Ввод` для сохранения предыдущего:");
        TaskStatusEnum.printCommands();
        out.println(BACK_KEYS + " или `Ввод`\tПредыдущий статус: " + defaultStatus.title + ".");
    }

    static void printTaskStartTimeMenu(Optional<Instant> defaultStartTime) {
        out.println();
        out.println("Укажите дату, когда предполагается приступить к выполнению задачи или нажмите `Ввод` для сохранения предыдущего:");
        TaskStatusEnum.printCommands();
        //out.println(BACK_KEYS + " или `Ввод`\tПредыдущий статус: " + defaultStatus.title + ".");
    }

    static void printTaskDurationMenu(long defaultDuration) {
        out.println();
        out.println("Укажите продолжительность задачи, оценка того, сколько времени она займёт в минутах (число), ");
        out.println(BACK_KEYS + " или `Ввод`\tдля предыдущего значения: " + defaultDuration + " мин.");
    }
}
