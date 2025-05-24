package tracker.controllers;

import tracker.dao.EpicDao;
import tracker.dao.SubtaskDao;
import tracker.dao.TaskDao;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.List;

public class DatabaseTaskManager implements TaskManager {
    private final TaskDao taskDao = new TaskDao();
    private final EpicDao epicDao = new EpicDao();
    private final SubtaskDao subtaskDao = new SubtaskDao();
    private HistoryManager historyManager;

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
        taskDao.create(task);
    }

    @Override
    public void createEpic(Epic epic) {
        epicDao.create(epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtaskDao.create(subtask);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskDao.getAll();
    }

    @Override
    public List<Epic> getAllEpics() {
        return epicDao.getAll();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return subtaskDao.getAll();
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        return subtaskDao.getByEpicId(epicId);
    }

    @Override
    public void deleteTasks() {
        for (Task task : getAllTasks()) {
            taskDao.delete(task.getId());
        }
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : getAllEpics()) {
            epicDao.delete(epic.getId());
        }
    }

    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : getAllSubtasks()) {
            subtaskDao.delete(subtask.getId());
        }
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskDao.getById(id);
        if (task == null) {
            Epic epic = epicDao.getById(id);
            if (epic != null) {
                historyManager.add(epic);
                return epic;
            }
            Subtask subtask = subtaskDao.getById(id);
            if (subtask != null) {
                historyManager.add(subtask);
                return subtask;
            }
            return null;
        } else {
            historyManager.add(task);
            return task;
        }
    }

    @Override
    public void deleteTaskById(int id) {
        taskDao.delete(id);
        epicDao.delete(id);
        subtaskDao.delete(id);
    }

    @Override
    public void updateTask(Task updatedTask, int id) {
        Task task = taskDao.getById(id);
        if (task != null) {
            updatedTask.setId(id);
            taskDao.update(updatedTask);
            return;
        }
        Epic epic = epicDao.getById(id);
        if (epic != null && updatedTask instanceof Epic) {
            updatedTask.setId(id);
            epicDao.update((Epic) updatedTask);
            return;
        }
        Subtask subtask = subtaskDao.getById(id);
        if (subtask != null && updatedTask instanceof Subtask) {
            updatedTask.setId(id);
            subtaskDao.update((Subtask) updatedTask);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager != null ? historyManager.getHistory() : List.of();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        // Для простоты возвращаем все задачи (можно реализовать сортировку по времени)
        List<Task> all = getAllTasks();
        all.addAll(getAllEpics());
        all.addAll(getAllSubtasks());
        return all;
    }
} 