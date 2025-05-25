package tracker.dto;

import tracker.model.TaskStatus;
import java.time.LocalDateTime;

/**
 * DTO для представления задачи
 */
public class TaskDto extends BaseDto {
    private TaskStatus status;

    public TaskDto() {
    }

    public TaskDto(Long id, String name, String description, TaskStatus status,
                  LocalDateTime startTime, Long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
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

    @Override
    public String toString() {
        return "TaskDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }
} 