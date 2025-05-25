package tracker.dao;

import tracker.model.Epic;
import tracker.model.TaskStatus;
import tracker.model.TaskType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpicDao implements TaskDaoInterface<Epic> {
    private static final Logger logger = LoggerFactory.getLogger(EpicDao.class);
    private static final String CREATE_SQL = 
        "INSERT INTO epics (name, description, status, start_time, duration) " +
        "VALUES (?, ?, ?, ?, ?)";
    
    private static final String GET_BY_ID_SQL = 
        "SELECT * FROM epics WHERE id = ?";
    
    private static final String GET_ALL_SQL = 
        "SELECT * FROM epics";
    
    private static final String UPDATE_SQL = 
        "UPDATE epics SET name = ?, description = ?, status = ?, " +
        "start_time = ?, duration = ? WHERE id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM epics WHERE id = ?";
    
    private static final String DELETE_ALL_SQL = 
        "DELETE FROM epics";

    @Override
    public void create(Epic epic) {
        logger.info("Creating epic in database: {}", epic);
        if (epic.getStatus() == null) {
            epic.setStatus(TaskStatus.NEW);
            logger.info("Set default status NEW for epic");
        }
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, epic.getName());
            stmt.setString(2, epic.getDescription());
            stmt.setString(3, epic.getStatus().name());
            stmt.setTimestamp(4, epic.getStartTime() != null ? 
                Timestamp.valueOf(epic.getStartTime()) : null);
            stmt.setLong(5, epic.getDuration());
            
            logger.info("Executing SQL: {} with params: name={}, description={}, status={}, startTime={}, duration={}",
                CREATE_SQL, epic.getName(), epic.getDescription(), epic.getStatus(), epic.getStartTime(), epic.getDuration());
            
            int affectedRows = stmt.executeUpdate();
            logger.info("Affected rows: {}", affectedRows);
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    epic.setId(id);
                    logger.info("Set epic ID to: {}", id);
                } else {
                    logger.error("Failed to get generated key for epic");
                    throw new SQLException("Creating epic failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating epic in database", e);
            throw new RuntimeException("Ошибка при создании эпика", e);
        }
    }

    @Override
    public Epic getById(int id) {
        logger.info("Getting epic by ID from database: {}", id);
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BY_ID_SQL)) {
            
            stmt.setInt(1, id);
            logger.info("Executing SQL: {} with id={}", GET_BY_ID_SQL, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Epic epic = mapRowToEpic(rs);
                    logger.info("Found epic: {}", epic);
                    return epic;
                } else {
                    logger.warn("No epic found with ID: {}", id);
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting epic from database", e);
            throw new RuntimeException("Ошибка при получении эпика", e);
        }
    }

    @Override
    public List<Epic> getAll() {
        List<Epic> epics = new ArrayList<>();
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_SQL)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    epics.add(mapRowToEpic(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка эпиков", e);
        }
        return epics;
    }

    @Override
    public void update(Epic epic) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
            
            stmt.setString(1, epic.getName());
            stmt.setString(2, epic.getDescription());
            stmt.setString(3, epic.getStatus().name());
            stmt.setTimestamp(4, epic.getStartTime() != null ? 
                Timestamp.valueOf(epic.getStartTime()) : null);
            stmt.setLong(5, epic.getDuration());
            stmt.setInt(6, epic.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении эпика", e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
            
            stmt.setInt(1, id);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении эпика", e);
        }
    }

    @Override
    public void deleteAll() {
        try (Connection conn = DatabaseInitializer.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_ALL_SQL)) {
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении всех эпиков", e);
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
        return epic;
    }
} 