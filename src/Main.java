import tracker.controllers.*;
import tracker.model.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);

        Task task1 = new Task("Задача 1", "Описание задачи 1",
                15L, "10.01.2025 08:00");
        Task task2 = new Task("Задача 2", "Описание задачи 2", 23L, null);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId(),
                15, "15.01.2025 09:00");
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId(),
                25, "17.02.2025 08:00");
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getId(),
                50, null);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(epic1.getId());
        taskManager.getTaskById(subtask1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(subtask2.getId());
        taskManager.getTaskById(subtask3.getId());

        System.out.println("История после нескольких запросов:");
        printHistory(historyManager);

        taskManager.getTaskById(task1.getId());

        System.out.println("История после повторного запроса задачи 1:");
        printHistory(historyManager);

        taskManager.deleteTaskById(task1.getId());
        System.out.println("История после удаления задачи 1:");
        printHistory(historyManager);

        taskManager.deleteTaskById(epic1.getId());
        System.out.println("История после удаления эпика 1 и его подзадач:");
        printHistory(historyManager);
    }

    private static void printHistory(HistoryManager historyManager) {
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }
}