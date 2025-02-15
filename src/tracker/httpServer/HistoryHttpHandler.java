package tracker.httpServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.controllers.HistoryManager;
import tracker.controllers.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

import static tracker.httpServer.BaseHttpHandler.GSON;

public class HistoryHttpHandler implements HttpHandler {
    TaskManager taskManager;
    HistoryManager historyManager;
    String requestMethod;
    String requestPath;
    String[] requestPathArray;
    String responseText;
    int responseCode;

    HistoryHttpHandler(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        findPath(httpExchange);
        boolean isHistoryEndpoint = requestMethod.equals("GET")
                && requestPathArray.length == 2
                && requestPathArray[1].equals("history");

        if (isHistoryEndpoint) {
            responseText = convertToJson(getHistory());
            responseCode = HttpURLConnection.HTTP_OK;
        } else {
            setEndpointNotFound();
        }

        sendResponse(httpExchange, responseText, responseCode);
    }

    protected void findPath(HttpExchange httpExchange) {
        requestMethod = httpExchange.getRequestMethod();
        requestPath = httpExchange.getRequestURI().getPath();
        requestPathArray = requestPath.split("/");
    }

    protected void sendResponse(HttpExchange httpExchange, String responseText, int responseCode) throws IOException {
        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            byte[] response = responseText.getBytes();
            httpExchange.sendResponseHeaders(responseCode, response.length);
            os.write(response);
        }
    }

    protected String convertToJson(Object o) {
        return GSON.toJson(o);
    }

    protected void setEndpointNotFound() {
        responseText = "Эндпоинт не найден";
        responseCode = HttpURLConnection.HTTP_NOT_FOUND;
    }

    private List<Task> getHistory() {
        return taskManager.getHistory();
    }
}