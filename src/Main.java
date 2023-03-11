import dev.service.server.HttpTaskServer;
import dev.utils.KVServer;

import java.io.IOException;

public class Main {


    /* 1. Для выбора менеджера работы с памятью
        следует выполнить команду: Managers.SetMemoryTasksManager();
        2. Для выбора менеджера работы с файлом
        следует выполнить команды:
        Path path = FileSystems.getDefault().getPath("java-kanban.csv");
        path.toFile().createNewFile();
        Managers.setFileTasksManager(path.toFile());
        */
    /* try {
            Path path = FileSystems.getDefault().getPath("java-kanban.csv");
            path.toFile().createNewFile();
            Managers.setFileTasksManager(path.toFile());
            TasksManager manager = Managers.getDefault();
            MainMenu.menu(manager);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

     */
    public static void main(String[] args) {
        System.out.println("Трекер задач");
        try {
            new KVServer().start();
            new HttpTaskServer().start();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}