package tracker.dao;

import tracker.model.Task;
import java.util.List;

public interface TaskDaoInterface<T extends Task> {
    /**
     * Создает новую задачу в базе данных
     * @param task задача для создания
     */
    void create(T task);

    /**
     * Получает задачу по идентификатору
     * @param id идентификатор задачи
     * @return найденная задача или null, если задача не найдена
     */
    T getById(int id);

    /**
     * Получает список всех задач
     * @return список всех задач
     */
    List<T> getAll();

    /**
     * Обновляет существующую задачу
     * @param task задача для обновления
     */
    void update(T task);

    /**
     * Удаляет задачу по идентификатору
     * @param id идентификатор задачи для удаления
     */
    void delete(int id);

    /**
     * Удаляет все задачи
     */
    void deleteAll();
} 