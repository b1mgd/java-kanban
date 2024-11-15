package test;

import controllers.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager;
    Epic epic;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        taskManager.deleteEpics();
        epic = new Epic("Организация переезда", "Переезд в новую квартиру");
        taskManager.createEpic(epic);
    }

    @Test
    // ТЗ: равенство экземпляров наследников Task при равенстве их ID
    void epicsAreEqualIfIdsAreEqual() {
        Epic epic1 = (Epic) taskManager.getTaskById(epic.getId());
        assertEquals(epic.getId(), epic1.getId(), "ID не равны");
        assertEquals(epic, epic1, "Экземпляры не равны");
    }

    @Test
    void shouldNotAllowEpicToAddItselfAsSubtask() {
        Subtask invalidSubtask = new Subtask("Некорректная подзадача",
                "Эпик не может быть своей подзадачей", epic.getId());
        taskManager.createSubtask(invalidSubtask);
        assertFalse(epic.getSubTaskId().contains(epic.getId()),
                "ID подзадачи эпика не может совпадать с ID эпика");
    }

}