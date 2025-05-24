package tracker.controllers;

import com.sun.net.httpserver.HttpServer;
import tracker.serverHandlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager taskManager;
    private final HistoryManager historyManager;
    private HttpServer httpServer;

    public HttpTaskServer() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();

        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);
    }

    public static void main(String[] args) {
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.startServer();
    }

    public void startServer() {
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

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public void stopServer() {
        if (httpServer != null) {
            httpServer.stop(0);
            System.out.println("Остановка сервера");
        }
    }
}