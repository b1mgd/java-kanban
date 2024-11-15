package controllers;

import model.Task;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class InMemoryHistoryManager implements HistoryManager {

    private final Deque<Task> historyList = new ArrayDeque<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
       if (task == null) return;
       if (historyList.size() >= MAX_HISTORY_SIZE) {
           historyList.removeFirst();
       }
       historyList.addLast(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyList);
    }
}
