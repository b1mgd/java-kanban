package tracker.serverHandlers;

import tracker.controllers.HistoryManager;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.model.TaskType;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;

public class EpicsHttpHandler extends BaseHttpHandler {

    public EpicsHttpHandler(TaskManager taskManager, HistoryManager historyManager) {
        super(taskManager, historyManager);
    }

    @Override
    protected boolean isRootEndpoint() {
        return requestPathArray.length == 2 && requestPath.endsWith("epics");
    }

    @Override
    protected boolean isIdEndpoint() {
        return requestPathArray.length == 3 && requestPathArray[1].equals("epics");
    }

    @Override
    protected List<Epic> getTasks() {
        return taskManager.getAllEpics();
    }

    @Override
    protected Optional<Task> getTaskById(int id) {
        return Optional.ofNullable(taskManager.getTaskById(id))
                .filter(task -> task.getType() == TaskType.EPIC);
    }

    @Override
    protected void postTask(Task task) {
        taskManager.createEpic((Epic) task);
    }

    @Override
    protected Class<Epic> getTaskClass() {
        return Epic.class;
    }

    @Override
    protected void handleGet() {
        if (isSubtaskEndpoint()) {
            try {
                int id = parseId(requestPathArray);

                if (getTaskById(id).isEmpty()) {
                    throw new IllegalArgumentException("Задача c указанным ID не найдена");
                }

                responseText = convertToJson(getSubtasksOfEpic(id));
                responseCode = HttpURLConnection.HTTP_OK;

            } catch (IllegalArgumentException e) {
                responseText = e.getMessage();
                responseCode = HttpURLConnection.HTTP_NOT_FOUND;
            }

        } else {
            super.handleGet();
        }
    }

    private boolean isSubtaskEndpoint() {
        return requestPathArray.length == 4
                && requestPathArray[1].equals("epics")
                && requestPathArray[3].equals("subtasks");
    }

    private List<Subtask> getSubtasksOfEpic(int id) {
        return taskManager.getSubtasksOfEpic(id);
    }
}