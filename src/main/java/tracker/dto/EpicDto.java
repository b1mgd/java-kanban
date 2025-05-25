package tracker.dto;

import tracker.model.TaskStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO для представления эпика
 */
public class EpicDto extends BaseDto {
    private TaskStatus status;
    private List<Long> subtaskIds;

    public EpicDto() {
        this.subtaskIds = new ArrayList<>();
    }

    public EpicDto(Long id, String name, String description, TaskStatus status,
                  LocalDateTime startTime, Long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.subtaskIds = new ArrayList<>();
        if (startTime != null && duration != null) {
            this.endTime = startTime.plusMinutes(duration);
        }
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public List<Long> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Long> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void addSubtaskId(Long subtaskId) {
        if (subtaskIds == null) {
            subtaskIds = new ArrayList<>();
        }
        subtaskIds.add(subtaskId);
    }

    @Override
    public String toString() {
        return "EpicDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
} 