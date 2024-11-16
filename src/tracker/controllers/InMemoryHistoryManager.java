package tracker.controllers;

import tracker.model.Task;

import java.util.ArrayDeque;
import java.util.Deque;

public class InMemoryHistoryManager implements HistoryManager {

    private TaskManager taskManager;
    private Deque<Task> historyList = new ArrayDeque<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public TaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public Deque<Task> getHistoryList() {
        return historyList;
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (historyList.size() >= MAX_HISTORY_SIZE) {
            historyList.removeFirst();
        }
        historyList.addLast(task);
    }
}
