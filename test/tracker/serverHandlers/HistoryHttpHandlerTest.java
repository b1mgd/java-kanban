package tracker.serverHandlers;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tracker.serverHandlers.BaseHttpHandler.GSON;

public class HistoryHttpHandlerTest extends BaseHttpHandlerTest {

    @BeforeEach
    void setUp() {
        super.setUp();
        baseUrl = BASE_URL + "/history";
        taskManager.createEpic(epic1);

        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId(),
                15, "08.01.2025 12:00");
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId(),
                45, "08.01.2025 12:15");
    }

    @Test
    void shouldGetAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.getTaskById(subtask2.getId());
        taskManager.getTaskById(subtask1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());

        url = URI.create(baseUrl);
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

            tasks = GSON.fromJson(response.body(), new TypeToken<List<Task>>() {
            }.getType());

            assertNotNull(tasks);
            assertEquals(4, tasks.size(), "История задач неполная");
            assertEquals(subtask2.getName(), tasks.get(0).getName(), "Неверное имя подзадачи 1");
            assertEquals(task1.getName(), tasks.get(tasks.size() - 1).getName(), "Неверное имя задачи 1");

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}