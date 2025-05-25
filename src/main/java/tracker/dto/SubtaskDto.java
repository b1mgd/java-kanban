package tracker.dto;

import tracker.model.TaskStatus;
import java.time.LocalDateTime;

/**
 * DTO для представления подзадачи
 */
public class SubtaskDto extends BaseDto {
    private TaskStatus status;
    private Long epicId;

    public SubtaskDto() {
    }

    public SubtaskDto(Long id, String name, String description, TaskStatus status,
                     LocalDateTime startTime, Long duration, Long epicId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.epicId = epicId;
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

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubtaskDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                ", epicId=" + epicId +
                '}';
    }
} 