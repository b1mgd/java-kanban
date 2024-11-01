import controllers.TaskManager;
import model.*;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task("Закупка материалов", "Закупка необходимых материалов для проекта");
        Task task2 = new Task("Создание плана проекта", "Создать план и распределить задачи");
        manager.createTask(task1);
        manager.createTask(task2);

        System.out.println("Все задачи:");
        System.out.println(manager.getAllTasks());

        Epic epic1 = new Epic("Организация переезда", "Переезд в новую квартиру");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Упаковка вещей", "Упаковать вещи в коробки", epic1.getId());
        Subtask subtask2 = new Subtask("Заказ транспорта", "Заказать грузовое такси", epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        Epic epic2 = new Epic("Планирование вечеринки", "Организовать семейное празднование");
        manager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Заказ еды", "Заказать еду для вечеринки", epic2.getId());
        manager.createSubtask(subtask3);

        System.out.println("\nВсе задачи после добавления эпиков и подзадач:");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        Task updatedTask = new Task("Закупка материалов - обновлено", "Закупка материалов для нового проекта");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        System.out.println("Обновленная задача:");
        System.out.println(manager.getTaskById(task1.getId()));

        Epic updatedEpic = new Epic("Организация переезда - обновлено", "Переезд с обновленными задачами");
        updatedEpic.setStatus(TaskStatus.IN_PROGRESS);
        System.out.println("Обновленный эпик:");
        System.out.println(manager.getTaskById(epic1.getId()));

        Subtask updatedSubtask = new Subtask("Упаковка вещей - обновлено", "Упаковка с учетом дополнительного груза", epic1.getId());
        updatedSubtask.setStatus(TaskStatus.DONE);
        System.out.println("Обновленная подзадача:");
        System.out.println(manager.getTaskById(subtask1.getId()));

        System.out.println("\nУдаление задачи и эпика:");
        manager.deleteTaskById(task1.getId());
        manager.deleteTaskById(epic1.getId());
        System.out.println("Все задачи после удаления:");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());

        System.out.println("\nУдаление всех задач:");
        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubtasks();
        System.out.println("Все задачи после полного удаления:");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
    }
}

