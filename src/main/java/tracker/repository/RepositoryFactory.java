package tracker.repository;

import tracker.dao.EpicDao;
import tracker.dao.SubtaskDao;
import tracker.dao.TaskDao;
import tracker.dao.TaskDaoInterface;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.repository.impl.EpicRepositoryImpl;
import tracker.repository.impl.SubtaskRepositoryImpl;
import tracker.repository.impl.TaskRepositoryImpl;

/**
 * Фабрика для создания и получения экземпляров репозиториев.
 * Реализует паттерн Singleton для обеспечения единственного экземпляра фабрики.
 */
public class RepositoryFactory {
    private static RepositoryFactory instance;
    private final TaskRepository taskRepository;
    private final EpicRepository epicRepository;
    private final SubtaskRepository subtaskRepository;

    private RepositoryFactory() {
        TaskDaoInterface<Task> taskDao = new TaskDao();
        TaskDaoInterface<Epic> epicDao = new EpicDao();
        TaskDaoInterface<Subtask> subtaskDao = new SubtaskDao();

        this.taskRepository = new TaskRepositoryImpl(taskDao);
        this.epicRepository = new EpicRepositoryImpl(epicDao);
        this.subtaskRepository = new SubtaskRepositoryImpl(subtaskDao);
    }

    /**
     * Получает единственный экземпляр фабрики репозиториев.
     *
     * @return экземпляр RepositoryFactory
     */
    public static synchronized RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new RepositoryFactory();
        }
        return instance;
    }

    /**
     * Получает репозиторий задач.
     *
     * @return экземпляр TaskRepository
     */
    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    /**
     * Получает репозиторий эпиков.
     *
     * @return экземпляр EpicRepository
     */
    public EpicRepository getEpicRepository() {
        return epicRepository;
    }

    /**
     * Получает репозиторий подзадач.
     *
     * @return экземпляр SubtaskRepository
     */
    public SubtaskRepository getSubtaskRepository() {
        return subtaskRepository;
    }
} 