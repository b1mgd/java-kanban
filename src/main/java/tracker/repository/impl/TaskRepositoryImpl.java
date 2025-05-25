package tracker.repository.impl;

import tracker.dao.TaskDaoInterface;
import tracker.dto.TaskDto;
import tracker.model.Task;
import tracker.model.TaskStatus;
import tracker.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация репозитория для работы с задачами
 */
public class TaskRepositoryImpl implements TaskRepository {
    private static final Logger logger = LoggerFactory.getLogger(TaskRepositoryImpl.class);
    private final TaskDaoInterface<Task> taskDao;

    public TaskRepositoryImpl(TaskDaoInterface<Task> taskDao) {
        this.taskDao = taskDao;
    }

    @Override
    public TaskDto save(TaskDto taskDto) {
        Task task = convertToEntity(taskDto);
        taskDao.create(task);
        return convertToDto(task);
    }

    @Override
    public TaskDto update(TaskDto taskDto) {
        Task task = convertToEntity(taskDto);
        taskDao.update(task);
        return taskDto;
    }

    @Override
    public Optional<TaskDto> findById(Long id) {
        Task task = taskDao.getById(id.intValue());
        return Optional.ofNullable(task)
                .map(this::convertToDto);
    }

    @Override
    public List<TaskDto> findAll() {
        return taskDao.getAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> findByStatus(TaskStatus status) {
        return taskDao.getAll().stream()
                .filter(task -> task.getStatus() == status)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            taskDao.delete(id.intValue());
            return true;
        } catch (Exception e) {
            logger.error("Error deleting task with id: " + id, e);
            return false;
        }
    }

    @Override
    public void deleteAll() {
        taskDao.deleteAll();
    }

    private TaskDto convertToDto(Task task) {
        if (task == null) {
            return null;
        }
        TaskDto dto = new TaskDto();
        dto.setId((long) task.getId());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setStartTime(task.getStartTime());
        dto.setDuration(task.getDuration());
        dto.setEndTime(task.getEndTime());
        return dto;
    }

    private Task convertToEntity(TaskDto dto) {
        if (dto == null) {
            return null;
        }
        String startTimeStr = dto.getStartTime() != null ? 
            dto.getStartTime().format(Task.DATE_TIME_FORMATTER) : "null";
        Task task = new Task(
            dto.getName(),
            dto.getDescription(),
            dto.getDuration() != null ? dto.getDuration() : 0,
            startTimeStr
        );
        if (dto.getId() != null) {
            task.setId(dto.getId().intValue());
        }
        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }
        return task;
    }
} 