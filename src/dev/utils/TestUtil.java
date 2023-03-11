package dev.utils;

import dev.domain.*;
import dev.service.manager.FileBackedTasksManager;
import dev.service.InvalidTaskDateException;
import dev.service.server.Managers;
import dev.service.manager.TasksManager;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TestUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");


    public static void testSprint3() throws IOException {
        Managers.setMemoryTasksManager();
        TasksManager manager = Managers.getDefault();

        System.out.println("Тестирование приложения по условиям, заданным в техническом задании Спринта №3:");

        System.out.println("\n1.\tСоздайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей\n"
                + "Распечатайте списки эпиков, задач и подзадач, через System.out.println(..).");

        int nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Task shoppingTask = new Task(nextTaskId, "Купить батарейки!", "Необходимо 4 шт.",
                TaskStatusEnum.NEW, 2020, 1, 1, 18, 5, 45);
        manager.create(shoppingTask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Task lessonTask = new Task(nextTaskId, "Проверить уроки!", "В дневнике задание.",
                TaskStatusEnum.NEW, 2020, 2, 1, 19, 5, 20);
        manager.create(lessonTask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Epic removalEpic = new Epic(nextTaskId,
                "Переезд", "Снять наличные деньги");
        manager.create(removalEpic);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Subtask callSubtask = new Subtask(removalEpic.getTaskId(), nextTaskId,
                "Заказать машину", "Газели будет достаточно.",
                TaskStatusEnum.NEW, 2020, 3, 1, 10, 0, 20);
        removalEpic.create(callSubtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Subtask packSubtask = new Subtask(removalEpic.getTaskId(), nextTaskId,
                "Упаковать коробки", "5-6 коробок должно хватить.",
                TaskStatusEnum.NEW, 2020, 4, 2, 11, 5, 200);
        removalEpic.create(packSubtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Subtask tapeSubtask = new Subtask(removalEpic.getTaskId(), nextTaskId,
                "Купить скотч", "20 метров.");
        removalEpic.create(tapeSubtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Epic epicParty = new Epic(nextTaskId, "Вечеринка", "Поздравление с праздником в коллективе");
        manager.create(epicParty);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Subtask barSubtask = new Subtask(epicParty.getTaskId(), nextTaskId,
                "Купить цветы и шампанское", "К 2-3 бутылкам шампанского нужно взять 2 коробки шоколадных конфет и фрукты.");
        epicParty.create(barSubtask);

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks(), true);

        System.out.println("\n2.\tИзмените статусы созданных объектов, распечатайте. Проверьте, что статус задачи\n"
                + "и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.");

        Task updateShoppingTask = (Task) manager.getTask(0).clone(TaskStatusEnum.DONE);
        manager.update(updateShoppingTask);

        Subtask updateCallSubtask = (Subtask) manager.getSubtask(3).clone(TaskStatusEnum.IN_PROGRESS);
        removalEpic.update(updateCallSubtask);

        Subtask updatePackSubtask = (Subtask) manager.getSubtask(4).clone(TaskStatusEnum.DONE);
        removalEpic.update(updatePackSubtask);

        Subtask updateBarSubtask = (Subtask) manager.getSubtask(5).clone(
                TaskStatusEnum.DONE, 2022, 5, 1, 1, 1);
        removalEpic.update(updateBarSubtask);

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks(), true);

        System.out.println("\n3.\tПопробуйте удалить одну из задач и один из эпиков.");
        manager.removeTask(0);
        manager.removeTask(epicParty.getTaskId());

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks(), true);

        System.out.println("\n4.\t Печать по отдельным категориям. Только задачи:");
        ReportUtils.printTasksCollection(manager.getTasks().stream()
                .map(t -> (TaskBase) t)
                .collect(Collectors.toList()), true);

        System.out.println("\n5.\t Печать по отдельным категориям. Только эпики:");
        ReportUtils.printTasksCollection(manager.getEpics().stream()
                .map(t -> (TaskBase) t)
                .collect(Collectors.toList()), true);

        System.out.println("\n6.\t Печать по отдельным категориям. Только подзадачи:");
        ReportUtils.printTasksCollection(manager.getSubtasks().stream()
                .map(t -> (TaskBase) t)
                .collect(Collectors.toList()), true);

        System.out.println("Тест по ТЗ №3 выполнен.");
    }


    public static void testSprint4() throws IOException {
        Managers.setMemoryTasksManager();
        TasksManager manager = Managers.getDefault();

        System.out.println("Тестирование приложения по условиям, заданным в техническом задании Спринта №4:");

        System.out.println("\n1.\tСоздайте несколько задач разного типа.");

        int nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Task shoppingTask = new Task(nextTaskId, "Задача 1", "Создаю обычную задачу с индексом 0.");
        manager.create(shoppingTask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Epic repairEpic = new Epic(nextTaskId,
                "Эпик-задача 1", "Создаю пустую эпик-задачу с индексом 1.");
        manager.create(repairEpic);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Subtask subtask = new Subtask(repairEpic.getTaskId(), nextTaskId,
                "Подзадача 1", "Создаю подзадачу с индексом 2. Метод: epic.create(subtask)");
        repairEpic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new Subtask(repairEpic.getTaskId(), nextTaskId,
                "Подзадача 2",
                "Создаю подзадачу с индексом 3. Метод: epic.create(subtask)",
                TaskStatusEnum.DONE, Optional.of(Instant.now()), 15);
        repairEpic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new Subtask(repairEpic.getTaskId(), nextTaskId, "Подзадача 3",
                "Создаю подзадачу с индексом 4. Метод: manager.create(subtask)");
        manager.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new Subtask(repairEpic.getTaskId(), nextTaskId,
                "Подзадача 4", "Создаю подзадачу с индексом 5. Метод: manager.create(subtask)");
        manager.create(subtask);

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks(), true);

        System.out.println("\n2.\tВызываем разные методы интерфейса TaskManager\n" +
                "и напечатаем историю просмотров после каждого вызова");

        System.out.println("\nВызываем задание № 0 и меняем его статус.");
        Task updateShoppingTask = (Task) manager.getTask(0).clone(TaskStatusEnum.DONE);
        manager.update(updateShoppingTask);

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks(), true);

        System.out.println("\nПечатаем историю просмотра. (ТЗ №5: Ограничения на 10 позиций больше нет!)");
        ReportUtils.printTasksCollection(manager.getHistoryManager().getHistory(), false);

        System.out.println("\nВызываем задания 12 раз в цикле по одному.");
        List<Integer> taskIsCollection = manager.getAllTaskId();
        int index = 0;
        for (int i = 0; i < 12; i++) {
            if (index >= taskIsCollection.size()) {
                index = 0;
            }
            TaskBase task = manager.getTaskBase(taskIsCollection.get(index));
            System.out.print((i + 1) + ") ");
            ReportUtils.printTask(task, false);
            index++;
        }

        System.out.println("\nПечатаем историю просмотра. (ТЗ №5: Ограничения на 10 позиций больше нет!)");
        ReportUtils.printTasksCollection(manager.getHistoryManager().getHistory(), false);

        System.out.println("Тест по ТЗ №4 выполнен.");
    }


    public static void testSprint5() throws IOException {
        Managers.setMemoryTasksManager();
        TasksManager manager = Managers.getDefault();

        manager.removeAllTasks();
        System.out.println("Тестирование приложения по условиям, заданным в техническом задании Спринта №5:");

        System.out.println("\n1.\tСоздайте две задачи, эпик с тремя подзадачами и эпик без подзадач;");

        int nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Task task = new Task(nextTaskId, "Задача 1", "Создаю обычную задачу с индексом 0.");
        manager.create(task);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        task = new Task(nextTaskId, "Задача 2", "Создаю обычную задачу с индексом 1.");
        manager.create(task);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Epic epic = new Epic(nextTaskId,
                "Эпик-задача 1", "Создаю эпик-задачу с индексом 2, в которой будет создано три подзадачи.");
        manager.create(epic);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Subtask subtask = new Subtask(epic.getTaskId(), nextTaskId,
                "Подзадача 1", "Создаю подзадачу с индексом 3.");
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new Subtask(epic.getTaskId(), nextTaskId,
                "Подзадача 2", "Создаю подзадачу с индексом 4.");
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new Subtask(epic.getTaskId(), nextTaskId,
                "Подзадача 3", "Создаю подзадачу с индексом 5.");
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        epic = new Epic(nextTaskId,
                "Эпик-задача 2", "Создаю эпик-задачу с индексом 6 без подзадач.");
        manager.create(epic);

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks(), true);

        System.out.println("\n2.\tЗапросите созданные задачи несколько раз в разном порядке;");
        System.out.println("\nВызываю задачи 20 раз в случайном порядке.");
        for (int i = 0; i < 20; i++) {
            int randomId = (int)(Math.random() * 7);
            TaskBase randomTask = manager.getTaskBase(randomId);
            System.out.print((i + 1) + ") ");
            ReportUtils.printTask(randomTask, true);
        }

        System.out.println("\n3.\tПосле каждого запроса выведите историю и убедитесь, что в ней нет повторов;");

        System.out.println("\nПечатаем историю просмотра.");
        ReportUtils.printTasksCollection(manager.getHistoryManager().getHistory(), false);

        System.out.println("\n4.\tУдалите задачу, которая есть в истории, и проверьте," +
                " что при печати она не будет выводиться;");

        manager.removeTask(0);
        System.out.println("\nУдалили задачу с идентификатором 0.");

        System.out.println("\nПечатаем историю просмотра.");
        ReportUtils.printTasksCollection(manager.getHistoryManager().getHistory(), false);

        System.out.println("\n5.\tУдалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик," +
                " так и все его подзадачи.");

        manager.removeTask(2);
        System.out.println("\nУдалили эпик с идентификатором 2.");

        System.out.println("\nПечатаем историю просмотра.");
        ReportUtils.printTasksCollection(manager.getHistoryManager().getHistory(), false);

        System.out.println("Тест по ТЗ №5 выполнен.");
    }


    public static void testSprint6() throws IOException {
        System.out.println("Тестирование приложения по условиям, заданным в техническом задании Спринта №6:");
        Path path;
        try {
            path = FileSystems.getDefault().getPath("java-kanban.csv");
            if (path.toFile().exists()) {
                path.toFile().delete();
            }
            path.toFile().createNewFile();
            Managers.setFileTasksManager(path.toFile());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return;
        }
        TasksManager manager = Managers.getDefault();

        System.out.println("\n1.\tЗаведите несколько разных задач, эпиков и подзадач;");
        int nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Task task = new Task(nextTaskId, "Задача 1", "Создаю обычную задачу с индексом 0.",
                TaskStatusEnum.NEW, 2022, 6, 30, 9, 0, 5);
        manager.create(task);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        task = new Task(nextTaskId, "Задача 2", "Создаю обычную задачу с индексом 1.");
        manager.create(task);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Epic epic = new Epic(nextTaskId,
                "Эпик-задача 1", "Создаю эпик-задачу с индексом 2, в которой будет создано три подзадачи.");
        manager.create(epic);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Subtask subtask = new Subtask(epic.getTaskId(), nextTaskId,
                "Подзадача 1", "Создаю подзадачу с индексом 3.",
                TaskStatusEnum.NEW, 2022, 6, 30, 10, 0, 58);
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new Subtask(epic.getTaskId(), nextTaskId,
                "Подзадача 2", "Создаю подзадачу с индексом 4.");
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new Subtask(epic.getTaskId(), nextTaskId,
                "Подзадача 3", "Создаю подзадачу с индексом 5.",
                TaskStatusEnum.NEW, 2022, 7, 1, 11, 0, 20);
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        epic = new Epic(nextTaskId,
                "Эпик-задача 2", "Создаю эпик-задачу с индексом 6 без подзадач.");
        manager.create(epic);

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks(), false);

        System.out.println("\n2.\tЗапросите некоторые из них, чтобы заполнилась история просмотра;");
        System.out.println("\nВызываю задачи 20 раз в случайном порядке.");
        for (int i = 0; i < 20; i++) {
            int randomId = (int) (Math.random() * 7);
            TaskBase randomTask = manager.getTaskBase(randomId);
            System.out.print((i + 1) + ") ");
            ReportUtils.printTask(randomTask, false);
        }
        System.out.println("\nПечатаем историю просмотра.");
        ReportUtils.printTasksCollection(manager.getHistoryManager().getHistory(), false);

        System.out.println("\n3.\tСоздаем новый FileBackedTasksManager менеджер из этого же файла.;");
        manager = FileBackedTasksManager.loadFromFile(path.toFile());

        System.out.println("\nПечатаем историю просмотра.");
        ReportUtils.printTasksCollection(manager.getHistoryManager().getHistory(), false);
    }

    public static void testSprint7() throws IOException {
        System.out.println("Тестирование приложения по условиям, заданным в техническом задании Спринта №7:");
        Path path;
        try {
            path = FileSystems.getDefault().getPath("java-kanban.csv");
            if (path.toFile().exists()) {
                path.toFile().delete();
            }
            path.toFile().createNewFile();
            Managers.setFileTasksManager(path.toFile());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return;
        }
        testSprint7_8();
        System.out.println("Тест по ТЗ №7 выполнен.");
    }

    public static void testSprint8() throws IOException {
        System.out.println("Тестирование приложения по условиям, заданным в техническом задании Спринта №8 (клиент-сервер):");

        Managers.setHttpTaskManager("http://localhost", 8078);

        testSprint7_8();
        System.out.println("Тест по ТЗ №8 выполнен.");
    }

    private static void testSprint7_8() throws IOException {
        TasksManager manager = Managers.getDefault();

        System.out.println("\n1.\tСоздам две задачи и эпик с тремя подзадачами;");

        int nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Task task = new Task(nextTaskId, "Задача 1", "Создаю обычную задачу с индексом 0.",
                TaskStatusEnum.NEW, 2022, 7, 4, 15, 0, 10);
        manager.create(task);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        task = new Task(nextTaskId, "Задача 2", "Создаю обычную задачу с индексом 1.");
        manager.create(task);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Epic epic = new Epic(nextTaskId,
                "Эпик-задача 1", "Создаю эпик-задачу с индексом 2, в которой будет создано три подзадачи.");
        manager.create(epic);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Subtask subtask = new Subtask(epic.getTaskId(), nextTaskId,
                "Подзадача 1", "Создаю подзадачу с индексом 3.",
                TaskStatusEnum.IN_PROGRESS, 2022, 6, 30, 9, 30, 45);
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new Subtask(epic.getTaskId(), nextTaskId,
                "Подзадача 2", "Создаю подзадачу с индексом 4. Если нет конфликта, то это ошибка!",
                TaskStatusEnum.NEW, 2022, 6, 30, 9, 0, 245);

        try {
            epic.create(subtask);
        } catch (InvalidTaskDateException ex) {
            System.out.println("Ошибка! " + ex.getMessage() + "\n" +
                    "Создаваемое задание: '" + ex.getTask().getName() +
                    "' (" +
                    LocalDateTime.ofInstant(ex.getTask().getStartTime().get(), ZoneId.systemDefault()).format(formatter) +
                    " - " +
                    LocalDateTime.ofInstant(ex.getTask().getEndTime().get(), ZoneId.systemDefault()).format(formatter) +
                    ")\nпересекается с существующим заданием: '" + ex.getCurrentTask().getName() +
                    "' (" +
                    LocalDateTime.ofInstant(ex.getCurrentTask().getStartTime().get(), ZoneId.systemDefault()).format(formatter) +
                    " - " +
                    LocalDateTime.ofInstant(ex.getCurrentTask().getEndTime().get(), ZoneId.systemDefault()).format(formatter) +
                    ")");
        }

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new Subtask(epic.getTaskId(), nextTaskId,
                "Подзадача 2.1", "Создаю подзадачу с индексом 4(?)",
                TaskStatusEnum.NEW, 2022, 5, 30, 9, 0, 245);
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new Subtask(epic.getTaskId(), nextTaskId,
                "Подзадача 3", "Создаю подзадачу с индексом 5.",
                TaskStatusEnum.DONE, 2022, 6, 29, 16, 15, 17);
        epic.create(subtask);

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks(), true);

        System.out.println("\n2.\tВыведу созданные задачи в порядке приоритетности;");

        ReportUtils.printTasksCollection(Managers.getDefault().getPrioritizedTasks(), false);
    }
}