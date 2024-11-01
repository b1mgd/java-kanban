import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subTaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTaskId() {
        return subTaskIds;
    }

    public void addSubtaskId(int id) {
        subTaskIds.add(id);
    }

    public void updateStatus(HashMap<Integer, Subtask> subtaskMap) {
        if (subTaskIds.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }
        boolean areSubtasksNew = true;
        boolean areSubtasksDone = true;
        for (Integer id : subTaskIds) {
            Subtask subtask = subtaskMap.get(id);
            if (subtask.getStatus() != TaskStatus.NEW) {
                areSubtasksNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                areSubtasksDone = false;
            }
        }
        if (areSubtasksNew) {
            setStatus(TaskStatus.NEW);
        } else if (areSubtasksDone) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "\nEpic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subTaskIds=" + getSubTaskId() +
                "}";
    }
}


