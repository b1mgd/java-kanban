package tracker.controllers;

import tracker.model.Task;

import java.util.Deque;

public interface HistoryManager {

    // Пришла в голову только такая идея, как связать между собой двух менеджеров в случае,
    // если не используем Singletone. Можно ли как-то лучше?
    void setTaskManager(TaskManager taskManager);

    TaskManager getTaskManager();

    Deque<Task> getHistoryList();

    void add(Task task);
}
