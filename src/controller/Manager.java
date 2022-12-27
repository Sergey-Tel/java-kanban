package controller;
import model.EpicCards;
import model.SubTask;
import model.Task;

import java.util.ArrayList;

public class Manager {

    TasksManager tasksManager = new TasksManager();
    EpManagerTask epManagerTask = new EpManagerTask();
    SubTasksManager subTasksManager = new SubTasksManager(epManagerTask);



    public ArrayList<Task> findAllTasks() {
        return tasksManager.findAll();
    }

    public ArrayList<EpicCards> findAllEpics() {
        return epManagerTask.findAllEpics();
    }

    public ArrayList<SubTask> findAllSubTasksOfEpic(EpicCards epicCards) {
        return subTasksManager.findAllOfEpic(epicCards);
    }


    public void deleteAllTask() {
        tasksManager.deleteAll();
    }

    public void deleteAllSubTasks() {
        subTasksManager.deleteAll();
    }

    public void deleteAllEpics() {
        epManagerTask.deleteAllEpics();
    }


    public SubTask findSubTaskById(Integer id) {
        return subTasksManager.findById(id);
    }

    public Task findTaskById(Integer id) {
        return tasksManager.findById(id);
    }

    public EpicCards findEpicById(Integer id) {
        return epManagerTask.findByID(id);
    }

    public Task createTask(Task task) {
        return tasksManager.create(task);
    }

    public SubTask createSubTask(SubTask subTask, EpicCards epicCards) {
        return subTasksManager.create(subTask, epicCards);
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
        subTasksManager.deleteByID(id);
    }

    public void deleteEpicById(Integer id) {
        epManagerTask.deleteByID(id);
    }

    public Task deleteTaskById(Integer id) {
        return tasksManager.deleteById(id);
    }
}