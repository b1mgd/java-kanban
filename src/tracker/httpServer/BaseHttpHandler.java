package tracker.httpServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.controllers.HistoryManager;
import tracker.controllers.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    protected final TaskManager taskManager;
    protected final HistoryManager historyManager;

    protected String requestMethod;
    protected String requestPath;
    protected String[] requestPathArray;
    protected String responseText;
    protected int responseCode;

    protected BaseHttpHandler(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        requestMethod = httpExchange.getRequestMethod();
        requestPath = httpExchange.getRequestURI().getPath();
        requestPathArray = requestPath.split("/");

        switch (requestMethod) {
            case "GET":
                handleGet();
                break;

            case "POST":
                handlePost(httpExchange);
                break;

            case "DELETE":
                handleDelete();
                break;

            default:
                setEndpointNotFound();
        }
        sendResponse(httpExchange, responseText, responseCode);
    }

    protected abstract boolean isRootEndpoint();

    protected abstract boolean isIdEndpoint();

    protected abstract List<? extends Task> getTasks();

    protected abstract Optional<Task> getTaskById(int id);

    protected abstract void postTask(Task task);

    protected abstract Class<? extends Task> getTaskClass();

    protected void updateTask(Task updatedTask, int id) {
        taskManager.updateTask(updatedTask, id);
    }

    protected void deleteTaskById(int id) {
        taskManager.deleteTaskById(id);
    }

    protected void handleGet() {
        if (isRootEndpoint()) {
            responseText = convertToJson(getTasks());
            responseCode = HttpURLConnection.HTTP_OK;

        } else if (isIdEndpoint()) {

            try {
                int id = parseId(requestPathArray);
                Optional<Task> optionalTask = getTaskById(id);

                if (optionalTask.isEmpty()) {
                    throw new IllegalArgumentException("Задача с указанным ID не найдена");
                }

                Task task = optionalTask.get();
                responseText = convertToJson(task);
                responseCode = HttpURLConnection.HTTP_OK;

            } catch (IllegalArgumentException e) {
                responseText = e.getMessage();
                responseCode = HttpURLConnection.HTTP_NOT_FOUND;
            }

        } else {
            setEndpointNotFound();
        }
    }

    protected void handlePost(HttpExchange httpExchange) {
        Optional<Task> optionalTask = getFromJson(httpExchange);

        if (optionalTask.isEmpty()) {
            responseText = "Некорректный формат JSON";
            responseCode = HttpURLConnection.HTTP_BAD_REQUEST;
            return;
        }

        Task task = optionalTask.get();

        if (isRootEndpoint()) {

            try {
                postTask(task);
                responseText = "Задача добавлена";
                responseCode = HttpURLConnection.HTTP_CREATED;

            } catch (IllegalArgumentException e) {
                responseText = e.getMessage();
                responseCode = HttpURLConnection.HTTP_NOT_ACCEPTABLE;
            }

        } else if (isIdEndpoint()) {

            try {
                int id = parseId(requestPathArray);

                if (getTaskById(id).isEmpty()) {
                    throw new IllegalArgumentException("Задача c указанным ID не найдена");
                }

                updateTask(task, id);
                responseText = "Задача обновлена";
                responseCode = HttpURLConnection.HTTP_CREATED;

            } catch (IllegalArgumentException e) {
                responseText = e.getMessage();
                responseCode = HttpURLConnection.HTTP_NOT_ACCEPTABLE;
            }

        } else {
            setEndpointNotFound();
        }
    }

    protected void handleDelete() {
        if (isIdEndpoint()) {
            try {
                int id = parseId(requestPathArray);

                if (getTaskById(id).isEmpty()) {
                    throw new IllegalArgumentException("Задача c указанным ID не найдена");
                }

                deleteTaskById(id);
                responseText = "Задача удалена";
                responseCode = HttpURLConnection.HTTP_OK;

            } catch (IllegalArgumentException e) {
                responseText = e.getMessage();
                responseCode = HttpURLConnection.HTTP_BAD_REQUEST;
            }

        } else {
            setEndpointNotFound();
        }
    }

    protected void sendResponse(HttpExchange httpExchange, String responseText, int code) throws IOException {
        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            byte[] response = responseText.getBytes(DEFAULT_CHARSET);
            httpExchange.sendResponseHeaders(code, response.length);
            os.write(response);
        }
    }

    protected int parseId(String[] requestPathArray) {
        if (requestPathArray.length < 3) {
            throw new NumberFormatException("В пути не указан ID");
        }
        return Integer.parseInt(requestPathArray[2]);
    }

    protected void setEndpointNotFound() {
        responseText = "Эндпоинт не найден";
        responseCode = HttpURLConnection.HTTP_NOT_FOUND;
    }

    protected String convertToJson(Object o) {
        return GSON.toJson(o);
    }

    protected Optional<Task> getFromJson(HttpExchange httpExchange) {
        try (InputStreamReader reader = new InputStreamReader(httpExchange.getRequestBody(), DEFAULT_CHARSET)) {
            return Optional.ofNullable(GSON.fromJson(reader, getTaskClass()));
        } catch (JsonSyntaxException | IOException e) {
            return Optional.empty();
        }
    }
}