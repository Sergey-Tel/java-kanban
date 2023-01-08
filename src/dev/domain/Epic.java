package dev.domain;

import dev.service.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends dev.domain.Task {
    private final List<Integer> subtasks;

    public Epic(int taskId, String name, String description) {
        super(taskId, name, description);
        this.subtasks = new ArrayList<>();
        this.status = TaskStatusEnum.NEW;
    }

    public Epic(int taskId, String name) {
        this(taskId, name, "");
    }


    public void create(SubTask subtask) {
        if (!subtasks.contains(subtask.getTaskId())) {
            Managers.getDefault().create(subtask);
            updateStatus();
        } else {
            throw new IndexOutOfBoundsException(String.format("Подзадача с идентификационным номером %d уже присутствует в коллекции.", subtask.getTaskId()));
        }
    }


    public void update(SubTask subtask) {
        if (subtasks.contains(subtask.getTaskId())) {
            Managers.getDefault().update(subtask);
            updateStatus();
        } else {
            throw new IndexOutOfBoundsException(String.format("Подзадача с идентификационным номером %d отсутствует в коллекции.", subtask.getTaskId()));
        }
    }

    public SubTask create(int taskId, String name, String description) {
        SubTask addingSubTask = new SubTask(this.getTaskId(), taskId, name, description);
        Managers.getDefault().create(addingSubTask);
        updateStatus();
        return addingSubTask;
    }

    public SubTask create(int taskId, String name) {
        return create(taskId, name, "");
    }

    public SubTask getSubtask(Integer taskId) {
        if (subtasks.contains(taskId)) {
            return Managers.getDefault().getSubtask(taskId);
        } else {
            throw new IndexOutOfBoundsException(String.format("Идентификационный номер задачи %d отсутствует в коллекции.",taskId));
        }
    }

    public void updateStatus() {
        subtasks.clear();
        subtasks.addAll(Managers.getDefault().getSubtasks().stream()
                .filter(subtask -> subtask.getEpicId().equals(this.getTaskId()))
                .map(AbstractTask::getTaskId)
                .toList());
        if (subtasks.size() == 0) {
            status = TaskStatusEnum.NEW;
        } else {
            status = Managers.getDefault().getSubtask(subtasks.get(0)).status;
            for (int i = 1; i < subtasks.size(); i++) {
                status = TaskStatusEnum.compareEnum(status, Managers.getDefault().getSubtask(subtasks.get(i)).status);
            }
        }
    }

    public int size() {
        return subtasks.size();
    }

    public boolean containsSubtaskId(Integer taskId) {
        return subtasks.contains(taskId);
    }

    public List<Integer> subtaskIdList() {
        return subtasks;
    }


    public List<SubTask> getAllSubtasks() {
        return Managers.getDefault().getSubtasks().stream()
                .filter(subtask -> subtask.getEpicId().equals(this.getTaskId()))
                .collect(Collectors.toList());
    }

    public void removeSubtask(Integer taskId) {
        if (subtasks.contains(taskId)) {
            if (Managers.getDefault().containsSubtaskId(taskId)) {
                Managers.getDefault().removeTask(taskId);
            }
            updateStatus();
        } else {
            throw new IndexOutOfBoundsException(String.format("Идентификационный номер задачи %d отсутствует в коллекции.", taskId));
        }
    }

    public void removeAllTasks() {
        for (Integer id : subtasks) {
            Managers.getDefault().removeTask(id);
        }
        updateStatus();
    }

    @Override
    public Object clone() {
        super.clone();
        Epic cloneableEpic = new Epic(this.getTaskId(), this.getName(), this.getDescription());
        cloneableEpic.updateStatus();
        return cloneableEpic;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", taskId=" + this.getTaskId() + '\'' +
                ", status=" + status.title + '\'' +
                ", size=" + size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;

        Epic epic = (Epic) o;

        if (getTaskId() != epic.getTaskId()) return false;
        if (!getName().equals(epic.getName())) return false;
        if (!getDescription().equals(epic.getDescription())) return false;
        if (!status.equals(epic.status)) return false;
        return subtasks.equals(epic.subtasks);
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getTaskId();
        result = 31 * result + status.hashCode();
        result = 31 * result + subtasks.hashCode();
        return result;
    }
}
