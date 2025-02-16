package tracker.serverHandlers;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tracker.serverHandlers.BaseHttpHandler.GSON;

public class TasksHttpHandlerTest extends BaseHttpHandlerTest {

    @BeforeEach
    void setUp() {
        super.setUp();
        baseUrl = BASE_URL + "/tasks";
    }

    @Test
    void shouldGetAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

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
            assertEquals(2, tasks.size(), "Список задач неполный");
            assertEquals(task1.getName(), tasks.get(0).getName(), "Неверное имя задачи 1");
            assertEquals(task2.getName(), tasks.get(1).getName(), "Неверное имя задачи 2");

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldGetSpecificTask() {
        taskManager.createTask(task1);

        url = URI.create(baseUrl + "/" + task1.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

            Task task = GSON.fromJson(response.body(), Task.class);

            assertNotNull(task);
            assertEquals(task1.getName(), task.getName(), "Неверное имя задачи");

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldSendTaskNotFound() {
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
    void shouldPostTask() {
        String jsonTask = GSON.toJson(task1);
        url = URI.create(baseUrl);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }

        tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(1, tasks.size());
        assertEquals(task1.getName(), tasks.get(0).getName(), "Неверное имя задачи");
    }

    @Test
    void shouldPreventAddingIntersectingTask() {
        taskManager.createTask(task1);
        String jsonTask = GSON.toJson(task1);

        url = URI.create(baseUrl);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
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
    void shouldUpdateTask() {
        taskManager.createTask(task1);
        String jsonTask = GSON.toJson(task2);

        url = URI.create(baseUrl + "/" + task1.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }

        tasks = taskManager.getAllTasks();

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals(task2.getName(), tasks.get(0).getName(), "Неверное имя обновленной задачи");
    }

    @Test
    void shouldDeleteTask() {
        taskManager.createTask(task1);

        url = URI.create(baseUrl + "/" + task1.getId());
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