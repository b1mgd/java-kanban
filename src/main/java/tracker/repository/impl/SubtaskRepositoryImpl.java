package tracker.repository.impl;

import tracker.dao.TaskDaoInterface;
import tracker.dto.SubtaskDto;
import tracker.model.Subtask;
import tracker.model.TaskStatus;
import tracker.repository.SubtaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация репозитория для работы с подзадачами
 */
public class SubtaskRepositoryImpl implements SubtaskRepository {
    private static final Logger logger = LoggerFactory.getLogger(SubtaskRepositoryImpl.class);
    private final TaskDaoInterface<Subtask> subtaskDao;

    public SubtaskRepositoryImpl(TaskDaoInterface<Subtask> subtaskDao) {
        this.subtaskDao = subtaskDao;
    }

    @Override
    public SubtaskDto save(SubtaskDto subtaskDto) {
        Subtask subtask = convertToEntity(subtaskDto);
        subtaskDao.create(subtask);
        return convertToDto(subtask);
    }

    @Override
    public SubtaskDto update(SubtaskDto subtaskDto) {
        Subtask subtask = convertToEntity(subtaskDto);
        subtaskDao.update(subtask);
        return subtaskDto;
    }

    @Override
    public Optional<SubtaskDto> findById(Long id) {
        Subtask subtask = subtaskDao.getById(id.intValue());
        return Optional.ofNullable(subtask)
                .map(this::convertToDto);
    }

    @Override
    public List<SubtaskDto> findAll() {
        return subtaskDao.getAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubtaskDto> findByStatus(TaskStatus status) {
        return subtaskDao.getAll().stream()
                .filter(subtask -> subtask.getStatus() == status)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubtaskDto> findByEpicId(Long epicId) {
        return subtaskDao.getAll().stream()
                .filter(subtask -> subtask.getEpicId() == epicId.intValue())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            subtaskDao.delete(id.intValue());
            return true;
        } catch (Exception e) {
            logger.error("Error deleting subtask with id: " + id, e);
            return false;
        }
    }

    @Override
    public void deleteAll() {
        subtaskDao.deleteAll();
    }

    private SubtaskDto convertToDto(Subtask subtask) {
        if (subtask == null) {
            return null;
        }
        SubtaskDto dto = new SubtaskDto();
        dto.setId((long) subtask.getId());
        dto.setName(subtask.getName());
        dto.setDescription(subtask.getDescription());
        dto.setStatus(subtask.getStatus());
        dto.setStartTime(subtask.getStartTime());
        dto.setDuration(subtask.getDuration());
        dto.setEndTime(subtask.getEndTime());
        dto.setEpicId((long) subtask.getEpicId());
        return dto;
    }

    private Subtask convertToEntity(SubtaskDto dto) {
        if (dto == null) {
            return null;
        }
        String startTimeStr = dto.getStartTime() != null ? 
            dto.getStartTime().format(Subtask.DATE_TIME_FORMATTER) : "null";
        Subtask subtask = new Subtask(
            dto.getName(),
            dto.getDescription(),
            dto.getEpicId() != null ? dto.getEpicId().intValue() : 0,
            dto.getDuration() != null ? dto.getDuration() : 0,
            startTimeStr
        );
        if (dto.getId() != null) {
            subtask.setId(dto.getId().intValue());
        }
        if (dto.getStatus() != null) {
            subtask.setStatus(dto.getStatus());
        }
        return subtask;
    }
} 