package tracker.dao;

import tracker.model.Subtask;
import tracker.model.TaskStatus;
import tracker.model.TaskType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SubtaskDao implements TaskDaoInterface<Subtask> {
    private static final String CREATE_SQL = 
        "INSERT INTO subtasks (name, description, status, start_time, duration, epic_id) " +
        "VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String GET_BY_ID_SQL = 
        "SELECT * FROM subtasks WHERE id = ?";
    
    private static final String GET_ALL_SQL = 
        "SELECT * FROM subtasks";
    
    private static final String GET_BY_EPIC_SQL = 
        "SELECT * FROM subtasks WHERE epic_id = ?";
    
    private static final String UPDATE_SQL = 
        "UPDATE subtasks SET name = ?, description = ?, status = ?, " +
        "start_time = ?, duration = ?, epic_id = ? WHERE id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM subtasks WHERE id = ?";
    
    private static final String DELETE_ALL_SQL = 
        "DELETE FROM subtasks";

    @Override
    public void create(Subtask subtask) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, subtask.getName());
            stmt.setString(2, subtask.getDescription());
            stmt.setString(3, subtask.getStatus().name());
            stmt.setTimestamp(4, subtask.getStartTime() != null ? 
                Timestamp.valueOf(subtask.getStartTime()) : null);
            stmt.setLong(5, subtask.getDuration());
            stmt.setInt(6, subtask.getEpicId());
            
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

    @Override
    public Subtask getById(int id) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BY_ID_SQL)) {
            
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

    @Override
    public List<Subtask> getAll() {
        List<Subtask> subtasks = new ArrayList<>();
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_SQL)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subtasks.add(mapRowToSubtask(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка подзадач", e);
        }
        return subtasks;
    }

    public List<Subtask> getByEpicId(int epicId) {
        List<Subtask> subtasks = new ArrayList<>();
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BY_EPIC_SQL)) {
            
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

    @Override
    public void update(Subtask subtask) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            
            stmt.setString(1, subtask.getName());
            stmt.setString(2, subtask.getDescription());
            stmt.setString(3, subtask.getStatus().name());
            stmt.setTimestamp(4, subtask.getStartTime() != null ? 
                Timestamp.valueOf(subtask.getStartTime()) : null);
            stmt.setLong(5, subtask.getDuration());
            stmt.setInt(6, subtask.getEpicId());
            stmt.setInt(7, subtask.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении подзадачи", e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            
            stmt.setInt(1, id);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении подзадачи", e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_ALL_SQL)) {
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении всех подзадач", e);
        }
    }

    private Subtask mapRowToSubtask(ResultSet rs) throws SQLException {
        Subtask subtask = new Subtask(
            rs.getString("name"),
            rs.getString("description"),
            rs.getInt("epic_id"),
            rs.getLong("duration"),
            rs.getTimestamp("start_time") != null ? 
                rs.getTimestamp("start_time").toLocalDateTime().format(Subtask.DATE_TIME_FORMATTER) : null
        );
        subtask.setId(rs.getInt("id"));
        subtask.setStatus(TaskStatus.valueOf(rs.getString("status")));
        return subtask;
    }
} 