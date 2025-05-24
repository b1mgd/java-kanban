package tracker.dao;

import tracker.model.Epic;
import tracker.model.TaskStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EpicDao {
    public void create(Epic epic) {
        String sql = "INSERT INTO epics (name, description, status, duration, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, epic.getName());
            stmt.setString(2, epic.getDescription());
            stmt.setString(3, epic.getStatus().name());
            stmt.setLong(4, epic.getDuration());
            stmt.setTimestamp(5, epic.getStartTime() == null ? null : Timestamp.valueOf(epic.getStartTime()));
            stmt.setTimestamp(6, epic.getEndTime() == null ? null : Timestamp.valueOf(epic.getEndTime()));
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    epic.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании эпика", e);
        }
    }

    public Epic getById(int id) {
        String sql = "SELECT * FROM epics WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToEpic(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении эпика", e);
        }
        return null;
    }

    public List<Epic> getAll() {
        List<Epic> epics = new ArrayList<>();
        String sql = "SELECT * FROM epics";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                epics.add(mapRowToEpic(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка эпиков", e);
        }
        return epics;
    }

    public void update(Epic epic) {
        String sql = "UPDATE epics SET name=?, description=?, status=?, duration=?, start_time=?, end_time=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, epic.getName());
            stmt.setString(2, epic.getDescription());
            stmt.setString(3, epic.getStatus().name());
            stmt.setLong(4, epic.getDuration());
            stmt.setTimestamp(5, epic.getStartTime() == null ? null : Timestamp.valueOf(epic.getStartTime()));
            stmt.setTimestamp(6, epic.getEndTime() == null ? null : Timestamp.valueOf(epic.getEndTime()));
            stmt.setInt(7, epic.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении эпика", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM epics WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении эпика", e);
        }
    }

    private Epic mapRowToEpic(ResultSet rs) throws SQLException {
        Epic epic = new Epic(
                rs.getString("name"),
                rs.getString("description")
        );
        epic.setId(rs.getInt("id"));
        epic.setStatus(TaskStatus.valueOf(rs.getString("status")));
        epic.setDuration(rs.getLong("duration"));
        if (rs.getTimestamp("start_time") != null) {
            epic.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        }
        if (rs.getTimestamp("end_time") != null) {
            epic.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        }
        return epic;
    }
} 