package dev.service;

import dev.domain.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.FileSystems.getDefault;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileBackedTasksManagerTest extends TaskManagerTestAbstract<InMemoryTasksManager> {

    static final Path defualtPath = getDefault().getPath("java-kanban-test.csv");

    static final Path testFilePath = getDefault().getPath("java-kanban-error-test.csv");

    static final String[] testFileLines = {
            "id|type|name|status|start|duration|description|epic\n",
            "0|TASK|Задача 1|1|1666936000000|10|Создаю обычную задачу с индексом 0.|0\n",
            "1|TASK|Задача 2|1|1676936000000|10|Создаю обычную задачу с индексом 1.|0\n",
            "2|EPIC|Эпик-задача 1|2|1686936000000|10|Создаю эпик-задачу с индексом 2, в которой будет создано три подзадачи.|0\n",
            "3|SUBTASK|Подзадача 1|2|1696936000000|10|Создаю подзадачу с индексом 3.|2\n",
            "4|SUBTASK|Подзадача 2|1|1706936000000|10|Создаю подзадачу с индексом 4. Если нет конфликта, то это ошибка!|2\n",
            "5|SUBTASK|Подзадача 3|3|1716936000000|10|Создаю подзадачу с индексом 5.|2\n",
            "\n",
            "5|4|3|2"};


    static final String[] testErrorTaskFileLines = {
            "id|type|name|status|start|duration|description|epic\n",
            "0|TASK|Задача 1|1|1656936000000|10|Создаю обычную задачу с индексом 0.|0\n",
            "1|TASK|Задача 2|1|1656936000000|10|Создаю обычную задачу с индексом 1.|0\n",
            "2|EPIC|Эпик-задача 1|2|1656570600000|135|Создаю эпик-задачу с индексом 2, в которой будет создано три подзадачи.|0\n",
            "3|SUBTASK|Подзадача 1|2|1656570600000|45|Создаю подзадачу с индексом 3.|2\n",
            "4|SUBTASK|Подзадача 2|1|1656570600000|45|Создаю подзадачу с индексом 4. Если нет конфликта, то это ошибка!|2\n",
            "5|SUBTASK|Подзадача 3|3|1656570600000|45|Создаю подзадачу с индексом 5.|2\n",
            "\n",
            "5|4|3|2"};

    static final String[] testErrorSubtaskFileLines = {
            "id|type|name|status|start|duration|description|epic\n",
            "0|TASK|Задача 1|1|1656936000000|10|Создаю обычную задачу с индексом 0.|0\n",
            "1|TASK|Задача 2|1|0|0|Создаю обычную задачу с индексом 1.|0\n",
            "2|EPIC|Эпик-задача 1|2|1656570600000|135|Создаю эпик-задачу с индексом 2, в которой будет создано три подзадачи.|0\n",
            "3|SUBTASK|Подзадача 1|2|1656570600000|45|Создаю подзадачу с индексом 3.|2\n",
            "4|SUBTASK|Подзадача 2|1|1656570600000|45|Создаю подзадачу с индексом 4. Если нет конфликта, то это ошибка!|2\n",
            "5|SUBTASK|Подзадача 3|3|1656570600000|45|Создаю подзадачу с индексом 5.|2\n",
            "\n",
            "5|4|3|2"};

    static final String[] linesTestA = {
            "id|type|name|status|start|duration|description|epic\n"};

    static final String[] linesTestB = {
            "id|type|name|status|start|duration|description|epic\n",
            "0|TASK|Задача 1|1|1666936000000|10|Создаю обычную задачу с индексом 0.|0\n",
            "1|TASK|Задача 2|1|1676936000000|10|Создаю обычную задачу с индексом 1.|0\n",
            "2|EPIC|Эпик-задача 1|2|1686936000000|10|Создаю эпик-задачу с индексом 2, в которой будет создано три подзадачи.|0\n",
            "\n",
            "2|1"};

    static final String[] linesTestC = {
            "id|type|name|status|start|duration|description|epic\n",
            "0|TASK|Задача 1|1|1666936000000|10|Создаю обычную задачу с индексом 0.|0\n",
            "1|TASK|Задача 2|1|1676936000000|10|Создаю обычную задачу с индексом 1.|0\n",
            "2|EPIC|Эпик-задача 1|2|1686936000000|10|Создаю эпик-задачу с индексом 2, в которой будет создано три подзадачи.|0\n",
            "3|SUBTASK|Подзадача 1|2|1696936000000|10|Создаю подзадачу с индексом 3.|2\n",
            "4|SUBTASK|Подзадача 2|1|1706936000000|10|Создаю подзадачу с индексом 4. Если нет конфликта, то это ошибка!|2\n",
            "5|SUBTASK|Подзадача 3|3|1716936000000|10|Создаю подзадачу с индексом 5.|2\n",
    };

    static void createTestFile(String[] fileLines) {
        testFilePath.toFile().setExecutable(true);
        testFilePath.toFile().setReadable(true);
        testFilePath.toFile().setWritable(true);

        try (FileWriter writer = new FileWriter(testFilePath.toFile())) {
            for (String line : fileLines) {
                writer.write(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время создания тестового файла", e.getCause());
        }
    }

    @Override
    @BeforeEach
    void beforeEach() {
        manager = Managers.setFileTasksManager(defualtPath.toFile());
        manager.removeAll();
        manager.createTask("Первая задача!");
        Epic epic = manager.createEpic("Эпик-задача");
        manager.createSubtask(epic.getTaskId(), "Подзадача 1");
        manager.createSubtask(epic.getTaskId(), "Подзадача 2");
    }


    @Test
    void testFileBackedTasksManager() {

        createTestFile(linesTestA);
        manager = Managers.setFileTasksManager(testFilePath.toFile());
        assertEquals(0, manager.allSize());


        createTestFile(linesTestB);
        manager = Managers.setFileTasksManager(testFilePath.toFile());
        assertEquals(0, manager.getEpic(2).size());


        createTestFile(linesTestC);
        manager = Managers.setFileTasksManager(testFilePath.toFile());
        assertEquals(0, manager.getHistoryManager().getHistory().size());
    }

    @Test
    void testLoadFromFile() {
        createTestFile(testFileLines);
        FileBackedTasksManager.loadFromFile(testFilePath.toFile());

        final ManagerSaveException saveTaskException = assertThrows(ManagerSaveException.class, new Executable() {
            @Override
            public void execute() {
                createTestFile(testErrorTaskFileLines);
                FileBackedTasksManager.loadFromFile(testFilePath.toFile());
            }
        });
        assertEquals("Произошла ошибка во время чтения файла. Обнаружен конфликт во времени исполнения задач", saveTaskException.getMessage());

        final ManagerSaveException saveSubtaskException = assertThrows(ManagerSaveException.class, new Executable() {
            @Override
            public void execute() {
                createTestFile(testErrorSubtaskFileLines);
                FileBackedTasksManager.loadFromFile(testFilePath.toFile());
            }
        });
        assertEquals("Произошла ошибка во время чтения файла. Обнаружен конфликт во времени исполнения задач", saveSubtaskException.getMessage());

        final ManagerSaveException createFileException = assertThrows(ManagerSaveException.class, new Executable() {
            @Override
            public void execute() {
                testFilePath.toFile().setWritable(false);
                manager = Managers.setFileTasksManager(testFilePath.toFile());
            }
        });
        assertEquals("Произошла ошибка во время записи в файл.", createFileException.getMessage());

        testFilePath.toFile().delete();
        Throwable thrown = assertThrows(ManagerSaveException.class, () -> {
            FileBackedTasksManager.loadFromFile(testFilePath.toFile());
        });
        assertEquals("Произошла ошибка во время чтения файла.", thrown.getMessage());
    }
}