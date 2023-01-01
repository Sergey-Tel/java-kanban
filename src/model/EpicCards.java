package model;
import java.util.ArrayList;
import java.util.Objects;
public class EpicCards extends Task {
    protected ArrayList<SubTask> subTasksEp = new ArrayList<>();

    public EpicCards(String name, String description, Integer id) {
        super(name, description, id);
    }

    public ArrayList<SubTask> getSubTasksEp() {

        return subTasksEp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EpicCards epicCards = (EpicCards) o;
        return Objects.equals(subTasksEp, epicCards.subTasksEp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksEp);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasksEp +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }
}
