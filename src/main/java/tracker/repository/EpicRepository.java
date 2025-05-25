package tracker.repository;

import tracker.dto.EpicDto;
import tracker.model.TaskStatus;
import java.util.List;

/**
 * Репозиторий для работы с эпиками
 */
public interface EpicRepository extends Repository<EpicDto, Long> {
    /**
     * Находит все эпики с определенным статусом
     * @param status статус эпиков
     * @return список эпиков с указанным статусом
     */
    List<EpicDto> findByStatus(TaskStatus status);

    /**
     * Получает список идентификаторов подзадач для эпика
     * @param epicId идентификатор эпика
     * @return список идентификаторов подзадач
     */
    List<Long> getSubtaskIds(Long epicId);

    /**
     * Добавляет подзадачу к эпику
     * @param epicId идентификатор эпика
     * @param subtaskId идентификатор подзадачи
     * @return true если подзадача была добавлена, false если эпик не найден
     */
    boolean addSubtask(Long epicId, Long subtaskId);

    /**
     * Удаляет подзадачу из эпика
     * @param epicId идентификатор эпика
     * @param subtaskId идентификатор подзадачи
     * @return true если подзадача была удалена, false если эпик не найден
     */
    boolean removeSubtask(Long epicId, Long subtaskId);
} 