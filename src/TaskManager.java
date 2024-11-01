import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    private int idCounter = 1;

    public void createTask(Task task) {
        task.setId(idCounter++);
        taskMap.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(idCounter++);
        epicMap.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (epicMap.containsKey(epicId)) {
            subtask.setId(idCounter++);
            subtaskMap.put(subtask.getId(), subtask);
            Epic epic = epicMap.get(epicId);
            epic.addSubtaskId(subtask.getId());
            epic.updateStatus(subtaskMap);
        }
    }

     public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.addAll(taskMap.values());
        tasks.addAll(epicMap.values());
        tasks.addAll(subtaskMap.values());
        return tasks;
     }

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

    public void removeAllTasks() {
        taskMap.clear();
        epicMap.clear();
        subtaskMap.clear();
    }

    public Task getTaskById(int id) {
        if (taskMap.containsKey(id)) return taskMap.get(id);
        else if (epicMap.containsKey(id)) return epicMap.get(id);
        else return subtaskMap.get(id);
    }

    public void removeTaskById(int id) {
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

    public void updateStatus(int id, TaskStatus status) {
        if (taskMap.containsKey(id)) {
            taskMap.get(id).setStatus(status);
        }
        if (subtaskMap.containsKey(id)) {
            Subtask subtask = subtaskMap.get(id);
            subtask.setStatus(status);
            Epic epic = epicMap.get(subtask.getEpicId());
            epic.updateStatus(subtaskMap);
        }
    }
}
