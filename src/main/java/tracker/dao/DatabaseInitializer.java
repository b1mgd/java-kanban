package tracker.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static HikariDataSource dataSource;

    public static void initialize() {
        try {
            Properties props = loadProperties();
            HikariConfig config = new HikariConfig();
            
            config.setJdbcUrl(props.getProperty("db.url").trim());
            config.setUsername(props.getProperty("db.user").trim());
            config.setPassword(props.getProperty("db.password").trim());
            config.setDriverClassName(props.getProperty("db.driver").trim());
            
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.poolSize").trim()));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.connectionTimeout").trim()));
            config.setIdleTimeout(Long.parseLong(props.getProperty("db.idleTimeout").trim()));
            config.setMaxLifetime(Long.parseLong(props.getProperty("db.maxLifetime").trim()));
            
            dataSource = new HikariDataSource(config);
            
            // Инициализация схемы базы данных
            initializeSchema();
            
            logger.info("Database initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = DatabaseInitializer.class.getClassLoader()
                .getResourceAsStream("h2.properties")) {
            if (input == null) {
                throw new IOException("Unable to find h2.properties");
            }
            props.load(input);
        }
        return props;
    }

    private static void initializeSchema() throws SQLException {
        String createTasksTable = """
            CREATE TABLE IF NOT EXISTS tasks (
                id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                description TEXT,
                status VARCHAR(20) NOT NULL,
                start_time TIMESTAMP,
                duration BIGINT,
                type VARCHAR(20) NOT NULL
            )
        """;

        String createEpicsTable = """
            CREATE TABLE IF NOT EXISTS epics (
                id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                description TEXT,
                status VARCHAR(20) NOT NULL,
                start_time TIMESTAMP,
                duration BIGINT
            )
        """;

        String createSubtasksTable = """
            CREATE TABLE IF NOT EXISTS subtasks (
                id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                description TEXT,
                status VARCHAR(20) NOT NULL,
                start_time TIMESTAMP,
                duration BIGINT,
                epic_id INT NOT NULL,
                FOREIGN KEY (epic_id) REFERENCES epics(id) ON DELETE CASCADE
            )
        """;

        String createHistoryTable = """
            CREATE TABLE IF NOT EXISTS history (
                id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                task_id INT NOT NULL,
                task_type VARCHAR(20) NOT NULL,
                viewed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String createIndexes = """
            CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);
            CREATE INDEX IF NOT EXISTS idx_tasks_type ON tasks(type);
            CREATE INDEX IF NOT EXISTS idx_tasks_start_time ON tasks(start_time);
            CREATE INDEX IF NOT EXISTS idx_epics_status ON epics(status);
            CREATE INDEX IF NOT EXISTS idx_epics_start_time ON epics(start_time);
            CREATE INDEX IF NOT EXISTS idx_subtasks_epic_id ON subtasks(epic_id);
            CREATE INDEX IF NOT EXISTS idx_subtasks_status ON subtasks(status);
            CREATE INDEX IF NOT EXISTS idx_subtasks_start_time ON subtasks(start_time);
            CREATE INDEX IF NOT EXISTS idx_history_task_id ON history(task_id);
            CREATE INDEX IF NOT EXISTS idx_history_viewed_at ON history(viewed_at);
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createTasksTable);
            stmt.execute(createEpicsTable);
            stmt.execute(createSubtasksTable);
            stmt.execute(createHistoryTable);
            stmt.execute(createIndexes);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database not initialized");
        }
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
} 