package test.java.controllers;

import tracker.controllers.*;
import tracker.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void shouldReturnManagersReadyToWork() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);

        assertNotNull(taskManager);
        assertTrue(taskManager instanceof InMemoryTaskManager);

        Task task = new Task("Задача", "Описание задачи");
        taskManager.createTask(task);
        assertFalse(taskManager.getAllTasks().isEmpty());

        assertNotNull(historyManager);
        assertEquals(InMemoryHistoryManager.class, historyManager.getClass());

        taskManager.getTaskById(task.getId());
        assertFalse(taskManager.getHistory().isEmpty());
    }
}