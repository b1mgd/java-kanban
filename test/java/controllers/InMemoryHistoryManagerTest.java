package test.java.controllers;

import tracker.controllers.*;
import tracker.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager;
    TaskManager taskManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);
    }

    @Test
    void add() {
        Task task1 = new Task("Закупка материалов", "Закупка необходимых материалов для проекта");
        taskManager.createTask(task1);
        Task task2 = new Task("Создание плана проекта", "Создать план и распределить задачи");
        taskManager.createTask(task2);
        Task task3 = new Task("Заказ транспорта", "Заказать грузовое такси");
        taskManager.createTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать 3 задачи.");

        assertTrue(history.contains(task1), "Задача 1 не найдена в истории.");
        assertTrue(history.contains(task2), "Задача 2 не найдена в истории.");
        assertTrue(history.contains(task3), "Задача 3 не найдена в истории.");
    }

    @Test
    void remove() {
        Task task1 = new Task("Закупка материалов", "Закупка необходимых материалов для проекта");
        taskManager.createTask(task1);
        Task task2 = new Task("Создание плана проекта", "Создать план и распределить задачи");
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        assertEquals(2, historyManager.getHistory().size(), "История должна содержать 2 задачи.");

        taskManager.deleteTaskById(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать 1 задачу.");
        assertFalse(history.contains(task2), "Задача 1 не должна быть в истории.");
    }

    @Test
    void addDuplicate() {
        Task task1 = new Task("Закупка материалов", "Закупка необходимых материалов для проекта");
        taskManager.createTask(task1);
        Task task2 = new Task("Создание плана проекта", "Создать план и распределить задачи");
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи.");
        assertEquals(history.getFirst().getId(), 2, "Задача 2 должна быть в истории первой");
    }
}
