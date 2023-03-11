package dev.domain;

import dev.service.server.Managers;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class Epic extends TaskAbstract {
    private final List<Integer> subtasks;

    public Epic(int taskId, String name, String description) {
        super(taskId, name, description);
        this.subtasks = new LinkedList<>();
        this.status = TaskStatusEnum.NEW;
        this.type = TaskTypeEnum.EPIC;
    }

    public Epic(int taskId, String name) {
        this(taskId, name, "");
        this.type = TaskTypeEnum.EPIC;
    }


    public void create(Subtask subtask) throws IOException {
        if (!subtasks.contains(subtask.getTaskId())) {
            Managers.getDefault().create(subtask);
        } else {
            throw new IndexOutOfBoundsException("Подзадача с идентификационным номером " +
                    subtask.getTaskId() + " уже присутствует в коллекции.");
        }
    }


    public void update(Subtask subtask) throws IOException {
        if (subtasks.contains(subtask.getTaskId())) {
            Managers.getDefault().update(subtask);
        } else {
            throw new IndexOutOfBoundsException("Подзадача с идентификационным номером " +
                    subtask.getTaskId() + " отсутствует в коллекции.");
        }
    }

    public Subtask create(int taskId, String name, String description) throws IOException {
        Subtask addingSubtask = new Subtask(this.getTaskId(), taskId, name, description);
        Managers.getDefault().create(addingSubtask);
        return addingSubtask;
    }

    public Subtask create(int taskId, String name) throws IOException {
        return create(taskId, name, "");
    }

    public Subtask getSubtask(Integer taskId) throws IOException {
        if (subtasks.contains(taskId)) {
            return Managers.getDefault().getSubtask(taskId);
        } else {
            throw new IndexOutOfBoundsException("Идентификационный номер задачи " +
                    taskId + " отсутствует в коллекции.");
        }
    }

    public void updateStatus() throws IOException {
        subtasks.clear();
        subtasks.addAll(Managers.getDefault().getSubtasks().stream()
                .filter(subtask -> subtask.getEpicId().equals(this.getTaskId()))
                .map(TaskBase::getTaskId)
                .collect(Collectors.toList()));
        if (subtasks.size() == 0) {
            status = TaskStatusEnum.NEW;
            this.startTime = Optional.empty();
            this.duration = Duration.ZERO;
            this.endTime = Optional.empty();
        } else {
            Subtask itemSubtask = Managers.getDefault().getSubtask(subtasks.get(0));
            status = itemSubtask.status;
            Subtask firstSubtask = itemSubtask;
            Subtask lastSubtask = itemSubtask;
            Duration sumDuration = itemSubtask.duration;
            for (int i = 1; i < subtasks.size(); i++) {
                itemSubtask = Managers.getDefault().getSubtask(subtasks.get(i));
                status = TaskStatusEnum.compareEnum(status, itemSubtask.status);
                sumDuration = sumDuration.plus(itemSubtask.duration);
                if (firstSubtask.compareTo(itemSubtask) < 0) {
                    firstSubtask = itemSubtask;
                }
                if (lastSubtask.compareToEndTime(itemSubtask) < 0) {
                    lastSubtask = itemSubtask;
                }
            }
            this.startTime = firstSubtask.getStartTime();
            this.duration = sumDuration;
            this.endTime = lastSubtask.getEndTime();
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


    public List<Subtask> getAllSubtasks() throws IOException {
        return Managers.getDefault().getSubtasks().stream()
                .filter(subtask -> subtask.getEpicId().equals(this.getTaskId()))
                .collect(Collectors.toList());
    }

    public void removeSubtask(Integer taskId) throws IOException {
        if (subtasks.contains(taskId)) {
            if (Managers.getDefault().containsSubtaskId(taskId)) {
                Managers.getDefault().removeTask(taskId);
            }
        } else {
            throw new IndexOutOfBoundsException("Идентификационный номер задачи " +
                    taskId + " отсутствует в коллекции.");
        }
    }

    public void removeAllTasks() throws IOException {
        for (Integer id : new LinkedList<>(subtasks)) {
            Managers.getDefault().removeTask(id);
        }
    }

    @Override
    public Object clone() {
        Epic cloneableEpic = new Epic(this.getTaskId(), this.getName(), this.getDescription());
        try {
            cloneableEpic.updateStatus();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cloneableEpic;
    }

    @Override
    public Object clone(String name, String description) throws IOException {
        Epic cloneableEpic = new Epic(this.getTaskId(), name, description);
        cloneableEpic.updateStatus();
        return cloneableEpic;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", taskId=" + this.getTaskId() + '\'' +
                ", status=" + this.getStatus().title +
                ", startTime=" + (this.getStartTime().isPresent() ?
                LocalDateTime.ofInstant(this.getStartTime().get(), ZoneId.systemDefault()) : "null") +
                ", duration=" + this.getDuration() +
                ", endTime=" + (this.getEndTime().isPresent() ?
                LocalDateTime.ofInstant(this.getEndTime().get(), ZoneId.systemDefault()) : "null") +
                ", size=" + size() +
                '}';
    }

    @Override
    public String toString(String separator) {
        return toString(TaskTypeEnum.EPIC, separator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;

        Epic epic = (Epic) o;

        if (getTaskId() != epic.getTaskId()) return false;
        if (!getName().equals(epic.getName())) return false;
        if (!getDescription().equals(epic.getDescription())) return false;
        if (!getStatus().equals(epic.getStatus())) return false;
        if (!getStartTime().equals(epic.getStartTime())) return false;
        if (!this.duration.equals(epic.duration)) return false;
        return subtasks.equals(epic.subtasks);
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getTaskId();
        result = 31 * result + status.hashCode();
        result = 31 * result + startTime.hashCode();
        result = 31 * result + duration.hashCode();
        result = 31 * result + subtasks.hashCode();
        return result;
    }
}
