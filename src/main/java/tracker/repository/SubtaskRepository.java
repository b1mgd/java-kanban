package tracker.repository;

import tracker.dto.SubtaskDto;
import tracker.model.TaskStatus;
import java.util.List;

/**
 * Репозиторий для работы с подзадачами
 */
public interface SubtaskRepository extends Repository<SubtaskDto, Long> {
    /**
     * Находит все подзадачи с определенным статусом
     * @param status статус подзадач
     * @return список подзадач с указанным статусом
     */
    List<SubtaskDto> findByStatus(TaskStatus status);

    /**
     * Находит все подзадачи для определенного эпика
     * @param epicId идентификатор эпика
     * @return список подзадач эпика
     */
    List<SubtaskDto> findByEpicId(Long epicId);
} 