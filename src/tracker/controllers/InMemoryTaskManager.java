package tracker.controllers;

import tracker.model.*;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private HistoryManager historyManager;
    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final Map<Integer, Epic> epicMap = new HashMap<>();
    private final Map<Integer, Subtask> subtaskMap = new HashMap<>();
    private int idCounter = 1;

    /*
    Реализовал добавление задач со startTime = null таким образом, чтобы они добавляются в конец.
    Логика работы компаратора не ломается (из текста условия: "может сломаться")
     */

    private final Comparator<Task> timeComparator = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);
    private final Set<Task> taskSet = new TreeSet<>(timeComparator);

    private boolean doesNotIntersect(Task task) {
        LocalDateTime taskStartTime = task.getStartTime();
        LocalDateTime taskEndTime = task.getEndTime();

        if (taskStartTime == null || taskEndTime == null) {
            return true;
        }

        return getPrioritizedTasks().stream()
                .filter(setTask -> setTask.getStartTime() != null && setTask.getEndTime() != null)
                .noneMatch(setTask -> {

                    /*
                    Проверка ниже проводится, чтобы допустить пересечение времени у задачи из taskSet
                    и заменяющей ее задачи из updateTask(updatedTask, id).
                    Можно ли корректнее сделать, если equals() и updatedTask.getId() не сработают,
                    т.к. не всем полям присвоено значения до добавления в менеджер?
                     */

                    if ((setTask.getName().equals(task.getName())
                            && setTask.getDescription().equals(task.getDescription()))
                            || setTask.getType() == TaskType.EPIC
                            || task.getType() == TaskType.EPIC) {
                        return false;
                    }

                    LocalDateTime setStartTime = setTask.getStartTime();
                    LocalDateTime setEndTime = setTask.getEndTime();

                    return !(taskEndTime.isBefore(setStartTime) || taskStartTime.isAfter(setEndTime.minusSeconds(1)));
                });
    }

    @Override
    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    /*
    Для задач типа add (у меня - create) не ясна логика задания "выполнять проверки на пересечение
    с помощью Stream API" (только усложняет код). Выполнил обычную проверку с if (doesNotIntersect)
    */

    @Override
    public void createTask(Task task) {
        task.setId(idCounter++);

        if (doesNotIntersect(task)) {
            taskMap.put(task.getId(), task);
            taskSet.add(task);
        } else {
            throw new IllegalArgumentException("Задача пересекается по времени с существующей");
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);

        if (doesNotIntersect(epic)) {
            epicMap.put(epic.getId(), epic);
            taskSet.add(epic);
        } else {
            throw new IllegalArgumentException("Задача пересекается по времени с существующей");
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (epicId == subtask.getId()) {
            return;
        }

        Epic epic = epicMap.get(epicId);
        if (epicMap.containsKey(epicId)) {
            if (doesNotIntersect(subtask)) {
                subtask.setId(idCounter++);
                subtaskMap.put(subtask.getId(), subtask);

                taskSet.add(subtask);
                epic.addSubtaskId(subtask.getId());
                epic.updateStatus(subtaskMap);
                epic.updateTime(subtaskMap, taskSet);
            } else {
                throw new IllegalArgumentException("Задача пересекается по времени с существующей");
            }
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        List<Subtask> subtasks = new ArrayList<>();

        if (!epicMap.containsKey(epicId))
            return subtasks;

        Set<Integer> subtaskIds = epicMap.get(epicId).getSubTaskId();

        // Collections.forEach() вместо .stream(). В задании не понял, для чего стрим

        subtaskIds.forEach(id -> subtasks.add(subtaskMap.get(id)));

        return subtasks;
    }

    @Override
    public void deleteTasks() {
        taskMap.values().forEach(task -> historyManager.remove(task.getId()));
        taskMap.clear();

        taskSet.removeIf(task -> task.getType() == TaskType.TASK);
    }

    @Override
    public void deleteEpics() {
        epicMap.values().forEach(epic -> {
            historyManager.remove(epic.getId());
            epic.getSubTaskId().forEach(id -> historyManager.remove(id));
        });
        epicMap.clear();
        subtaskMap.clear();

        taskSet.removeIf(epic -> epic.getType() == TaskType.EPIC);
    }

    @Override
    public void deleteSubtasks() {
        subtaskMap.values().forEach(subtask -> historyManager.remove(subtask.getId()));
        subtaskMap.clear();

        taskSet.removeIf(subtask -> subtask.getType() == TaskType.SUBTASK);

        epicMap.values().forEach(epic -> {
            epic.getSubTaskId().clear();
            epic.updateStatus(subtaskMap);
            epic.updateTime(subtaskMap, taskSet);
        });
    }

    @Override
    public Task getTaskById(int id) {
        Task task;

        if (taskMap.containsKey(id))
            task = taskMap.get(id);
        else if (epicMap.containsKey(id))
            task = epicMap.get(id);
        else
            task = subtaskMap.get(id);

        if (task != null)
            historyManager.add(task);

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
                epic.updateTime(subtaskMap, taskSet);
            }
        }
    }

    @Override
    public void updateTask(Task updatedTask, int id) {
        if (doesNotIntersect(updatedTask)) {

            if (taskMap.containsKey(id)) {
                Task task = taskMap.get(id);
                task.setName(updatedTask.getName());
                task.setDescription(updatedTask.getDescription());
                task.setStatus(updatedTask.getStatus());
                task.setDuration(updatedTask.getDuration());
                task.setStartTime(updatedTask.getStartTime());
                historyManager.add(task);

            } else if (epicMap.containsKey(id)) {
                Epic epic = epicMap.get(id);
                epic.setName(updatedTask.getName());
                epic.setDescription(updatedTask.getDescription());
                epic.setStatus(updatedTask.getStatus());
                epic.setDuration(updatedTask.getDuration());
                epic.setStartTime(updatedTask.getStartTime());

            } else if (subtaskMap.containsKey(id)) {
                Subtask subtask = subtaskMap.get(id);
                subtask.setName(updatedTask.getName());
                subtask.setDescription(updatedTask.getDescription());
                subtask.setStatus(updatedTask.getStatus());
                subtask.setDuration(updatedTask.getDuration());
                subtask.setStartTime(updatedTask.getStartTime());

                Epic epic = epicMap.get(subtask.getEpicId());
                if (epic != null) {
                    historyManager.add(subtask);
                    epic.updateStatus(subtaskMap);
                    epic.updateTime(subtaskMap, taskSet);
                }
            }
        } else {
            throw new IllegalArgumentException("Задача пересекается по времени с существующей");
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return taskSet;
    }
}