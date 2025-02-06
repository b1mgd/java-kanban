package tracker.model;

import tracker.controllers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager taskManager;
    HistoryManager historyManager;
    Epic epic;
    Subtask subtask1, subtask2;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);

        taskManager.deleteEpics();

        epic = new Epic("Организация переезда", "Переезд в новую квартиру");
        taskManager.createEpic(epic);

        subtask1 = new Subtask("Собрать вещи", "Сложить вещи в коробки", epic.getId(),
                60L, "10.02.2025 10:00");
        subtask2 = new Subtask("Заказать грузчиков", "Позвонить в транспортную компанию",
                epic.getId(), 30L, "10.02.2025 11:00");
    }

    @Test
    void epicsAreEqualIfIdsAreEqual() {
        Epic epic1 = (Epic) taskManager.getTaskById(epic.getId());
        assertEquals(epic.getId(), epic1.getId(), "ID не равны");
        assertEquals(epic, epic1, "Экземпляры не равны");
    }

    @Test
    void shouldNotAllowEpicToAddItselfAsSubtask() {
        Subtask invalidSubtask = new Subtask("Некорректная подзадача",
                "Эпик не может быть своей подзадачей", epic.getId(), 0L, null);
        taskManager.createSubtask(invalidSubtask);
        assertFalse(epic.getSubTaskId().contains(epic.getId()),
                "ID подзадачи эпика не может совпадать с ID эпика");
    }

    @Test
    void shouldSetEpicStatusToNewWhenAllSubtasksAreNew() {
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус эпика должен быть NEW");
    }

    @Test
    void shouldSetEpicStatusToDoneWhenAllSubtasksAreDone() {
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус эпика должен быть DONE");
    }

    @Test
    void shouldSetEpicStatusToInProgressWhenSubtasksAreNewAndDone() {
        subtask1.setStatus(TaskStatus.NEW);
        subtask2.setStatus(TaskStatus.DONE);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    void shouldSetEpicStatusToInProgressWhenAtLeastOneSubtaskIsInProgress() {
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.NEW);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    void shouldCorrectlyCalculateEpicDuration() {
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(90L, epic.getDuration(), "Общая продолжительность эпика должна быть 90 минут");
    }
}