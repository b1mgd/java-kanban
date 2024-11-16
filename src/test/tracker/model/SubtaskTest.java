package test.tracker.model;

import tracker.controllers.*;
import tracker.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    TaskManager taskManager;
    HistoryManager historyManager;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);

        taskManager.deleteEpics();

        epic = new Epic("Организация переезда", "Переезд в новую квартиру");
        taskManager.createEpic(epic);
        subtask = new Subtask("Упаковка вещей", "Упаковать вещи в коробки", epic.getId());
        taskManager.createSubtask(subtask);

    }

    @Test
    void subtasksAreEqualIfIdsAreEqual() {
        Subtask subtask1 = (Subtask) taskManager.getTaskById(subtask.getId());
        assertEquals(subtask.getId(), subtask1.getId(), "ID не равны");
        assertEquals(subtask, subtask1, "Экземпляры не равны");
    }

    @Test
    void shouldNotAllowSubtaskToAddItselfAsEpic() {
        Subtask invalidSubtask = new Subtask("Неверная подзадача",
                "Попытка сделать подзадачу своим эпиком", epic.getId());
        invalidSubtask.setId(epic.getId());
        taskManager.createSubtask(invalidSubtask);
        assertFalse(taskManager.getAllSubtasks().contains(invalidSubtask),
                "ID подзадачи эпика не может совпадать с ID эпика");
    }
}