package test;

import controllers.*;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    // ТЗ: равенство экземпляров Task при равенстве ID
    void tasksAreEqualIfIdsAreEqual() {
        Task task = new Task("Закупка материалов", "Закупка необходимых материалов для проекта");
        taskManager.createTask(task);
        Task task1 = taskManager.getTaskById(task.getId());
        assertEquals(task.getId(), task1.getId(), "ID не равны");
        assertEquals(task, task1, "Метод equals переопределен некорректно");
    }
}