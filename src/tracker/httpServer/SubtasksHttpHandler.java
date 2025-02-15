package tracker.httpServer;

import tracker.controllers.HistoryManager;
import tracker.controllers.TaskManager;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.model.TaskType;

import java.util.List;
import java.util.Optional;

public class SubtasksHttpHandler extends BaseHttpHandler {
    SubtasksHttpHandler(TaskManager taskManager, HistoryManager historyManager) {
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
        return Optional.ofNullable(taskManager.getTaskById(id))
                .filter(task -> task.getType() == TaskType.SUBTASK);
    }

    @Override
    protected void postTask(Task task) {
        taskManager.createSubtask((Subtask) task);
    }

    @Override
    protected Class<Subtask> getTaskClass() {
        return Subtask.class;
    }
}