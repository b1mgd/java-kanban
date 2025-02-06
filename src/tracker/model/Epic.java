package tracker.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {

    private final Set<Integer> subTaskIds;

    public Epic(String name, String description) {
        super(name, description, 0L, null);
        this.subTaskIds = new HashSet<>();
    }

    private void updateStartTime(Set<Task> taskSet) {
        this.startTime = taskSet.stream()
                .filter(task -> getSubTaskId().contains(task.getId()))
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    public Set<Integer> getSubTaskId() {
        return subTaskIds;
    }

    public void addSubtaskId(int id) {
        subTaskIds.add(id);
    }

    public void updateStatus(Map<Integer, Subtask> subtaskMap) {
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

    public void updateTime(Map<Integer, Subtask> subtaskMap, Set<Task> taskSet) {
        duration = Duration.ZERO;
        List<Long> subtasksDuration = subTaskIds.stream()
                .map(id -> subtaskMap.get(id).getDuration())
                .toList();
        for (Long subtaskDuration : subtasksDuration) {
            duration = duration.plusMinutes(subtaskDuration);
        }
        updateStartTime(taskSet);
        updateEndTime();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String writeToFile() {
        return id + "," + getType() + "," + name + "," + status + "," +
                description + "," + duration.toMinutes() + ","
                + (startTime == null ? "null" : startTime.format(DATE_TIME_FORMATTER)) + ","
                + (endTime == null ? "null" : endTime.format(DATE_TIME_FORMATTER)) + ",";
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", duration=" + duration.toMinutes() +
                ", startTime=" + (startTime == null ? "null" : startTime.format(DATE_TIME_FORMATTER)) +
                ", endTime=" + (endTime == null ? "null" : endTime.format(DATE_TIME_FORMATTER)) +
                ", subTaskIds=" + subTaskIds +
                "}";
    }
}