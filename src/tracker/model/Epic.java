package tracker.model;

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
        boolean areAllNew = true;
        boolean areAllDone = true;

        for (Integer id : subTaskIds) {
            Subtask subtask = subtaskMap.get(id);
            if (subtask == null) {
                continue;
            }
            switch (subtask.getStatus()) {
                case IN_PROGRESS:
                    setStatus(TaskStatus.IN_PROGRESS);
                    return;

                case NEW:
                    areAllDone = false;
                    break;

                case DONE:
                    areAllNew = false;
                    break;
            }
            setStatus(areAllNew ? TaskStatus.NEW : areAllDone ? TaskStatus.DONE : TaskStatus.IN_PROGRESS);
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