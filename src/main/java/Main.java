import tracker.controllers.DatabaseTaskManager;
import tracker.dao.DatabaseInitializer;
import tracker.server.HttpTaskServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            // Инициализация базы данных
            DatabaseInitializer.initialize();
            
            // Создание менеджера задач
            DatabaseTaskManager taskManager = new DatabaseTaskManager();
            
            // Запуск HTTP сервера
            HttpTaskServer server = new HttpTaskServer(taskManager);
            server.start();
            
            logger.info("Server started at http://localhost:8080");
            
            // Добавление обработчика для корректного завершения работы
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down server...");
                server.stop();
                DatabaseInitializer.close();
                logger.info("Server stopped");
            }));
            
        } catch (Exception e) {
            logger.error("Failed to start server", e);
            System.exit(1);
        }
    }
}