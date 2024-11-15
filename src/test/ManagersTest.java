package test;

import controllers.*;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    // ТЗ: Managers всегда возвращает которые к работе экземпляры менеджеров
    void shouldReturnManagersReadyToWork() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
        assertTrue(taskManager instanceof InMemoryTaskManager);

        Task task = new Task("Задача", "Описание задачи");

        taskManager.createTask(task);
        assertFalse(taskManager.getAllTasks().isEmpty());

        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
        assertTrue(historyManager instanceof InMemoryHistoryManager);
        historyManager.add(task);
        assertFalse(historyManager.getHistory().isEmpty());
    }

}