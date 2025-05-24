package tracker.dao;

import tracker.model.Task;
import tracker.model.TaskStatus;
import tracker.model.TaskType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {
    public void create(Task task) {
        String sql = "INSERT INTO tasks (name, description, status, duration, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, task.getName());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus().name());
            stmt.setLong(4, task.getDuration());
            stmt.setTimestamp(5, task.getStartTime() == null ? null : Timestamp.valueOf(task.getStartTime()));
            stmt.setTimestamp(6, task.getEndTime() == null ? null : Timestamp.valueOf(task.getEndTime()));
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

    public Task getById(int id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
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

    public List<Task> getAll() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                tasks.add(mapRowToTask(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка задач", e);
        }
        return tasks;
    }

    public void update(Task task) {
        String sql = "UPDATE tasks SET name=?, description=?, status=?, duration=?, start_time=?, end_time=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, task.getName());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus().name());
            stmt.setLong(4, task.getDuration());
            stmt.setTimestamp(5, task.getStartTime() == null ? null : Timestamp.valueOf(task.getStartTime()));
            stmt.setTimestamp(6, task.getEndTime() == null ? null : Timestamp.valueOf(task.getEndTime()));
            stmt.setInt(7, task.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении задачи", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении задачи", e);
        }
    }

    private Task mapRowToTask(ResultSet rs) throws SQLException {
        Task task = new Task(
                rs.getString("name"),
                rs.getString("description"),
                rs.getLong("duration"),
                rs.getTimestamp("start_time") == null ? null : rs.getTimestamp("start_time").toLocalDateTime().format(Task.DATE_TIME_FORMATTER)
        );
        task.setId(rs.getInt("id"));
        task.setStatus(TaskStatus.valueOf(rs.getString("status")));
        return task;
    }
} 