package tracker.repository.impl;

import tracker.dao.TaskDaoInterface;
import tracker.dto.EpicDto;
import tracker.model.Epic;
import tracker.model.TaskStatus;
import tracker.repository.EpicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

/**
 * Реализация репозитория для работы с эпиками
 */
public class EpicRepositoryImpl implements EpicRepository {
    private static final Logger logger = LoggerFactory.getLogger(EpicRepositoryImpl.class);
    private final TaskDaoInterface<Epic> epicDao;

    public EpicRepositoryImpl(TaskDaoInterface<Epic> epicDao) {
        this.epicDao = epicDao;
    }

    @Override
    public EpicDto save(EpicDto epicDto) {
        logger.info("Saving epic DTO: {}", epicDto);
        Epic epic = convertToEntity(epicDto);
        logger.info("Converted to entity: {}", epic);
        epicDao.create(epic);
        logger.info("Created epic in DAO with ID: {}", epic.getId());
        EpicDto savedDto = convertToDto(epic);
        logger.info("Converted back to DTO: {}", savedDto);
        return savedDto;
    }

    @Override
    public EpicDto update(EpicDto epicDto) {
        Epic epic = convertToEntity(epicDto);
        epicDao.update(epic);
        return epicDto;
    }

    @Override
    public Optional<EpicDto> findById(Long id) {
        logger.info("Finding epic by ID: {}", id);
        Epic epic = epicDao.getById(id.intValue());
        logger.info("Found epic in DAO: {}", epic);
        Optional<EpicDto> dto = Optional.ofNullable(epic)
                .map(this::convertToDto);
        logger.info("Converted to DTO: {}", dto.orElse(null));
        return dto;
    }

    @Override
    public List<EpicDto> findAll() {
        return epicDao.getAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EpicDto> findByStatus(TaskStatus status) {
        return epicDao.getAll().stream()
                .filter(epic -> epic.getStatus() == status)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            epicDao.delete(id.intValue());
            return true;
        } catch (Exception e) {
            logger.error("Error deleting epic with id: " + id, e);
            return false;
        }
    }

    @Override
    public void deleteAll() {
        epicDao.deleteAll();
    }

    @Override
    public List<Long> getSubtaskIds(Long epicId) {
        Epic epic = epicDao.getById(epicId.intValue());
        if (epic == null) {
            return new ArrayList<>();
        }
        return epic.getSubTaskId().stream()
                .map(Integer::longValue)
                .collect(Collectors.toList());
    }

    @Override
    public boolean addSubtask(Long epicId, Long subtaskId) {
        Epic epic = epicDao.getById(epicId.intValue());
        if (epic == null) {
            return false;
        }
        epic.addSubtaskId(subtaskId.intValue());
        epicDao.update(epic);
        return true;
    }

    @Override
    public boolean removeSubtask(Long epicId, Long subtaskId) {
        Epic epic = epicDao.getById(epicId.intValue());
        if (epic == null) {
            return false;
        }
        // В текущей реализации Epic нет метода removeSubtaskId,
        // поэтому создаем новый Set без удаляемого ID
        Set<Integer> newSubtaskIds = new HashSet<>(epic.getSubTaskId());
        newSubtaskIds.remove(subtaskId.intValue());
        // Создаем новый эпик с обновленным списком подзадач
        Epic updatedEpic = new Epic(epic.getName(), epic.getDescription());
        updatedEpic.setId(epic.getId());
        updatedEpic.setStatus(epic.getStatus());
        updatedEpic.setStartTime(epic.getStartTime());
        updatedEpic.setDuration(epic.getDuration());
        newSubtaskIds.forEach(updatedEpic::addSubtaskId);
        epicDao.update(updatedEpic);
        return true;
    }

    private EpicDto convertToDto(Epic epic) {
        if (epic == null) {
            return null;
        }
        EpicDto dto = new EpicDto();
        dto.setId((long) epic.getId());
        dto.setName(epic.getName());
        dto.setDescription(epic.getDescription());
        dto.setStatus(epic.getStatus());
        dto.setStartTime(epic.getStartTime());
        dto.setDuration(epic.getDuration());
        dto.setEndTime(epic.getEndTime());
        dto.setSubtaskIds(epic.getSubTaskId().stream()
                .map(Integer::longValue)
                .collect(Collectors.toList()));
        return dto;
    }

    private Epic convertToEntity(EpicDto dto) {
        if (dto == null) {
            return null;
        }
        Epic epic = new Epic(dto.getName(), dto.getDescription());
        if (dto.getId() != null) {
            epic.setId(dto.getId().intValue());
        }
        if (dto.getStatus() != null) {
            epic.setStatus(dto.getStatus());
        }
        if (dto.getStartTime() != null) {
            epic.setStartTime(dto.getStartTime());
        }
        if (dto.getDuration() != null) {
            epic.setDuration(dto.getDuration());
        }
        if (dto.getSubtaskIds() != null) {
            dto.getSubtaskIds().forEach(subtaskId -> 
                epic.addSubtaskId(subtaskId.intValue()));
        }
        return epic;
    }
} 