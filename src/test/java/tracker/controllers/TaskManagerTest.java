package tracker.controllers;

import tracker.model.*;
import org.junit.jupiter.api.*;
import tracker.dao.DatabaseInitializer;

import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected HistoryManager historyManager;
    protected Task task1, task2;
    protected Epic epic;
    protected Subtask subtask1, subtask2;

    protected List<Task> tasks;
    protected List<Epic> epics;
    protected List<Subtask> subtasks;
    protected List<Subtask> subtasksOfEpic;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        DatabaseInitializer.initialize();
        taskManager = createTaskManager();
        historyManager = Managers.getDefaultHistory();

        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);

        taskManager.deleteTasks();
        taskManager.deleteEpics();
        taskManager.deleteSubtasks();

        task1 = new Task("Закупка материалов", "Закупка необходимых материалов для проекта",
                30L, "01.02.2025 10:00");
        task2 = new Task("Создание плана проекта", "Создать план и распределить задачи",
                60L, "01.02.2025 10:30");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        epic = new Epic("Организация переезда", "Переезд в новую квартиру");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        subtask1 = new Subtask("Упаковка вещей", "Упаковать вещи в коробки", epicId,
                60L, "01.02.2025 18:00");
        subtask2 = new Subtask("Заказ транспорта", "Заказать грузовое такси", epicId,
                60L, "01.02.2025 19:00");
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        tasks = taskManager.getAllTasks();
        epics = taskManager.getAllEpics();
        subtasks = taskManager.getAllSubtasks();
        subtasksOfEpic = taskManager.getSubtasksOfEpic(epic.getId());
    }
}