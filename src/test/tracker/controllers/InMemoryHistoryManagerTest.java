package test.tracker.controllers;

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
        Task task2 = new Task("Создание плана проекта", "Создать план и распределить задачи");
        Task task3 = new Task("Заказ транспорта", "Заказать грузовое такси");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать 3 задачи.");

        assertTrue(history.contains(task1), "Задача 1 не найдена в истории.");
        assertTrue(history.contains(task2), "Задача 2 не найдена в истории.");
        assertTrue(history.contains(task3), "Задача 3 не найдена в истории.");
    }

    @Test
    void remove() {
        Task task1 = new Task("Закупка материалов", "Закупка необходимых материалов для проекта");
        Task task2 = new Task("Создание плана проекта", "Создать план и распределить задачи");
        historyManager.add(task1);
        historyManager.add(task2);

        assertEquals(2, historyManager.getHistory().size(), "История должна содержать 2 задачи.");

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать 1 задачу.");
        assertFalse(history.contains(task1), "Задача 1 не должна быть в истории.");
    }

    @Test
    void addDuplicate() {
        Task task1 = new Task("Закупка материалов", "Закупка необходимых материалов для проекта");
        Task task2 = new Task("Создание плана проекта", "Создать план и распределить задачи");
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи.");
        assertTrue(history.contains(task1), "Задача 1 должна быть в истории.");
        assertTrue(history.contains(task2), "Задача 2 должна быть в истории.");
    }
}
