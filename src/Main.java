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

        System.out.println("\nПодзадачи для эпика 'Организация переезда':");
        System.out.println(manager.getSubtasksOfEpic(epic1.getId()));

        manager.updateStatus(task1.getId(), TaskStatus.IN_PROGRESS);
        manager.updateStatus(subtask1.getId(), TaskStatus.IN_PROGRESS);
        manager.updateStatus(subtask2.getId(), TaskStatus.DONE);

        System.out.println("\nСтатус эпика после обновления статусов подзадач:");
        System.out.println(manager.getTaskById(epic1.getId()));

        manager.updateStatus(subtask1.getId(), TaskStatus.DONE);
        System.out.println("\nСтатус эпика после завершения всех подзадач:");
        System.out.println(manager.getTaskById(epic1.getId()));

        // Обновление обычной задачи
        Task updatedTask = new Task("Закупка материалов - обновлено", "Закупка материалов для нового проекта");
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(updatedTask, task1.getId());
        System.out.println("Обновленная задача:");
        System.out.println(manager.getTaskById(task1.getId()));

        Epic updatedEpic = new Epic("Организация переезда - обновлено", "Переезд с обновленными задачами");
        updatedEpic.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(updatedEpic, epic1.getId());
        System.out.println("Обновленный эпик:");
        System.out.println(manager.getTaskById(epic1.getId()));

        Subtask updatedSubtask = new Subtask("Упаковка вещей - обновлено", "Упаковка с учетом дополнительного груза", epic1.getId());
        updatedSubtask.setStatus(TaskStatus.DONE);
        manager.updateTask(updatedSubtask, subtask1.getId());
        System.out.println("Обновленная подзадача:");
        System.out.println(manager.getTaskById(subtask1.getId()));

        System.out.println("\nУдаление задачи и эпика:");
        manager.removeTaskById(task1.getId());
        manager.removeTaskById(epic1.getId());
        System.out.println("Все задачи после удаления:");
        System.out.println(manager.getAllTasks());

        System.out.println("\nУдаление всех задач:");
        manager.removeAllTasks();
        System.out.println("Все задачи после полного удаления:");
        System.out.println(manager.getAllTasks());
    }
}

