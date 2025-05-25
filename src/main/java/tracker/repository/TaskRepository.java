package tracker.repository;

import tracker.dto.TaskDto;
import tracker.model.TaskStatus;
import java.util.List;

/**
 * Репозиторий для работы с задачами
 */
public interface TaskRepository extends Repository<TaskDto, Long> {
    /**
     * Находит все задачи с определенным статусом
     * @param status статус задач
     * @return список задач с указанным статусом
     */
    List<TaskDto> findByStatus(TaskStatus status);
} 