package tracker.controllers;

import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.util.ArrayList;

public interface TaskManager {

    // Аналогично пришлось дополнить, чтобы связать объекты менеджеров
    void setHistoryManager(HistoryManager historyManager);

    HistoryManager getHistoryManager();

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Subtask> getSubtasksOfEpic(int epicId);

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    Task getTaskById(int id);

    void deleteTaskById(int id);

    void updateTask(Task updatedTask, int id);

    ArrayList<Task> getHistory();
}
