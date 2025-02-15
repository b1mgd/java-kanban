package tracker.httpServer;

import com.sun.net.httpserver.HttpExchange;
import tracker.controllers.HistoryManager;
import tracker.controllers.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class PrioritizedHttpHandler extends HistoryHttpHandler {
    PrioritizedHttpHandler(TaskManager taskManager, HistoryManager historyManager) {
        super(taskManager, historyManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        findPath(httpExchange);
        boolean isPrioritizedEndpoint = requestMethod.equals("GET")
                && requestPathArray.length == 2
                && requestPathArray[1].equals("prioritized");

        if (isPrioritizedEndpoint) {
            responseText = convertToJson(getPrioritizedTasks());
            responseCode = HttpURLConnection.HTTP_OK;
        } else {
            setEndpointNotFound();
        }
        sendResponse(httpExchange, responseText, responseCode);
    }

    private List<Task> getPrioritizedTasks() {
        return taskManager.getPrioritizedTasks();
    }
}