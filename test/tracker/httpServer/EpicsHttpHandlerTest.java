package tracker.httpServer;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.model.Epic;
import tracker.model.Subtask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static tracker.httpServer.BaseHttpHandler.GSON;

public class EpicsHttpHandlerTest extends BaseHttpHandlerTest {

    @BeforeEach
    void setUp() {
        super.setUp();
        baseUrl = BASE_URL + "/epics";
    }

    @Test
    void shouldGetAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        url = URI.create(baseUrl);
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

            epics = GSON.fromJson(response.body(), new TypeToken<List<Epic>>() {
            }.getType());

            assertNotNull(epics);
            assertEquals(2, epics.size(), "Список эпиков неполный");
            assertEquals(epic1.getName(), epics.get(0).getName(), "Неверное имя эпика 1");
            assertEquals(epic2.getName(), epics.get(1).getName(), "Неверное имя эпика 2");

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldGetSpecificEpic() {
        taskManager.createEpic(epic1);

        url = URI.create(baseUrl + "/" + epic1.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

            Epic epic = GSON.fromJson(response.body(), Epic.class);

            assertNotNull(epic);
            assertEquals(epic1.getName(), epic.getName(), "Неверное имя эпика");

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldSendEpicNotFound() {
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
    void shouldGetEpicSubtasks() {
        taskManager.createEpic(epic1);
        subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId(),
                15, "08.01.2025 12:00");
        taskManager.createSubtask(subtask1);

        url = URI.create(baseUrl + "/" + epic1.getId() + "/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

            List<Subtask> subtasks = GSON.fromJson(response.body(), new TypeToken<List<Subtask>>() {
            }.getType());

            assertNotNull(subtasks);
            assertEquals(1, subtasks.size(), "Неверное количество подзадач");
            assertEquals(subtask1.getName(), subtasks.get(0).getName(), "Неверное имя подзадачи");

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void shouldSendEpicOfSubtaskNotFound() {
        url = URI.create(baseUrl + "/" + 10 + "/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.statusCode());
            assertEquals(response.body(), "Задача c указанным ID не найдена");

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());

        }
    }

    @Test
    void shouldPostEpic() {
        String jsonEpic = GSON.toJson(epic1);
        url = URI.create(baseUrl);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }

        epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size());
        assertEquals(epic1.getName(), epics.get(0).getName(), "Неверное имя эпика");
    }

    @Test
    void shouldUpdateEpic() {
        taskManager.createEpic(epic1);
        String jsonEpic = GSON.toJson(epic2);

        url = URI.create(baseUrl + "/" + epic1.getId());
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();

        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_CREATED, response.statusCode());

        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }

        epics = taskManager.getAllEpics();

        assertNotNull(epics);
        assertEquals(1, epics.size());
        assertEquals(epic2.getName(), epics.get(0).getName(), "Неверное имя обновленного эпика");
    }

    @Test
    void shouldDeleteEpic() {
        taskManager.createEpic(epic1);

        url = URI.create(baseUrl + "/" + epic1.getId());
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

        epics = taskManager.getAllEpics();

        assertTrue(epics.isEmpty());
    }
}