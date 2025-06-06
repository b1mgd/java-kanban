package tracker.model;

import tracker.controllers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    TaskManager taskManager;
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);
    }

    @Test
    void tasksAreEqualIfIdsAreEqual() {
        Task task = new Task("Закупка материалов", "Закупка необходимых материалов для проекта",
                0L, null);
        taskManager.createTask(task);
        Task task1 = taskManager.getTaskById(task.getId());
        assertEquals(task.getId(), task1.getId(), "ID не равны");
        assertEquals(task, task1, "Метод equals переопределен некорректно");
    }
}