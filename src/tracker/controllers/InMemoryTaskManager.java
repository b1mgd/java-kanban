package tracker.controllers;

import tracker.model.*;

import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {

    private HistoryManager historyManager;
    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    private int idCounter = 1;

    @Override
    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void createTask(Task task) {
        task.setId(idCounter++);
        taskMap.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (epicId == subtask.getId()) {
            return;
        }
        Epic epic = epicMap.get(epicId);
        if (epicMap.containsKey(epicId)) {
            subtask.setId(idCounter++);
            subtaskMap.put(subtask.getId(), subtask);
            epic.addSubtaskId(subtask.getId());
            epic.updateStatus(subtaskMap);
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        if (!epicMap.containsKey(epicId)) {
            return new ArrayList<>();
        }
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Integer id : epicMap.get(epicId).getSubTaskId()) {
            subtasks.add(subtaskMap.get(id));
        }
        return subtasks;
    }

    @Override
    public void deleteTasks() {
        for (Task task : taskMap.values()) {
            historyManager.remove(task.getId());
        }
        taskMap.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epicMap.values()) {
            historyManager.remove(epic.getId());
            for (Integer subtaskId : epic.getSubTaskId()) {
                historyManager.remove(subtaskId);
            }
        }
        epicMap.clear();
        subtaskMap.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtaskMap.values()) {
            historyManager.remove(subtask.getId());
        }
        subtaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.getSubTaskId().clear();
            epic.updateStatus(subtaskMap);
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task;
        if (taskMap.containsKey(id)) task = taskMap.get(id);
        else if (epicMap.containsKey(id)) task = epicMap.get(id);
        else task = subtaskMap.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        if (taskMap.remove(id) != null) {
            historyManager.remove(id);
        } else if (epicMap.containsKey(id)) {
            Epic epic = epicMap.remove(id);
            for (Integer subtaskId : epic.getSubTaskId()) {
                subtaskMap.remove(subtaskId);
            }
            historyManager.remove(id);
        } else if (subtaskMap.containsKey(id)) {
            Subtask subtask = subtaskMap.remove(id);
            historyManager.remove(id);
            Epic epic = epicMap.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubTaskId().remove(id);
                epic.updateStatus(subtaskMap);
            }
        }
    }

    @Override
    public void updateTask(Task updatedTask, int id) {
        if (taskMap.containsKey(id)) {
            Task task = taskMap.get(id);
            task.setName(updatedTask.getName());
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus());
            historyManager.add(task);
        } else if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            epic.setName(updatedTask.getName());
            epic.setDescription(updatedTask.getDescription());
        } else if (subtaskMap.containsKey(id)) {
            Subtask subtask = subtaskMap.get(id);
            subtask.setName(updatedTask.getName());
            subtask.setDescription(updatedTask.getDescription());
            subtask.setStatus(updatedTask.getStatus());
            Epic epic = epicMap.get(subtask.getEpicId());
            if (epic != null) {
                epic.updateStatus(subtaskMap);
                historyManager.add(subtask);
            }
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }
}