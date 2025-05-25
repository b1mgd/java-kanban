package tracker.controllers;

import tracker.dao.DatabaseInitializer;
import tracker.model.Task;
import tracker.model.TaskType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHistoryManager implements HistoryManager {
    private static final String ADD_SQL = 
        "INSERT INTO history (task_id, task_type) VALUES (?, ?)";
    
    private static final String REMOVE_SQL = 
        "DELETE FROM history WHERE task_id = ?";
    
    private static final String GET_HISTORY_SQL = 
        "SELECT h.task_id, h.task_type, " +
        "CASE h.task_type " +
        "  WHEN 'TASK' THEN t.name " +
        "  WHEN 'EPIC' THEN e.name " +
        "  WHEN 'SUBTASK' THEN s.name " +
        "END as name, " +
        "CASE h.task_type " +
        "  WHEN 'TASK' THEN t.description " +
        "  WHEN 'EPIC' THEN e.description " +
        "  WHEN 'SUBTASK' THEN s.description " +
        "END as description, " +
        "CASE h.task_type " +
        "  WHEN 'TASK' THEN t.status " +
        "  WHEN 'EPIC' THEN e.status " +
        "  WHEN 'SUBTASK' THEN s.status " +
        "END as status, " +
        "CASE h.task_type " +
        "  WHEN 'TASK' THEN t.start_time " +
        "  WHEN 'EPIC' THEN e.start_time " +
        "  WHEN 'SUBTASK' THEN s.start_time " +
        "END as start_time, " +
        "CASE h.task_type " +
        "  WHEN 'TASK' THEN t.duration " +
        "  WHEN 'EPIC' THEN e.duration " +
        "  WHEN 'SUBTASK' THEN s.duration " +
        "END as duration, " +
        "CASE h.task_type " +
        "  WHEN 'SUBTASK' THEN s.epic_id " +
        "  ELSE NULL " +
        "END as epic_id " +
        "FROM history h " +
        "LEFT JOIN tasks t ON h.task_id = t.id AND h.task_type = 'TASK' " +
        "LEFT JOIN epics e ON h.task_id = e.id AND h.task_type = 'EPIC' " +
        "LEFT JOIN subtasks s ON h.task_id = s.id AND h.task_type = 'SUBTASK' " +
        "ORDER BY h.viewed_at DESC";

    private TaskManager taskManager;

    @Override
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void add(Task task) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ADD_SQL)) {
            
            stmt.setInt(1, task.getId());
            stmt.setString(2, task.getType().name());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении задачи в историю", e);
        }
    }

    @Override
    public void remove(int id) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(REMOVE_SQL)) {
            
            stmt.setInt(1, id);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении задачи из истории", e);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_HISTORY_SQL)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TaskType type = TaskType.valueOf(rs.getString("task_type"));
                    int id = rs.getInt("task_id");
                    
                    Task task = null;
                    switch (type) {
                        case TASK:
                            task = taskManager.getAllTasks().stream()
                                .filter(t -> t.getId() == id)
                                .findFirst()
                                .orElse(null);
                            break;
                        case EPIC:
                            task = taskManager.getAllEpics().stream()
                                .filter(e -> e.getId() == id)
                                .findFirst()
                                .orElse(null);
                            break;
                        case SUBTASK:
                            task = taskManager.getAllSubtasks().stream()
                                .filter(s -> s.getId() == id)
                                .findFirst()
                                .orElse(null);
                            break;
                    }
                    
                    if (task != null) {
                        history.add(task);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении истории", e);
        }
        return history;
    }
} 