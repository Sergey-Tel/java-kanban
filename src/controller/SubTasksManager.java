package controller;
import model.EpicCards;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import static model.StatusTracker.*;

public class SubTasksManager {
    protected Integer counterIDSubTasks = 0;
    protected HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected EpManagerTask epManagerTask;

    public SubTasksManager(EpManagerTask epManagerTask) {
        this.epManagerTask = epManagerTask;
    }


    public ArrayList<SubTask> findAllOfEpic(EpicCards epicCards) {
        return epManagerTask.epics.get(epicCards.getId()).getSubTasksEp();
    }



    public void deleteAllSubtasks() {
        subTasks.clear();
    }



    public SubTask findByIdSubTask(Integer id) {
        return subTasks.get(id);
    }

    public SubTask updateSubTask(SubTask task) {
        final SubTask originalTask = subTasks.get(task.getId());
        if (originalTask == null) {
            System.out.println("Задачи с таким ID не существует.");
            return null;
        }
        originalTask.setDescription(task.getDescription());
        originalTask.setName(task.getName());
        originalTask.setStatus(task.getStatus());
        epManagerTask.epics.get(task.getEpicID()).getSubTasksEp().remove(originalTask);
        epManagerTask.epics.get(task.getEpicID()).getSubTasksEp().add(task);
        refreshStatusSubTask(task);
        return originalTask;
    }


    public SubTask createOneSubTask(SubTask subTask, EpicCards epicCards) {
        final SubTask newSubTask = new SubTask(subTask.getName(), subTask.getDescription(), ++counterIDSubTasks, epicCards.getId());
        if (!subTasks.containsKey(newSubTask.getId())) {
            subTasks.put(newSubTask.getId(), newSubTask);
            epManagerTask.epics.get(epicCards.getId()).getSubTasksEp().add(/*newSubTask.getId(),*/ newSubTask);
        } else {
            System.out.println("Задача с таким ID уже существует");
            return null;
        }
        return newSubTask;
    }


    public void refreshStatusSubTask(SubTask task) {
        ArrayList<SubTask> subTasksOfEpic = epManagerTask.epics.get(task.getEpicID()).getSubTasksEp();
        int counterNew = 0;
        int counterDone = 0;
        for (SubTask subTask : subTasksOfEpic) {
            if (subTask.getStatus().equals(NEW)) {
                counterNew++;
            } else if (subTask.getStatus().equals(DONE)) {
                counterDone++;
            }
        }
        if (counterNew == subTasksOfEpic.size()) {
            epManagerTask.epics.get(task.getEpicID()).setStatus(NEW);
        } else if (counterDone == subTasksOfEpic.size()) {
            epManagerTask.epics.get(task.getEpicID()).setStatus(DONE);
        } else {
            epManagerTask.epics.get(task.getEpicID()).setStatus(IN_PROGRESS);
        }
    }


    public SubTask deleteByIDSubTask(Integer id) {
        final SubTask deletedTask = subTasks.get(id);
        epManagerTask.epics.get(deletedTask.getEpicID()).getSubTasksEp().remove(deletedTask);
        subTasks.remove(id);
        return deletedTask;
    }
}