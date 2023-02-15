package dev.utils;

import dev.domain.*;
import dev.service.TaskManager;

import java.util.List;
import java.util.stream.Collectors;

public final class TestUtil {


    public static void testSprint3(TaskManager manager) {
        manager.removeAllTasks();
        System.out.println("Тестирование приложения по условиям, заданным в техническом задании Спринта №3:");

        System.out.println("\n1.\tСоздайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей\n"
                + "Распечатайте списки эпиков, задач и подзадач, через System.out.println(..).");

        int nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Task shoppingTask = new Task(nextTaskId, "Купить батарейки!", "Необходимо 4 шт.");
        manager.create(shoppingTask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Task lessonTask = new Task(nextTaskId, "Проверить уроки!");
        lessonTask.setDescription("В дневнике задание.");
        manager.create(lessonTask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Epic removalEpic = new Epic(nextTaskId,
                "Переезд", "Снять наличные деньги");
        manager.create(removalEpic);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        SubTask callSubtask = new SubTask(removalEpic.getTaskId(), nextTaskId,
                "Заказать машину", "Газели будет достаточно.");
        removalEpic.create(callSubtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        SubTask packSubtask = new SubTask(removalEpic.getTaskId(), nextTaskId, "Упаковать коробки");
        packSubtask.setDescription("5-6 коробок должно хватить.");
        removalEpic.create(packSubtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        SubTask tapeSubtask = new SubTask(removalEpic.getTaskId(), nextTaskId, "Купить скотч");
        tapeSubtask.setDescription("20 метров.");
        removalEpic.create(tapeSubtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        Epic epicParty = new Epic(nextTaskId, "Вечеринка");
        epicParty.setDescription("Поздравление с праздником в коллективе");
        manager.create(epicParty);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        SubTask barSubtask = new SubTask(epicParty.getTaskId(), nextTaskId, "Купить цветы и шампанское");
        barSubtask.setDescription("К 2-3 бутылкам шампанского нужно взять 2 коробки шоколадных конфет и фрукты.");
        epicParty.create(barSubtask);

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks());

        System.out.println("\n2.\tИзмените статусы созданных объектов, распечатайте. Проверьте, что статус задачи\n"
                + "и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.");

        Task updateShoppingTask = (Task) manager.getTask(0).clone();
        updateShoppingTask.setStatus(TaskStatusEnum.DONE);
        manager.update(updateShoppingTask);

        SubTask updateCallSubtask = (SubTask) manager.getSubtask(3).clone();
        updateCallSubtask.setStatus(TaskStatusEnum.IN_PROGRESS);
        removalEpic.update(updateCallSubtask);

        SubTask updatePackSubtask = (SubTask) manager.getSubtask(4).clone();
        updatePackSubtask.setDescription("В одну коробку войдет.");
        updatePackSubtask.setStatus(TaskStatusEnum.DONE);
        removalEpic.update(updatePackSubtask);

        SubTask updateBarSubtask = (SubTask) manager.getSubtask(5).clone();
        updateBarSubtask.setStatus(TaskStatusEnum.DONE);
        removalEpic.update(updateBarSubtask);

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks());

        System.out.println("\n3.\tПопробуйте удалить одну из задач и один из эпиков.");
        manager.removeTask(0);
        manager.removeTask(epicParty.getTaskId());

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks());

        System.out.println("\n4.\t Печать по отдельным категориям. Только задачи:");
        ReportUtils.printTasksCollection(manager.getTasks().stream()
                .map(t -> (TaskBase) t)
                .collect(Collectors.toList()));

        System.out.println("\n5.\t Печать по отдельным категориям. Только эпики:");
        ReportUtils.printTasksCollection(manager.getEpics().stream()
                .map(t -> (TaskBase) t)
                .collect(Collectors.toList()));

        System.out.println("\n6.\t Печать по отдельным категориям. Только подзадачи:");
        ReportUtils.printTasksCollection(manager.getSubtasks().stream()
                .map(t -> (TaskBase) t)
                .collect(Collectors.toList()));

        System.out.println("Тест по ТЗ №3 выполнен.");
    }


    public static void testSprint4(TaskManager manager) {
        manager.removeAllTasks();
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
        SubTask subtask = new SubTask(repairEpic.getTaskId(), nextTaskId,
                "Подзадача 1", "Создаю подзадачу с индексом 2. Метод: epic.create(subtask)");
        repairEpic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new SubTask(repairEpic.getTaskId(), nextTaskId,
                "Подзадача 2",
                "Создаю подзадачу с индексом 3. Метод: epic.create(subtask)",
                TaskStatusEnum.DONE);
        repairEpic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new SubTask(repairEpic.getTaskId(), nextTaskId, "Подзадача 3",
                "Создаю подзадачу с индексом 4. Метод: manager.create(subtask)");
        manager.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new SubTask(repairEpic.getTaskId(), nextTaskId,
                "Подзадача 4", "Создаю подзадачу с индексом 5. Метод: manager.create(subtask)");
        manager.create(subtask);

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks());

        System.out.println("\n2.\tВызываем разные методы интерфейса TaskManager\n" +
                "и напечатаем историю просмотров после каждого вызова");

        System.out.println("\nВызываем задание № 0 и меняем его статус.");
        Task updateShoppingTask = (Task) manager.getTask(0).clone();
        updateShoppingTask.setStatus(TaskStatusEnum.DONE);
        manager.update(updateShoppingTask);

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks());

        System.out.println("\nПечатаем историю просмотра. (ТЗ №5: Ограничения на 10 позиций больше нет!)");
        ReportUtils.printTasksCollection(manager.getHistory());

        System.out.println("\nВызываем задания 12 раз в цикле по одному.");
        List<Integer> taskIsCollection = manager.getAllTaskId();
        int index = 0;
        for (int i = 0; i < 12; i++) {
            if (index >= taskIsCollection.size()) {
                index = 0;
            }
            TaskBase task = manager.getTaskBase(taskIsCollection.get(index));
            System.out.print((i + 1) + ") ");
            ReportUtils.printTask(task);
            index++;
        }

        System.out.println("\nПечатаем историю просмотра. (ТЗ №5: Ограничения на 10 позиций больше нет!)");
        ReportUtils.printTasksCollection(manager.getHistory());

        System.out.println("Тест по ТЗ №4 выполнен.");
    }


