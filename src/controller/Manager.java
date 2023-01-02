package controller;
import model.EpicCards;
import model.SubTask;
import model.Task;

import java.util.ArrayList;

public class Manager {

    private final TasksManager tasksManager = new TasksManager();
    private final EpManagerTask epManagerTask = new EpManagerTask();
    private final SubTasksManager subTasksManager = new SubTasksManager(epManagerTask);



    public ArrayList<Task> findAllTasks() {
        return tasksManager.findAllTasks();
    }

    public ArrayList<EpicCards> findAllEpics() {
        return epManagerTask.findAllEpics();
    }

    public ArrayList<SubTask> findAllSubTasksOfEpic(EpicCards epicCards) {
        return subTasksManager.findAllOfEpic(epicCards);
    }


    public void deleteAllTask() {
        tasksManager.deleteAllTasks();
    }

    public void deleteAllSubTasks() {
        subTasksManager.deleteAllSubtasks();
    }

    public void deleteAllEpics() {
        epManagerTask.deleteAllEpics();
    }


    public SubTask findSubTaskById(Integer id) {
        return subTasksManager.findByIdSubTask(id);
    }

    public Task findTaskById(Integer id) {
        return tasksManager.findByIdTask(id);
    }

    public EpicCards findEpicById(Integer id) {
        return epManagerTask.findByID(id);
    }

    public Task createTask(Task task) {
        return tasksManager.createTask(task);
    }

    public SubTask createSubTask(SubTask subTask, EpicCards epicCards) {
        return subTasksManager.createOneSubTask(subTask, epicCards);
    }

    public EpicCards createEpic(EpicCards epicCards) {
        return epManagerTask.createOneEpic(epicCards);
    }

    public Task updateTaskByID(Task task) {
        return tasksManager.updateTask(task);
    }

    public SubTask updateSubTaskByID(SubTask subTask) {
        return subTasksManager.updateSubTask(subTask);
    }

    public Task updateEpicByID(EpicCards epicCards) {
        return epManagerTask.updateOneEpic(epicCards);
    }

    public void deleteSubTaskById(Integer id) {
        subTasksManager.deleteByIDSubTask(id);
    }

    public void deleteEpicById(Integer id) {
        epManagerTask.deleteByID(id);
    }

    public Task deleteTaskById(Integer id) {
        return tasksManager.deleteByIdTask(id);
    }
}