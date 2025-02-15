package tracker.httpServer;

import tracker.controllers.HistoryManager;
import tracker.controllers.TaskManager;
import tracker.model.Task;
import tracker.model.TaskType;

import java.util.List;
import java.util.Optional;

public class TasksHttpHandler extends BaseHttpHandler {

    TasksHttpHandler(TaskManager taskManager, HistoryManager historyManager) {
        super(taskManager, historyManager);
    }

    @Override
    protected boolean isRootEndpoint() {
        return requestPathArray.length == 2 && requestPath.endsWith("tasks");
    }

    @Override
    protected boolean isIdEndpoint() {
        return requestPathArray.length == 3 && requestPathArray[1].equals("tasks");
    }

    @Override
    protected List<Task> getTasks() {
        return taskManager.getAllTasks();
    }

    @Override
    protected Optional<Task> getTaskById(int id) {
        return Optional.ofNullable(taskManager.getTaskById(id))
                .filter(task -> task.getType() == TaskType.TASK);
    }

    @Override
    protected void postTask(Task task) {
        taskManager.createTask(task);
    }

    @Override
    protected Class<Task> getTaskClass() {
        return Task.class;
    }
}