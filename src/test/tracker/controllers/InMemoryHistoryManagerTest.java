package test.tracker.controllers;

import tracker.controllers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.*;
import java.util.ArrayList;

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
        Task task = new Task("Закупка материалов", "Закупка необходимых материалов для проекта");
        historyManager.add(task);
        ArrayList<Task> history = new ArrayList<>(historyManager.getHistoryList());
        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
        assertEquals(task, history.getFirst());
    }
}