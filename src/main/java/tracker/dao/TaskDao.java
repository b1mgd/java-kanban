package tracker.dao;

import tracker.model.Task;
import tracker.model.TaskStatus;
import tracker.model.TaskType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDao implements TaskDaoInterface<Task> {
    private static final String CREATE_SQL = 
        "INSERT INTO tasks (name, description, status, start_time, duration, type) " +
        "VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String GET_BY_ID_SQL = 
        "SELECT * FROM tasks WHERE id = ? AND type = ?";
    
    private static final String GET_ALL_SQL = 
        "SELECT * FROM tasks WHERE type = ?";
    
    private static final String UPDATE_SQL = 
        "UPDATE tasks SET name = ?, description = ?, status = ?, " +
        "start_time = ?, duration = ? WHERE id = ? AND type = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM tasks WHERE id = ? AND type = ?";
    
    private static final String DELETE_ALL_SQL = 
        "DELETE FROM tasks WHERE type = ?";

    @Override
    public void create(Task task) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, task.getName());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus().name());
            stmt.setTimestamp(4, task.getStartTime() != null ? 
                Timestamp.valueOf(task.getStartTime()) : null);
            stmt.setLong(5, task.getDuration());
            stmt.setString(6, TaskType.TASK.name());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    task.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании задачи", e);
        }
    }

    @Override
    public Task getById(int id) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BY_ID_SQL)) {
            
            stmt.setInt(1, id);
            stmt.setString(2, TaskType.TASK.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTask(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении задачи", e);
        }
        return null;
    }

    @Override
    public List<Task> getAll() {
        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_SQL)) {
            
            stmt.setString(1, TaskType.TASK.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapRowToTask(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка задач", e);
        }
        return tasks;
    }

    @Override
    public void update(Task task) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            
            stmt.setString(1, task.getName());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus().name());
            stmt.setTimestamp(4, task.getStartTime() != null ? 
                Timestamp.valueOf(task.getStartTime()) : null);
            stmt.setLong(5, task.getDuration());
            stmt.setInt(6, task.getId());
            stmt.setString(7, TaskType.TASK.name());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении задачи", e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            
            stmt.setInt(1, id);
            stmt.setString(2, TaskType.TASK.name());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении задачи", e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_ALL_SQL)) {
            
            stmt.setString(1, TaskType.TASK.name());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении всех задач", e);
        }
    }

    private Task mapRowToTask(ResultSet rs) throws SQLException {
        Task task = new Task(
            rs.getString("name"),
            rs.getString("description"),
            rs.getLong("duration"),
            rs.getTimestamp("start_time") != null ? 
                rs.getTimestamp("start_time").toLocalDateTime().format(Task.DATE_TIME_FORMATTER) : null
        );
        task.setId(rs.getInt("id"));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        return task;
    }
} 