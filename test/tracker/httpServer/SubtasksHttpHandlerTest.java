package tracker.httpServer;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Subtask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tracker.httpServer.BaseHttpHandler.GSON;

public class SubtasksHttpHandlerTest extends BaseHttpHandlerTest {

    @BeforeEach
    void setUp() {
        super.setUp();
        baseUrl = BASE_URL + "/subtasks";
        taskManager.createEpic(epic1);

        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId(),
                15, "08.01.2025 12:00");
        subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId(),
                45, "08.01.2025 12:15");
        subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getId(),
                30, "08.01.2025 12:00");
    }

    @Test
    void shouldGetAllSubtasks() {
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        url = URI.create(baseUrl);
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

            subtasks = GSON.fromJson(response.body(), new TypeToken<List<Subtask>>() {
            }.getType());

            assertNotNull(subtasks);
            assertEquals(2, subtasks.size(), "Список подзадач неполный");
            assertEquals(subtask1.getName(), subtasks.get(0).getName(), "Неверное имя подзадачи 1");
            assertEquals(subtask2.getName(), subtasks.get(1).getName(), "Неверное имя подзадачи 2");

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldGetSpecificSubtask() {
        taskManager.createSubtask(subtask1);

        url = URI.create(baseUrl + "/" + subtask1.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

            Subtask subtask = GSON.fromJson(response.body(), Subtask.class);

            assertNotNull(subtask);
            assertEquals(subtask1.getName(), subtask.getName(), "Неверное имя задачи");

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldSendSubtaskNotFound() {
        url = URI.create(baseUrl + "/10");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode());
            assertEquals(response.body(), "Задача с указанным ID не найдена");

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    void shouldPostSubtask() {
        String jsonSubtask = GSON.toJson(subtask1);
        url = URI.create(baseUrl);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }

        subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются");
        assertEquals(1, subtasks.size());
        assertEquals(subtask1.getName(), subtasks.get(0).getName(), "Неверное имя задачи");
    }

    @Test
    void shouldPreventAddingIntersectingTask() {
        taskManager.createSubtask(subtask1);
        String jsonSubtask = GSON.toJson(subtask3);

        url = URI.create(baseUrl);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_NOT_ACCEPTABLE, response.statusCode());
            assertEquals("Задача пересекается по времени с существующей", response.body());

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldUpdateSubtask() {
        taskManager.createSubtask(subtask1);
        String jsonSubtask = GSON.toJson(subtask2);

        url = URI.create(baseUrl + "/" + subtask1.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }

        subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks);
        assertEquals(1, subtasks.size());
        assertEquals(subtask2.getName(), subtasks.get(0).getName(), "Неверное имя обновленной подзадачи");
    }

    @Test
    void shouldDeleteSubtask() {
        taskManager.createSubtask(subtask1);

        url = URI.create(baseUrl + "/" + subtask1.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }

        tasks = taskManager.getAllTasks();

        assertTrue(tasks.isEmpty());
    }
}