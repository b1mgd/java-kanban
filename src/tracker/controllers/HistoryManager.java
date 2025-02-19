package tracker.controllers;

import tracker.model.Task;

import java.util.List;

public interface HistoryManager {

    void setTaskManager(TaskManager taskManager);

    List<Task> getHistory();

    void add(Task task);

    void remove(int id);
}