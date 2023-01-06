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

        int nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        dev.domain.Task shoppingTask = new dev.domain.Task(nextTaskId, "Купить батарейки!", "Необходимо 4 шт.");
        manager.create(shoppingTask);

        nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        dev.domain.Task lessonTask = new dev.domain.Task(nextTaskId, "Проверить уроки!");
        lessonTask.setDescription("В дневнике задание.");
        manager.create(lessonTask);

        nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        dev.domain.Epic removalEpic = new dev.domain.Epic(nextTaskId,
                "Переезд", "Снять наличные деньги");
        manager.create(removalEpic);

        nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        dev.domain.SubTask callSubTask = new dev.domain.SubTask(removalEpic.getTaskId(), nextTaskId,
                "Заказать машину", "Газели будет достаточно.");
        removalEpic.create(callSubTask);

        nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        dev.domain.SubTask packSubTask = new dev.domain.SubTask(removalEpic.getTaskId(), nextTaskId, "Упаковать коробки");
        packSubTask.setDescription("5-6 коробок должно хватить.");
        removalEpic.create(packSubTask);

        nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        dev.domain.SubTask tapeSubTask = new dev.domain.SubTask(removalEpic.getTaskId(), nextTaskId, "Купить скотч");
        tapeSubTask.setDescription("20 метров.");
        removalEpic.create(tapeSubTask);

        nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        dev.domain.Epic epicParty = new dev.domain.Epic(nextTaskId, "Вечеринка");
        epicParty.setDescription("Поздравление с праздником в коллективе");
        manager.create(epicParty);

        nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        dev.domain.SubTask barSubTask = new dev.domain.SubTask(epicParty.getTaskId(), nextTaskId, "Купить цветы и шампанское");
        barSubTask.setDescription("К 2-3 бутылкам шампанского нужно взять 2 коробки шоколадных конфет и фрукты.");
        epicParty.create(barSubTask);

        System.out.println("Результат:");
        dev.utils.ReportUtils.printTasksCollection(manager.getHighLevelTasks());

        System.out.println("\n2.\tИзмените статусы созданных объектов, распечатайте. Проверьте, что статус задачи\n"
                + "и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.");

        dev.domain.Task updateShoppingTask = (dev.domain.Task) manager.getTask(0).clone();
        updateShoppingTask.setStatus(dev.domain.TaskStatusEnum.DONE);
        manager.update(updateShoppingTask);

        dev.domain.SubTask updateCallSubTask = (dev.domain.SubTask) manager.getSubtask(3).clone();
        updateCallSubTask.setStatus(dev.domain.TaskStatusEnum.IN_PROGRESS);
        removalEpic.update(updateCallSubTask);

        dev.domain.SubTask updatePackSubTask = (dev.domain.SubTask) manager.getSubtask(4).clone();
        updatePackSubTask.setDescription("В одну коробку войдет.");
        updatePackSubTask.setStatus(dev.domain.TaskStatusEnum.DONE);
        removalEpic.update(updatePackSubTask);

        dev.domain.SubTask updateBarSubTask = (dev.domain.SubTask) manager.getSubtask(5).clone();
        updateBarSubTask.setStatus(dev.domain.TaskStatusEnum.DONE);
        removalEpic.update(updateBarSubTask);

        System.out.println("Результат:");
        dev.utils.ReportUtils.printTasksCollection(manager.getHighLevelTasks());

        System.out.println("\n3.\tПопробуйте удалить одну из задач и один из эпиков.");
        manager.removeTask(0);
        manager.removeTask(epicParty.getTaskId());

        System.out.println("Результат:");
        dev.utils.ReportUtils.printTasksCollection(manager.getHighLevelTasks());

        System.out.println("\n4.\t Печать по отдельным категориям. Только задачи:");
        dev.utils.ReportUtils.printTasksCollection(manager.getTasks().stream()
                .map(t -> (dev.domain.TaskBase) t)
                .collect(Collectors.toList()));

        System.out.println("\n5.\t Печать по отдельным категориям. Только эпики:");
        dev.utils.ReportUtils.printTasksCollection(manager.getEpics().stream()
                .map(t -> (dev.domain.TaskBase) t)
                .collect(Collectors.toList()));

        System.out.println("\n6.\t Печать по отдельным категориям. Только подзадачи:");
        dev.utils.ReportUtils.printTasksCollection(manager.getSubtasks().stream()
                .map(t -> (dev.domain.TaskBase) t)
                .collect(Collectors.toList()));
    }


    public static void testSprint4(TaskManager manager) {
        manager.removeAllTasks();
        System.out.println("Тестирование приложения по условиям, заданным в техническом задании Спринта №4:");

        System.out.println("\n1.\tСоздайте несколько задач разного типа.");

        int nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        dev.domain.Task shoppingTask = new dev.domain.Task(nextTaskId, "Задача 1", "Создаю обычную задачу с индексом 0.");
        manager.create(shoppingTask);

        nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        dev.domain.Epic repairEpic = new dev.domain.Epic(nextTaskId,
                "Эпик-задача 1", "Создаю пустую эпик-задачу с индексом 1.");
        manager.create(repairEpic);

        nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        dev.domain.SubTask subtask = new dev.domain.SubTask(repairEpic.getTaskId(), nextTaskId,
                "Подзадача 1", "Создаю подзадачу с индексом 2. Метод: epic.create(subtask)");
        repairEpic.create(subtask);

        nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new dev.domain.SubTask(repairEpic.getTaskId(), nextTaskId,
                "Подзадача 2",
                "Создаю подзадачу с индексом 3. Метод: epic.create(subtask)",
                dev.domain.TaskStatusEnum.DONE);
        repairEpic.create(subtask);

        nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new dev.domain.SubTask(repairEpic.getTaskId(), nextTaskId, "Подзадача 3",
                "Создаю подзадачу с индексом 4. Метод: manager.create(subtask)");
        manager.create(subtask);

        nextTaskId = dev.utils.CollectionUtils.getNextTaskId(manager.getAllTaskId());
        subtask = new dev.domain.SubTask(repairEpic.getTaskId(), nextTaskId,
                "Подзадача 4", "Создаю подзадачу с индексом 5. Метод: manager.create(subtask)");
        manager.create(subtask);

        System.out.println("Результат:");
        dev.utils.ReportUtils.printTasksCollection(manager.getHighLevelTasks());

        System.out.println("\n2.\tВызываем разные методы интерфейса TaskManager\n" +
                "и напечатаем историю просмотров после каждого вызова");

        System.out.println("\nВызываем задание № 0 и меняем его статус.");
        dev.domain.Task updateShoppingTask = (dev.domain.Task) manager.getTask(0).clone();
        updateShoppingTask.setStatus(dev.domain.TaskStatusEnum.DONE);
        manager.update(updateShoppingTask);

        System.out.println("Результат:");
        dev.utils.ReportUtils.printTasksCollection(manager.getHighLevelTasks());

        System.out.println("\nПечатаем историю просмотра.");
        dev.utils.ReportUtils.printTasksCollection(manager.getHistory());

        System.out.println("\nВызываем задания 12 раз в цикле по одному.");
        List<Integer> taskIsCollection = manager.getAllTaskId();
        int index = 0;
        for (int i = 0; i < 12; i++) {
            if (index >= taskIsCollection.size()) {
                index = 0;
            }
            dev.domain.TaskBase task = manager.getTaskBase(taskIsCollection.get(index));
            System.out.print((i + 1) + ") ");
            dev.utils.ReportUtils.printTask(task);
            index++;
        }

        System.out.println("\nПечатаем историю просмотра.");
        dev.utils.ReportUtils.printTasksCollection(manager.getHistory());
    }
}
