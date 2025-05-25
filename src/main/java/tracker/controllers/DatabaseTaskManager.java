package tracker.controllers;

import tracker.dto.EpicDto;
import tracker.dto.SubtaskDto;
import tracker.dto.TaskDto;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.model.TaskStatus;
import tracker.repository.EpicRepository;
import tracker.repository.RepositoryFactory;
import tracker.repository.SubtaskRepository;
import tracker.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseTaskManager implements TaskManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseTaskManager.class);
    private final TaskRepository taskRepository;
    private final EpicRepository epicRepository;
    private final SubtaskRepository subtaskRepository;
    private HistoryManager historyManager;

    public DatabaseTaskManager() {
        RepositoryFactory repositoryFactory = RepositoryFactory.getInstance();
        this.taskRepository = repositoryFactory.getTaskRepository();
        this.epicRepository = repositoryFactory.getEpicRepository();
        this.subtaskRepository = repositoryFactory.getSubtaskRepository();
        this.historyManager = Managers.getDefaultHistory();
        this.historyManager.setTaskManager(this);
    }

    @Override
    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void createTask(Task task) {
        // Проверка пересечений
        if (task.getStartTime() != null && task.getDuration() > 0) {
            List<Task> prioritizedTasks = getPrioritizedTasks();
            for (Task existingTask : prioritizedTasks) {
                if (existingTask.getStartTime() != null && existingTask.getDuration() > 0) {
                    if (task.getStartTime().isBefore(existingTask.getEndTime()) &&
                        task.getEndTime().isAfter(existingTask.getStartTime())) {
                        throw new IllegalArgumentException(
                            "Задача пересекается по времени с существующей задачей: " + existingTask.getName());
                    }
                }
            }
        }
        TaskDto taskDto = convertToTaskDto(task);
        taskRepository.save(taskDto);
    }

    @Override
    public void createEpic(Epic epic) {
        logger.info("Creating epic: {}", epic);
        EpicDto epicDto = convertToEpicDto(epic);
        logger.info("Converted to DTO: {}", epicDto);
        EpicDto savedDto = epicRepository.save(epicDto);
        logger.info("Saved epic DTO: {}", savedDto);
        if (savedDto != null && savedDto.getId() != null) {
            epic.setId(savedDto.getId().intValue());
            logger.info("Updated epic ID to: {}", epic.getId());
        } else {
            logger.error("Failed to save epic: DTO is null or has no ID");
            throw new RuntimeException("Failed to save epic: no ID returned");
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        // Проверка существования эпика
        Task epic = getTaskById(subtask.getEpicId());
        if (epic == null || !(epic instanceof Epic)) {
            throw new IllegalArgumentException("Указанный эпик не существует");
        }

        // Проверка пересечений
        if (subtask.getStartTime() != null && subtask.getDuration() > 0) {
            List<Task> prioritizedTasks = getPrioritizedTasks();
            for (Task existingTask : prioritizedTasks) {
                if (existingTask.getStartTime() != null && existingTask.getDuration() > 0) {
                    if (subtask.getStartTime().isBefore(existingTask.getEndTime()) &&
                        subtask.getEndTime().isAfter(existingTask.getStartTime())) {
                        throw new IllegalArgumentException(
                            "Подзадача пересекается по времени с существующей задачей: " + existingTask.getName());
                    }
                }
            }
        }

        // Сохраняем подзадачу
        SubtaskDto subtaskDto = convertToSubtaskDto(subtask);
        subtaskRepository.save(subtaskDto);

        // Обновляем эпик
        Epic epicObj = (Epic) epic;
        epicObj.addSubtaskId(subtask.getId());
        updateEpicStatusAndTime(epicObj);
        EpicDto epicDto = convertToEpicDto(epicObj);
        epicRepository.update(epicDto);
    }

    @Override
    public List<Task> getAllTasks() {
        List<TaskDto> taskDtos = taskRepository.findAll();
        return taskDtos.stream()
                .map(this::convertToTask)
                .toList();
    }

    @Override
    public List<Epic> getAllEpics() {
        List<EpicDto> epicDtos = epicRepository.findAll();
        return epicDtos.stream()
                .map(this::convertToEpic)
                .toList();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<SubtaskDto> subtaskDtos = subtaskRepository.findAll();
        return subtaskDtos.stream()
                .map(this::convertToSubtask)
                .toList();
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        List<SubtaskDto> subtaskDtos = subtaskRepository.findByEpicId((long) epicId);
        return subtaskDtos.stream()
                .map(this::convertToSubtask)
                .toList();
    }

    @Override
    public void deleteTasks() {
        taskRepository.deleteAll();
    }

    @Override
    public void deleteEpics() {
        epicRepository.deleteAll();
    }

    @Override
    public void deleteSubtasks() {
        subtaskRepository.deleteAll();
    }

    @Override
    public Task getTaskById(int id) {
        logger.info("Getting task by ID: {}", id);
        // Сначала проверяем в репозитории задач
        Optional<TaskDto> taskDto = taskRepository.findById((long) id);
        if (taskDto.isPresent()) {
            logger.info("Found task in task repository: {}", taskDto.get());
            Task task = convertToTask(taskDto.get());
            historyManager.add(task);
            return task;
        }
        
        // Затем проверяем в репозитории эпиков
        Optional<EpicDto> epicDto = epicRepository.findById((long) id);
        if (epicDto.isPresent()) {
            logger.info("Found epic in epic repository: {}", epicDto.get());
            Epic epic = convertToEpic(epicDto.get());
            historyManager.add(epic);
            return epic;
        }
        
        // Наконец проверяем в репозитории подзадач
        Optional<SubtaskDto> subtaskDto = subtaskRepository.findById((long) id);
        if (subtaskDto.isPresent()) {
            logger.info("Found subtask in subtask repository: {}", subtaskDto.get());
            Subtask subtask = convertToSubtask(subtaskDto.get());
            historyManager.add(subtask);
            return subtask;
        }
        
        logger.warn("No task found with ID: {}", id);
        return null;
    }

    @Override
    public void deleteTaskById(int id) {
        taskRepository.deleteById((long) id);
        epicRepository.deleteById((long) id);
        subtaskRepository.deleteById((long) id);
    }

    @Override
    public void updateTask(Task updatedTask, int id) {
        // Проверка пересечений
        if (updatedTask.getStartTime() != null && updatedTask.getDuration() > 0) {
            List<Task> prioritizedTasks = getPrioritizedTasks();
            for (Task existingTask : prioritizedTasks) {
                if (existingTask.getId() != id && existingTask.getStartTime() != null && existingTask.getDuration() > 0) {
                    if (updatedTask.getStartTime().isBefore(existingTask.getEndTime()) &&
                        updatedTask.getEndTime().isAfter(existingTask.getStartTime())) {
                        throw new IllegalArgumentException(
                            "Задача пересекается по времени с существующей задачей: " + existingTask.getName());
                    }
                }
            }
        }

        if (updatedTask instanceof Epic) {
            EpicDto epicDto = convertToEpicDto((Epic) updatedTask);
            epicDto.setId((long) id);
            epicRepository.update(epicDto);
        } else if (updatedTask instanceof Subtask) {
            SubtaskDto subtaskDto = convertToSubtaskDto((Subtask) updatedTask);
            subtaskDto.setId((long) id);
            subtaskRepository.update(subtaskDto);

            // Обновляем эпик
            Task epic = getTaskById(((Subtask) updatedTask).getEpicId());
            if (epic instanceof Epic) {
                updateEpicStatusAndTime((Epic) epic);
                EpicDto epicDto = convertToEpicDto((Epic) epic);
                epicRepository.update(epicDto);
            }
        } else {
            TaskDto taskDto = convertToTaskDto(updatedTask);
            taskDto.setId((long) id);
            taskRepository.update(taskDto);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager != null ? historyManager.getHistory() : List.of();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> all = new ArrayList<>();
        all.addAll(getAllTasks());
        all.addAll(getAllEpics());
        all.addAll(getAllSubtasks());
        return all;
    }

    private TaskDto convertToTaskDto(Task task) {
        if (task == null) {
            return null;
        }
        return new TaskDto(
            (long) task.getId(),
            task.getName(),
            task.getDescription(),
            task.getStatus(),
            task.getStartTime(),
            task.getDuration()
        );
    }

    private EpicDto convertToEpicDto(Epic epic) {
        if (epic == null) {
            return null;
        }
        EpicDto dto = new EpicDto(
            (long) epic.getId(),
            epic.getName(),
            epic.getDescription(),
            epic.getStatus(),
            epic.getStartTime(),
            epic.getDuration()
        );
        epic.getSubTaskId().forEach(id -> dto.addSubtaskId((long) id));
        return dto;
    }

    private SubtaskDto convertToSubtaskDto(Subtask subtask) {
        if (subtask == null) {
            return null;
        }
        return new SubtaskDto(
            (long) subtask.getId(),
            subtask.getName(),
            subtask.getDescription(),
            subtask.getStatus(),
            subtask.getStartTime(),
            subtask.getDuration(),
            (long) subtask.getEpicId()
        );
    }

    private Task convertToTask(TaskDto dto) {
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

    private Epic convertToEpic(EpicDto dto) {
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
            dto.getSubtaskIds().forEach(id -> epic.addSubtaskId(id.intValue()));
        }
        return epic;
    }

    private Subtask convertToSubtask(SubtaskDto dto) {
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

    private void updateEpicStatusAndTime(Epic epic) {
        List<Subtask> subtasks = getSubtasksOfEpic(epic.getId());
        if (subtasks.isEmpty()) {
            epic.setDuration(0);
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        // Обновляем время
        LocalDateTime start = LocalDateTime.MAX;
        LocalDateTime end = LocalDateTime.MIN;
        long duration = 0;
        boolean allDone = true;
        boolean anyInProgress = false;

        for (Subtask subtask : subtasks) {
            // Обновляем статус
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                anyInProgress = true;
            }

            // Обновляем время
            LocalDateTime startTime = subtask.getStartTime();
            LocalDateTime endTime = subtask.getEndTime();
            if (startTime != null && startTime.isBefore(start)) {
                start = startTime;
            }
            if (endTime != null && endTime.isAfter(end)) {
                end = endTime;
            }
            duration += subtask.getDuration();
        }

        epic.setDuration(duration);
        epic.setStartTime(start);
        epic.setEndTime(end);

        // Устанавливаем статус
        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (anyInProgress) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }
} 