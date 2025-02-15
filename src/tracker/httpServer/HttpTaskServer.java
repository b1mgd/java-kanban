package tracker.httpServer;

import com.sun.net.httpserver.HttpServer;
import tracker.controllers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static TaskManager taskManager;
    private static HistoryManager historyManager;
    private static HttpServer httpServer;

    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
        setUpManagers();

        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

            httpServer.createContext("/tasks", new TasksHttpHandler(taskManager, historyManager));
            httpServer.createContext("/subtasks", new SubtasksHttpHandler(taskManager, historyManager));
            httpServer.createContext("/epics", new EpicsHttpHandler(taskManager, historyManager));
            httpServer.createContext("/history", new HistoryHttpHandler(taskManager, historyManager));
            httpServer.createContext("/prioritized", new PrioritizedHttpHandler(taskManager, historyManager));

            httpServer.start();
            System.out.println("Сервер запущен на порту " + PORT);

        } catch (IOException e) {
            System.out.println("Ошибка при запуске сервера" + e.getMessage());
        }
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public static HistoryManager getHistoryManager() {
        return historyManager;
    }

    public static void stopServer() {
        if (httpServer != null) {
            httpServer.stop(0);
            System.out.println("Остановка сервера");
        }
    }

    private static void setUpManagers() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();

        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);
    }
}