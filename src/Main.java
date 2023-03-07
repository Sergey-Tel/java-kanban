import dev.service.Managers;
import dev.service.TasksManager;
import dev.utils.menu.MainMenu;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        System.out.println("Трекер задач");
        try {
            Path path = FileSystems.getDefault().getPath("java-kanban.csv");
            path.toFile().createNewFile();
            Managers.setFileTasksManager(path.toFile());
            TasksManager manager = Managers.getDefault();
            MainMenu.menu(manager);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}