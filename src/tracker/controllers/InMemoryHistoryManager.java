package tracker.controllers;

import tracker.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    class Node {
        public Task task;
        public Node prev;
        public Node next;

        public Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }

    private TaskManager taskManager;
    private final HashMap<Integer, Node> taskMap;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        head = null;
        tail = null;
        taskMap = new HashMap<>();
    }

    private void linkLast(Task task) {
        if (taskMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        Node newNode = new Node(tail, task, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        taskMap.put(task.getId(), newNode);
    }

    @Override
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> taskList = new ArrayList<>();
        Node node = head;
        while (node != null) {
            taskList.add(node.task);
            node = node.next;
        }
        return taskList;
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (taskMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    @Override
    public void remove(int id) {
        Node node = taskMap.get(id);
        if (node != null) {
            removeNode(node);
            taskMap.remove(id);
        }
    }
}