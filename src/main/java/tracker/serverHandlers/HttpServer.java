package tracker.serverHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import tracker.controllers.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

public class HttpServer {
    private final TaskManager taskManager;
    private Javalin server;

    public HttpServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void start(int port) {
        // Configure ObjectMapper with JavaTimeModule for LocalDateTime support
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
        
        server = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper, true));
        }).start(port);

        // Tasks
        server.get("/tasks", ctx -> ctx.json(taskManager.getAllTasks()));
        server.get("/tasks/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Task task = taskManager.getTaskById(id);
            if (task != null && task.getType().name().equals("TASK")) {
                ctx.json(task);
            } else {
                ctx.status(404).result("Task not found");
            }
        });
        server.post("/tasks", ctx -> {
            Task task = ctx.bodyAsClass(Task.class);
            taskManager.createTask(task);
            ctx.status(201).json(task);
        });
        server.put("/tasks/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Task updatedTask = ctx.bodyAsClass(Task.class);
            taskManager.updateTask(updatedTask, id);
            ctx.status(200).json(updatedTask);
        });
        server.delete("/tasks/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            taskManager.deleteTaskById(id);
            ctx.status(204);
        });
        server.delete("/tasks", ctx -> {
            taskManager.deleteTasks();
            ctx.status(204);
        });

        // Epics
        server.get("/epics", ctx -> ctx.json(taskManager.getAllEpics()));
        server.get("/epics/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Task task = taskManager.getTaskById(id);
            if (task != null && task.getType().name().equals("EPIC")) {
                ctx.json(task);
            } else {
                ctx.status(404).result("Epic not found");
            }
        });
        server.post("/epics", ctx -> {
            Epic epic = ctx.bodyAsClass(Epic.class);
            taskManager.createEpic(epic);
            ctx.status(201).json(epic);
        });
        server.put("/epics/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Epic updatedEpic = ctx.bodyAsClass(Epic.class);
            taskManager.updateTask(updatedEpic, id);
            ctx.status(200).json(updatedEpic);
        });
        server.delete("/epics/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            taskManager.deleteTaskById(id);
            ctx.status(204);
        });
        server.delete("/epics", ctx -> {
            taskManager.deleteEpics();
            ctx.status(204);
        });
        // Subtasks of Epic
        server.get("/epics/{id}/subtasks", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            ctx.json(taskManager.getSubtasksOfEpic(id));
        });

        // Subtasks
        server.get("/subtasks", ctx -> ctx.json(taskManager.getAllSubtasks()));
        server.get("/subtasks/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Task task = taskManager.getTaskById(id);
            if (task != null && task.getType().name().equals("SUBTASK")) {
                ctx.json(task);
            } else {
                ctx.status(404).result("Subtask not found");
            }
        });
        server.post("/subtasks", ctx -> {
            Subtask subtask = ctx.bodyAsClass(Subtask.class);
            taskManager.createSubtask(subtask);
            ctx.status(201).json(subtask);
        });
        server.put("/subtasks/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Subtask updatedSubtask = ctx.bodyAsClass(Subtask.class);
            taskManager.updateTask(updatedSubtask, id);
            ctx.status(200).json(updatedSubtask);
        });
        server.delete("/subtasks/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            taskManager.deleteTaskById(id);
            ctx.status(204);
        });
        server.delete("/subtasks", ctx -> {
            taskManager.deleteSubtasks();
            ctx.status(204);
        });

        // History
        server.get("/history", ctx -> ctx.json(taskManager.getHistory()));
    }

    public void stop() {
        if (server != null) {
            server.stop();
        }
    }
} 