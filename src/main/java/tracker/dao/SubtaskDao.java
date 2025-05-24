package tracker.dao;

import tracker.model.Subtask;
import tracker.model.TaskStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubtaskDao {
    public void create(Subtask subtask) {
        String sql = "INSERT INTO subtasks (name, description, status, duration, start_time, end_time, epic_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, subtask.getName());
            stmt.setString(2, subtask.getDescription());
            stmt.setString(3, subtask.getStatus().name());
            stmt.setLong(4, subtask.getDuration());
            stmt.setTimestamp(5, subtask.getStartTime() == null ? null : Timestamp.valueOf(subtask.getStartTime()));
            stmt.setTimestamp(6, subtask.getEndTime() == null ? null : Timestamp.valueOf(subtask.getEndTime()));
            stmt.setInt(7, subtask.getEpicId());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    subtask.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании подзадачи", e);
        }
    }

    public Subtask getById(int id) {
        String sql = "SELECT * FROM subtasks WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSubtask(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении подзадачи", e);
        }
        return null;
    }

    public List<Subtask> getAll() {
        List<Subtask> subtasks = new ArrayList<>();
        String sql = "SELECT * FROM subtasks";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                subtasks.add(mapRowToSubtask(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка подзадач", e);
        }
        return subtasks;
    }

    public List<Subtask> getByEpicId(int epicId) {
        List<Subtask> subtasks = new ArrayList<>();
        String sql = "SELECT * FROM subtasks WHERE epic_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, epicId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subtasks.add(mapRowToSubtask(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении подзадач эпика", e);
        }
        return subtasks;
    }

    public void update(Subtask subtask) {
        String sql = "UPDATE subtasks SET name=?, description=?, status=?, duration=?, start_time=?, end_time=?, epic_id=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, subtask.getName());
            stmt.setString(2, subtask.getDescription());
            stmt.setString(3, subtask.getStatus().name());
            stmt.setLong(4, subtask.getDuration());
            stmt.setTimestamp(5, subtask.getStartTime() == null ? null : Timestamp.valueOf(subtask.getStartTime()));
            stmt.setTimestamp(6, subtask.getEndTime() == null ? null : Timestamp.valueOf(subtask.getEndTime()));
            stmt.setInt(7, subtask.getEpicId());
            stmt.setInt(8, subtask.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении подзадачи", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM subtasks WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении подзадачи", e);
        }
    }

    private Subtask mapRowToSubtask(ResultSet rs) throws SQLException {
        Subtask subtask = new Subtask(
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("epic_id"),
                rs.getLong("duration"),
                rs.getTimestamp("start_time") == null ? null : rs.getTimestamp("start_time").toLocalDateTime().format(tracker.model.Task.DATE_TIME_FORMATTER)
        );
        subtask.setId(rs.getInt("id"));
        subtask.setStatus(TaskStatus.valueOf(rs.getString("status")));
        return subtask;
    }
} 