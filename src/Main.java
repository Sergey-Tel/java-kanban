import dev.service.Managers;
import dev.utils.menu.MainMenu;

public class Main {

    public static void main(String[] args) {
        System.out.println("Трекер задач");
        MainMenu.Menu(Managers.getDefault());
    }
}