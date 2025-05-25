package tracker.repository;

import java.util.List;
import java.util.Optional;

/**
 * Базовый интерфейс репозитория с основными операциями CRUD
 * @param <T> тип сущности
 * @param <ID> тип идентификатора
 */
public interface Repository<T, ID> {
    /**
     * Сохраняет сущность
     * @param entity сущность для сохранения
     * @return сохраненная сущность
     */
    T save(T entity);

    /**
     * Обновляет существующую сущность
     * @param entity сущность для обновления
     * @return обновленная сущность
     */
    T update(T entity);

    /**
     * Находит сущность по идентификатору
     * @param id идентификатор сущности
     * @return Optional с найденной сущностью или пустой Optional
     */
    Optional<T> findById(ID id);

    /**
     * Находит все сущности
     * @return список всех сущностей
     */
    List<T> findAll();

    /**
     * Удаляет сущность по идентификатору
     * @param id идентификатор сущности для удаления
     * @return true если сущность была удалена, false если сущность не найдена
     */
    boolean deleteById(ID id);

    /**
     * Удаляет все сущности
     */
    void deleteAll();
} 