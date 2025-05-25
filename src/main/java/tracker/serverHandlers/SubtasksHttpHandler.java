package tracker.serverHandlers;

import tracker.controllers.HistoryManager;
import tracker.controllers.TaskManager;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.model.TaskType;

import java.util.List;
import java.util.Optional;
import java.net.HttpURLConnection;

public class SubtasksHttpHandler extends BaseHttpHandler {
    public SubtasksHttpHandler(TaskManager taskManager, HistoryManager historyManager) {
        super(taskManager, historyManager);
    }

    @Override
    protected boolean isRootEndpoint() {
        return requestPathArray.length == 2 && requestPath.endsWith("subtasks");
    }

    @Override
    protected boolean isIdEndpoint() {
        return requestPathArray.length == 3 && requestPathArray[1].equals("subtasks");
    }

    @Override
    protected List<Subtask> getTasks() {
        return taskManager.getAllSubtasks();
    }

    @Override
    protected Optional<Task> getTaskById(int id) {
        Task task = taskManager.getTaskById(id);
        if (task == null) {
            return Optional.empty();
        }
        if (task.getType() != TaskType.SUBTASK) {
            return Optional.empty();
        }
        return Optional.of(task);
    }

    @Override
    protected void postTask(Task task) {
        taskManager.createSubtask((Subtask) task);
    }

    @Override
    protected Class<Subtask> getTaskClass() {
        return Subtask.class;
    }

    @Override
    protected void handleGet() {
        if (isRootEndpoint()) {
            responseText = convertToJson(getTasks());
            responseCode = HttpURLConnection.HTTP_OK;
        } else if (isIdEndpoint()) {
            try {
                int id = parseId(requestPathArray);
                Optional<Task> optionalTask = getTaskById(id);

                if (optionalTask.isEmpty()) {
                    responseText = convertToJson(new ErrorResponse("Подзадача не найдена"));
                    responseCode = HttpURLConnection.HTTP_NOT_FOUND;
                    return;
                }

                Task task = optionalTask.get();
                responseText = convertToJson(task);
                responseCode = HttpURLConnection.HTTP_OK;

            } catch (IllegalArgumentException e) {
                responseText = convertToJson(new ErrorResponse(e.getMessage()));
                responseCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                responseText = convertToJson(new ErrorResponse("Внутренняя ошибка сервера"));
                responseCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        } else {
            setEndpointNotFound();
        }
    }

    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}