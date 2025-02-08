package tracker.model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId, long durationInMinutes, String startTime) {
        super(name, description, durationInMinutes, startTime);
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
        return id + "," + getType() + "," + name + "," + status +
                "," + description + "," + duration.toMinutes() + ","
                + (startTime == null ? "null" : startTime.format(DATE_TIME_FORMATTER)) + ","
                + (endTime == null ? "null" : endTime.format(DATE_TIME_FORMATTER)) + ","
                + epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration.toMinutes() +
                ", startTime=" + (startTime == null ? "null" : startTime.format(DATE_TIME_FORMATTER)) +
                ", endTime=" + (endTime == null ? "null" : endTime.format(DATE_TIME_FORMATTER)) +
                ", epicId=" + epicId +
                "}";
    }
}