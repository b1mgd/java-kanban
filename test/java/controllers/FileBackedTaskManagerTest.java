package test.java.controllers;

import org.junit.jupiter.api.*;
import tracker.controllers.HistoryManager;
import tracker.controllers.Managers;
import tracker.controllers.TaskManager;
import tracker.model.*;
import tracker.controllers.FileBackedTaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("File", ".csv");
        historyManager = Managers.getDefaultHistory();
        taskManager = FileBackedTaskManager.loadFromFile(tempFile);

        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    void shouldSaveAndLoadTasks() {
        Task task = new Task("Task 1", "Description 1");
        taskManager.createTask(task);

        // пересоздаем менеджеры = имитация завершения программы
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        HistoryManager historyManager = Managers.getDefaultHistory();

        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);

        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(1, tasks.size(), "Задачи не были загружены");
        assertEquals("Task 1", tasks.getFirst().getName(), "Неверное имя задачи");
        assertEquals("Description 1", tasks.getFirst().getDescription(),
                "Описание задачи некорректно");
    }

    @Test
    void shouldSaveAndLoadEpicsAndSubtasks() {
        Epic epic = new Epic("Epic 1", "Description for epic");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description for subtask", epic.getId());
        taskManager.createSubtask(subtask);

        // пересоздаем менеджеры = имитация завершения программы
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        HistoryManager historyManager = Managers.getDefaultHistory();

        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);

        List<Epic> epics = taskManager.getAllEpics();
        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(1, epics.size(), "Эпики не были корректно загружены");
        assertEquals(1, subtasks.size(), "Подзадачи не были корректно загружены");

        assertEquals("Epic 1", epics.getFirst().getName(), "Имя эпика некорректно");
        assertEquals("Subtask 1", subtasks.getFirst().getName(), "Имя подзадачи некорректно");
        assertEquals(epic.getId(), subtasks.getFirst().getEpicId(), "Подзадача связана с некорректным эпиком");
    }

    @Test
    void shouldHandleEmptyFile() {
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач должен быть пустым");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void shouldSaveTasksAfterDeletion() {
        Task task1 = new Task("Task 1", "Description 1");
        taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2");
        taskManager.createTask(task2);

        taskManager.deleteTaskById(task1.getId());

        // пересоздаем менеджеры = имитация завершения программы
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(tempFile);
        HistoryManager historyManager = Managers.getDefaultHistory();

        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);

        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач после удаления");
        assertEquals("Task 2", tasks.getFirst().getName(), "Задача не была удалена");
    }
}

