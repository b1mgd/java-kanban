package tracker.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseHistoryManagerTest {
    private HistoryManager historyManager;
    private TaskManager taskManager;
    private Task task1, task2, task3;

    @BeforeEach
    void setUp() {
        historyManager = new DatabaseHistoryManager();
        taskManager = new DatabaseTaskManager();
        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);

        task1 = new Task("Задача 1", "Описание задачи 1",
                10, "01.03.2025 10:00");
        task2 = new Task("Задача 2", "Описание задачи 2",
                20, "02.03.2025 11:00");
        task3 = new Task("Задача 3", "Описание задачи 3", 0, null);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
    }

    @Test
    void shouldReturnEmptyHistoryInitially() {
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой при инициализации.");
    }

    @Test
    void shouldAddTasksToHistory() {
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задачи.");
        assertEquals(task1, history.get(0), "Задача 1 должна быть первой.");
        assertEquals(task2, history.get(1), "Задача 2 должна быть второй.");
    }

    @Test
    void shouldNotDuplicateTaskInHistory() {
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История должна содержать только 1 экземпляр задачи.");
        assertEquals(task1, history.get(0), "Задача должна быть в истории.");
    }

    @Test
    void shouldRemoveTaskFromHistory_Start() {
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления.");
        assertFalse(history.contains(task1), "Первая задача должна быть удалена.");
    }

    @Test
    void shouldRemoveTaskFromHistory_Middle() {
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления.");
        assertFalse(history.contains(task2), "Средняя задача должна быть удалена.");
    }

    @Test
    void shouldRemoveTaskFromHistory_End() {
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления.");
        assertFalse(history.contains(task3), "Последняя задача должна быть удалена.");
    }
}