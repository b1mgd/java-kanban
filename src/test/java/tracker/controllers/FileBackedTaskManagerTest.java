package tracker.controllers;

import org.junit.jupiter.api.*;
import tracker.controllers.exceptions.ManagerLoadException;
import tracker.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {
    private static Path tempFile;

    private void reloadTaskManager() {
        taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        HistoryManager historyManager = Managers.getDefaultHistory();

        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);
    }

    @Override
    protected TaskManager createTaskManager() {
        try {
            Files.write(tempFile, new byte[0]);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return FileBackedTaskManager.loadFromFile(tempFile);
    }

    @BeforeAll
    static void setUpFirst() throws IOException {
        tempFile = Files.createTempFile("File", ".csv");
    }

    @BeforeEach
    void setUp() {
        super.setUp();
        reloadTaskManager();
    }

    @AfterEach
    void clearTempFile() throws IOException {
        Files.write(tempFile, new byte[0]);
    }

    @AfterAll
    static void deleteTempFile() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void shouldSaveAndLoadTasks() {
        assertEquals(2, tasks.size(), "Задачи не были загружены");
        assertEquals(task1.getName(), tasks.get(0).getName(), "Неверное имя задачи");
        assertEquals(task1.getDescription(), tasks.get(0).getDescription(),
                "Описание задачи некорректно");
    }

    @Test
    void shouldSaveAndLoadEpicsAndSubtasks() {
        assertEquals(1, epics.size(), "Эпики не были корректно загружены");
        assertEquals(2, subtasks.size(), "Подзадачи не были корректно загружены");

        assertEquals(epic.getName(), epics.get(0).getName(), "Имя эпика некорректно");
        assertEquals(subtask1.getName(), subtasks.get(0).getName(), "Имя подзадачи некорректно");
        assertEquals(epic.getId(), subtasks.get(0).getEpicId(), "Подзадача связана с некорректным эпиком");
    }

    @Test
    void shouldHandleEmptyFile() {
        try {
            Files.write(tempFile, new byte[0]); // Явно очищаем файл
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        reloadTaskManager();

        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач должен быть пустым");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустым");
        assertTrue(taskManager.getHistory().isEmpty(), "История просмотров должна быть пустой");
    }

    @Test
    void shouldSaveTasksAfterDeletion() {
        taskManager.deleteTaskById(task1.getId());

        reloadTaskManager();

        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач после удаления");
        assertEquals(task2.getName(), tasks.get(0).getName(), "Задача не была удалена");
    }

    @Test
    void shouldThrowExceptionWhenSavingToNonExistentDirectory() {
        Path invalidPath = Paths.get("/invalid/directory/file.csv");

        Exception exception = assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(invalidPath));
        assertEquals("Ошибка в процессе загрузки файла", exception.getMessage(),
                "Сообщение об ошибке не совпадает");
    }
}