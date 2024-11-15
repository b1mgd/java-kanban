package controllers;

import model.*;
import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    private int idCounter = 1;

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
        if (epicId == subtask.getId()) {return;}
        if (epicMap.containsKey(epicId)) {
            subtask.setId(idCounter++);
            subtaskMap.put(subtask.getId(), subtask);
            Epic epic = epicMap.get(epicId);
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
        taskMap.clear();
    }

    @Override
    public void deleteEpics() {
        epicMap.clear();
        subtaskMap.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epicMap.values()) {
            epic.getSubTaskId().clear();
            epic.updateStatus(subtaskMap);
        }
        subtaskMap.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task;
        if (taskMap.containsKey(id)) task = taskMap.get(id);
        else if (epicMap.containsKey(id)) task = epicMap.get(id);
        else task = subtaskMap.get(id);
        if (task != null) Managers.getDefaultHistory().add(task);
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        if (taskMap.remove(id) == null) {
            if (epicMap.containsKey(id)) {
                for (Integer subtaskId : epicMap.get(id).getSubTaskId()) {
                    subtaskMap.remove(subtaskId);
                }
                epicMap.remove(id);
            } else {
                Subtask subtask = subtaskMap.remove(id);
                if (subtask != null) {
                    Epic epic = epicMap.get(subtask.getEpicId());
                    epic.getSubTaskId().remove((Integer) id);
                    epic.updateStatus(subtaskMap);
                }
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
        } else if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            epic.setName(updatedTask.getName());
            epic.setDescription(updatedTask.getDescription());
            epic.setStatus(updatedTask.getStatus());
        } else if (subtaskMap.containsKey(id)) {
            Subtask subtask = subtaskMap.get(id);
            subtask.setName(updatedTask.getName());
            subtask.setDescription(updatedTask.getDescription());
            subtask.setStatus(updatedTask.getStatus());
        }
    }
}