    public static void testSprint5(TaskManager manager) {
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
        SubTask subtask = new SubTask(epic.getTaskId(), nextTaskId,
                "Подзадача 1", "Создаю подзадачу с индексом 3.");
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new SubTask(epic.getTaskId(), nextTaskId,
                "Подзадача 2", "Создаю подзадачу с индексом 4.");
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new SubTask(epic.getTaskId(), nextTaskId,
                "Подзадача 3", "Создаю подзадачу с индексом 5.");
        epic.create(subtask);

        nextTaskId = CollectionUtils.getNextTaskId(manager.getAllTaskId());
        epic = new Epic(nextTaskId,
                "Эпик-задача 2", "Создаю эпик-задачу с индексом 6 без подзадач.");
        manager.create(epic);

        System.out.println("Результат:");
        ReportUtils.printTasksCollection(manager.getHighLevelTasks());

        System.out.println("\n2.\tЗапросите созданные задачи несколько раз в разном порядке;");
        System.out.println("\nВызываю задачи 20 раз в случайном порядке.");
        for (int i = 0; i < 20; i++) {
            int randomId = (int) (Math.random() * 7);
            TaskBase randomTask = manager.getTaskBase(randomId);
            System.out.print((i + 1) + ") ");
            ReportUtils.printTask(randomTask);
        }

        System.out.println("\n3.\tПосле каждого запроса выведите историю и убедитесь, что в ней нет повторов;");

        System.out.println("\nПечатаем историю просмотра.");
        ReportUtils.printTasksCollection(manager.getHistory());

        System.out.println("\n4.\tУдалите задачу, которая есть в истории, и проверьте," +
                " что при печати она не будет выводиться;");

        manager.removeTask(0);
        System.out.println("\nУдалили задачу с идентификатором 0.");

        System.out.println("\nПечатаем историю просмотра.");
        ReportUtils.printTasksCollection(manager.getHistory());

        System.out.println("\n5.\tУдалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик," +
                " так и все его подзадачи.");

        manager.removeTask(2);
        System.out.println("\nУдалили эпик с идентификатором 2.");

        System.out.println("\nПечатаем историю просмотра.");
        ReportUtils.printTasksCollection(manager.getHistory());

        System.out.println("Тест по ТЗ №5 выполнен.");
    }
}