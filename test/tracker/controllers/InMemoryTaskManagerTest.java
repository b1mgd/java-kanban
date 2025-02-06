package tracker.controllers;

import tracker.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @Override
    protected TaskManager createTaskManager() {
        return Managers.getDefault();
    }

    @BeforeEach
    void setUp() {
        super.setUp();
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
        assertFalse(epic.getSubTaskId().isEmpty(), "Список подзадач пуст");

        assertEquals(1, epics.size(), "Неверное количество эпиков");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают");

        taskManager.deleteEpics();

        epics = taskManager.getAllEpics();

        assertTrue(epics.isEmpty(), "После очистки список не пуст");
    }

    @Test
    void createNewSubtask() {
        Subtask savedSubtask1 = (Subtask) taskManager.getTaskById(subtask1.getId());
        Subtask savedSubtask2 = (Subtask) taskManager.getTaskById(subtask2.getId());

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
        taskManager.getAllEpics().forEach(System.out::println);
        subtasks = taskManager.getAllSubtasks();
        taskManager.getAllSubtasks().forEach(System.out::println);

        assertTrue(epics.isEmpty() && subtasks.isEmpty(), "После очистки списки не пусты");

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);

        taskManager.deleteEpics();
        taskManager.deleteSubtasks();

        assertTrue(taskManager.getAllSubtasks().isEmpty() && taskManager.getAllEpics().isEmpty(),
                "Список подзадач после очистки не пуст");
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

    @Test
    void shouldThrowExceptionWhenOverlappingTaskCreated() {
        Task overlappingTask = new Task("Конфликтующая задача", "Описание конфликтующей задачи",
                15, "01.02.2025 10:10");
        assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(overlappingTask));
    }
}