package tracker.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DbSchemaInitializer {
    public static void initSchema() {
        try (Connection conn = DatabaseInitializer.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = readSchemaSql();
            for (String query : sql.split(";")) {
                String trimmed = query.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка инициализации схемы БД", e);
        }
    }

    private static String readSchemaSql() {
        try (InputStream is = DbSchemaInitializer.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (is == null) throw new RuntimeException("Файл schema.sql не найден");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка чтения schema.sql", e);
        }
    }
} 