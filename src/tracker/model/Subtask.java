package tracker.model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String writeToFile() {
        return getId() + "," + getType() + "," + getName() + "," + getStatus() +
                "," + getDescription() + "," + getEpicId();
    }

    @Override
    public String toString() {
        return "\nSubtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                "}";
    }
}