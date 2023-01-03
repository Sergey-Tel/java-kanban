package controller;
import model.EpicCards;
import java.util.ArrayList;
import java.util.HashMap;

public class EpManagerTask {
    private HashMap<Integer, EpicCards> epics = new HashMap<>();
    protected Integer counterIDEpics = 0;


    public ArrayList<EpicCards> findAllEpics() {
        return new ArrayList<>(epics.values());
    }


    public void deleteAllEpics() {
        epics.clear();
    }


    public EpicCards findByID(Integer id) {
        return epics.get(id);
    }

    public HashMap<Integer, EpicCards> getEpics() {
        return epics;
    }

    public EpicCards createOneEpic(EpicCards task) {
        final EpicCards newTask = new EpicCards(task.getName(), task.getDescription(), ++counterIDEpics);
        if (!epics.containsKey(newTask.getId())) {
            epics.put(newTask.getId(), newTask);
        } else {
            System.out.println("Задача с таким ID уже существует");
            return null;
        }
        return newTask;
    }


    public EpicCards updateOneEpic(EpicCards epicCards) {
        final EpicCards originalTask = epics.get(epicCards.getId());
        if (originalTask == null) {
            System.out.println("Задачи с таким ID не существует.");
            return null;
        }
        originalTask.setDescription(epicCards.getDescription());
        originalTask.setName(epicCards.getName());
        return originalTask;
    }


    public void deleteByID(Integer id) {
        epics.remove(id);
    }
}

