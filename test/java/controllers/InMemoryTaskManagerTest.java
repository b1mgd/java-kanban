package test.java.controllers;

import tracker.model.*;
import tracker.controllers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager taskManager;
    HistoryManager historyManager;
    Task task1, task2;
    Epic epic;
    Subtask subtask1, subtask2;
    ArrayList<Task> tasks;
    ArrayList<Epic> epics;
    ArrayList<Subtask> subtasks;
    ArrayList<Subtask> subtasksOfEpic;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();

        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);

        taskManager.deleteTasks();
        taskManager.deleteEpics();

        task1 = new Task("Закупка материалов", "Закупка необходимых материалов для проекта");
        taskManager.createTask(task1);
        task2 = new Task("Создание плана проекта", "Создать план и распределить задачи");
        taskManager.createTask(task2);
        epic = new Epic("Организация переезда", "Переезд в новую квартиру");
        taskManager.createEpic(epic);
        subtask1 = new Subtask("Упаковка вещей", "Упаковать вещи в коробки", epic.getId());
        taskManager.createSubtask(subtask1);
        subtask2 = new Subtask("Заказ транспорта", "Заказать грузовое такси", epic.getId());
        taskManager.createSubtask(subtask2);

        tasks = taskManager.getAllTasks();
        epics = taskManager.getAllEpics();
        subtasks = taskManager.getAllSubtasks();
        subtasksOfEpic = taskManager.getSubtasksOfEpic(epic.getId());
    }

    @Test
    void createNewTask() {
        Task savedTask = taskManager.getTaskById(task1.getId());

        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask, "Задачи не совпадают");

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Неверное количество задач");
        assertEquals(task1, tasks.getFirst(), "Задачи не совпадают");

        taskManager.deleteTasks();
        tasks = taskManager.getAllTasks();

        assertTrue(tasks.isEmpty(), "После очистки список не пуст");
    }

    @Test
    void createNewEpic() {
        Epic savedEpic = (Epic) taskManager.getTaskById(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic, savedEpic, "Эпики не совпадают");

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Неверное количество эпиков");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают");
        assertFalse(epic.getSubTaskId().isEmpty(), "Список подзадач пуст");

        taskManager.deleteEpics();
        epics = taskManager.getAllEpics();

        assertTrue(epics.isEmpty(), "После очистки список не пуст");
    }

    @Test
    void createNewSubtask() {
        Subtask savedSubtask1 = (Subtask) taskManager.getTaskById(subtask1.getId());
        Subtask savedSubtask2 = (Subtask) taskManager.getTaskById(subtask2.getId());

        assertNotNull(savedSubtask1, "Подзадача не найдена");
        assertNotNull(savedSubtask2, "Подзадача не найдена");
        assertEquals(subtask1, savedSubtask1, "Подзадачи не совпадают");
        assertEquals(subtask2, savedSubtask2, "Подзадачи не совпадают");
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач");
        assertEquals(subtask1, subtasks.get(0), "Подзадачи не совпадают");
        assertEquals(subtask2, subtasks.get(1), "Подзадачи не совпадают");
        assertNotNull(subtasksOfEpic, "Подзадачи не возвращаются");
        assertEquals(2, subtasksOfEpic.size(), "Неверное количество подзадач");
        assertEquals(subtask1, subtasksOfEpic.get(0), "Подзадачи не совпадают");
        assertEquals(subtask2, subtasksOfEpic.get(1), "Подзадачи не совпадают");

        taskManager.deleteEpics();

        epics = taskManager.getAllEpics();
        subtasks = taskManager.getAllSubtasks();

        assertTrue(epics.isEmpty() && subtasks.isEmpty(), "После очистки списки не пусты");

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.deleteSubtasks();

        subtasks = taskManager.getAllSubtasks();

        assertTrue(subtasks.isEmpty() && epic.getSubTaskId().isEmpty(), "Список подзадач " +
                "после очистки не пуст");
    }

    @Test
    void deleteTaskById() {
        taskManager.deleteTaskById(task2.getId());
        tasks = taskManager.getAllTasks();

        assertEquals(1, tasks.size(), "Задача не была удалена");
    }

    @Test
    void updateTask() {
        taskManager.updateTask(task2, task1.getId());
        Task updatedTask = taskManager.getTaskById(task1.getId());

        assertEquals(task2.getName(), updatedTask.getName(), "Задача по данному ID не была обновлена");
        assertEquals(task2.getDescription(), updatedTask.getDescription(), "Задача по данному ID " +
                "не была обновлена");
        assertEquals(task2.getStatus(), updatedTask.getStatus(), "Задача по данному ID не была обновлена");
    }

    @Test
    void addedToManagerTaskShouldBeTheSame() {
        assertEquals(task1.getId(), taskManager.getTaskById(task1.getId()).getId());
        assertEquals(task1.getStatus(), taskManager.getTaskById(task1.getId()).getStatus());
        assertEquals(task1.getDescription(), taskManager.getTaskById(task1.getId()).getDescription());
        assertEquals(task1.getName(), taskManager.getTaskById(task1.getId()).getName());
    }
}
