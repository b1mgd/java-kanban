package tracker.serverHandlers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import tracker.controllers.HistoryManager;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.controllers.HttpTaskServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public abstract class BaseHttpHandlerTest {
    protected static final String BASE_URL = "http://localhost:8080";
    protected static HttpTaskServer taskServer;
    protected HttpClient httpClient;
    protected TaskManager taskManager;
    protected HistoryManager historyManager;
    protected HttpRequest request;
    protected String baseUrl;
    protected URI url;
    protected HttpResponse<String> response;
    protected Task task1, task2;
    protected Epic epic1, epic2;
    protected Subtask subtask1, subtask2, subtask3;
    protected List<Task> tasks;
    protected List<Epic> epics;
    protected List<Subtask> subtasks;

    @BeforeAll
    static void startServer() {
        taskServer = new HttpTaskServer();
        taskServer.startServer();
    }

    @AfterAll
    static void stopServer() {
        if (taskServer != null) {
            taskServer.stopServer();
        }
    }

    @BeforeEach
    void setUp() {

        taskManager = taskServer.getTaskManager();
        historyManager = taskManager.getHistoryManager();
        httpClient = HttpClient.newHttpClient();

        task1 = new Task("Задача 1", "Описание задачи 1",
                15L, "10.01.2025 08:00");
        task2 = new Task("Задача 2", "Описание задачи 2",
                23L, null);

        epic1 = new Epic("Эпик 1", "Описание эпика 1");
        epic2 = new Epic("Эпик 2", "Описание эпика 2");
    }

    @AfterEach
    void clearManager() {
        taskManager.deleteEpics();
        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
    }
}