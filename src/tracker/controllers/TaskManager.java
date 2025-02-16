package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.List;

public interface TaskManager {

    void setHistoryManager(HistoryManager historyManager);

    HistoryManager getHistoryManager();

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getSubtasksOfEpic(int epicId);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    Task getTaskById(int id);

    void deleteTaskById(int id);

    void updateTask(Task updatedTask, int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}