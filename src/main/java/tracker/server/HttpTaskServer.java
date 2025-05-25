package tracker.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import spark.Service;
import tracker.controllers.DatabaseTaskManager;
import tracker.dao.DatabaseInitializer;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;
import tracker.model.TaskType;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class HttpTaskServer {
    private final DatabaseTaskManager taskManager;
    private final Gson gson;
    private static final int PORT = 8080;

    public HttpTaskServer(DatabaseTaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(Epic.class, new EpicTypeAdapter())
            .registerTypeAdapter(Subtask.class, new SubtaskTypeAdapter())
            .create();
    }

    public void start() {
        port(PORT);
        staticFiles.location("/public");
        staticFiles.expireTime(600L);
        
        // Получение всех задач (HTML)
        get("/tasks", (req, res) -> {
            String accept = req.headers("Accept");
            if (accept != null && accept.contains("application/json")) {
                res.type("application/json");
                List<Task> tasks = taskManager.getAllTasks();
                return gson.toJson(tasks);
            }
            res.type("text/html");
            return getClass().getResourceAsStream("/public/templates/tasks.html");
        });

        // Получение всех эпиков (HTML)
        get("/epics", (req, res) -> {
            String accept = req.headers("Accept");
            if (accept != null && accept.contains("application/json")) {
                res.type("application/json");
                return gson.toJson(taskManager.getAllEpics());
            }
            res.type("text/html");
            return getClass().getResourceAsStream("/public/templates/epics.html");
        });

        // Получение списка эпиков для выбора при создании подзадачи
        get("/epics/list", (req, res) -> {
            res.type("application/json");
            try {
                List<Epic> epics = taskManager.getAllEpics();
                if (epics == null) {
                    epics = List.of(); // Возвращаем пустой список вместо null
                }
                return gson.toJson(epics);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("error", "Ошибка при получении списка эпиков: " + e.getMessage()));
            }
        });

        // Получение всех подзадач (HTML)
        get("/subtasks", (req, res) -> {
            String accept = req.headers("Accept");
            if (accept != null && accept.contains("application/json")) {
                res.type("application/json");
                return gson.toJson(taskManager.getAllSubtasks());
            }
            res.type("text/html");
            return getClass().getResourceAsStream("/public/templates/subtasks.html");
        });

        // Получение задачи по ID
        get("/tasks/:id", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                Task task = taskManager.getTaskById(id);
                if (task == null) {
                    res.status(404);
                    return gson.toJson(Map.of("error", "Задача не найдена"));
                }

                // Проверяем Accept-заголовок
                String accept = req.headers("Accept");
                if (accept != null && accept.contains("text/html")) {
                    res.type("text/html");
                    return getClass().getResourceAsStream("/public/templates/task-detail.html");
                } else {
                    res.type("application/json");
                    return gson.toJson(task);
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(Map.of("error", "Неверный формат ID"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Получение эпика по ID (HTML или JSON)
        get("/epics/:id", (req, res) -> {
            try {
                String accept = req.headers("Accept");
                int id = Integer.parseInt(req.params(":id"));
                Task epic = taskManager.getTaskById(id);
                
                if (epic == null || !(epic instanceof Epic)) {
                    res.status(404);
                    if (accept != null && accept.contains("application/json")) {
                        res.type("application/json");
                        return gson.toJson(Map.of("error", "Эпик не найден"));
                    }
                    return "Эпик не найден";
                }

                if (accept != null && accept.contains("application/json")) {
                    res.type("application/json");
                    return gson.toJson(epic);
                }
                
                res.type("text/html");
                return getClass().getResourceAsStream("/public/templates/epic-detail.html");
            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(Map.of("error", "Неверный формат ID"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Получение подзадачи по ID (HTML или JSON)
        get("/subtasks/:id", (req, res) -> {
            String accept = req.headers("Accept");
            int id = Integer.parseInt(req.params(":id"));
            Task subtask = taskManager.getTaskById(id);
            
            if (subtask == null || !(subtask instanceof Subtask)) {
                res.status(404);
                if (accept != null && accept.contains("application/json")) {
                    res.type("application/json");
                    return gson.toJson(Map.of("error", "Подзадача не найдена"));
                }
                return "Подзадача не найдена";
            }

            if (accept != null && accept.contains("application/json")) {
                res.type("application/json");
                return gson.toJson(subtask);
            }
            
            res.type("text/html");
            return getClass().getResourceAsStream("/public/templates/subtask-detail.html");
        });

        // Получение подзадач эпика
        get("/epics/:id/subtasks", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params(":id"));
            List<Subtask> subtasks = taskManager.getSubtasksOfEpic(id);
            return gson.toJson(subtasks);
        });

        // Создание задачи с проверкой пересечений
        post("/tasks", (req, res) -> {
            res.type("application/json");
            try {
                Task task = gson.fromJson(req.body(), Task.class);
                
                // Проверка пересечений
                if (task.getStartTime() != null && task.getDuration() > 0) {
                    List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                    for (Task existingTask : prioritizedTasks) {
                        if (existingTask.getStartTime() != null && existingTask.getDuration() > 0) {
                            if (task.getStartTime().isBefore(existingTask.getEndTime()) &&
                                task.getEndTime().isAfter(existingTask.getStartTime())) {
                                res.status(400);
                                return gson.toJson(Map.of("error", 
                                    "Задача пересекается по времени с существующей задачей: " + existingTask.getName()));
                            }
                        }
                    }
                }

                taskManager.createTask(task);
                res.status(201);
                return gson.toJson(task);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Создание эпика
        post("/epics", (req, res) -> {
            res.type("application/json");
            try {
                Epic epic = gson.fromJson(req.body(), Epic.class);
                taskManager.createEpic(epic);
                res.status(201);
                return gson.toJson(epic);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Создание подзадачи
        post("/subtasks", (req, res) -> {
            res.type("application/json");
            try {
                Subtask subtask = gson.fromJson(req.body(), Subtask.class);
                
                // Проверяем существование эпика
                Task epic = taskManager.getTaskById(subtask.getEpicId());
                if (epic == null || !(epic instanceof Epic)) {
                    res.status(400);
                    return gson.toJson(Map.of("error", "Указанный эпик не существует"));
                }

                // Проверка пересечений
                if (subtask.getStartTime() != null && subtask.getDuration() > 0) {
                    List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                    for (Task existingTask : prioritizedTasks) {
                        if (existingTask.getStartTime() != null && existingTask.getDuration() > 0) {
                            if (subtask.getStartTime().isBefore(existingTask.getEndTime()) &&
                                subtask.getEndTime().isAfter(existingTask.getStartTime())) {
                                res.status(400);
                                return gson.toJson(Map.of("error", 
                                    "Подзадача пересекается по времени с существующей задачей: " + existingTask.getName()));
                            }
                        }
                    }
                }

                taskManager.createSubtask(subtask);
                res.status(201);
                return gson.toJson(subtask);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Обновление задачи
        put("/tasks/:id", (req, res) -> {
            res.type("application/json");
            try {
                int id = Integer.parseInt(req.params(":id"));
                Task task = gson.fromJson(req.body(), Task.class);
                taskManager.updateTask(task, id);
                return gson.toJson(task);
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Обновление эпика
        put("/epics/:id", (req, res) -> {
            res.type("application/json");
            try {
                int id = Integer.parseInt(req.params(":id"));
                Task task = taskManager.getTaskById(id);
                if (task == null || !(task instanceof Epic)) {
                    res.status(404);
                    return gson.toJson(Map.of("error", "Эпик не найден"));
                }
                Epic epic = gson.fromJson(req.body(), Epic.class);
                epic.setId(id);
                taskManager.updateTask(epic, id);
                return gson.toJson(epic);
            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(Map.of("error", "Неверный формат ID"));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Удаление задачи
        delete("/tasks/:id", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                taskManager.deleteTaskById(id);
                res.status(200);
                return "";
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("error", e.getMessage()));
            }
        });

        // Удаление всех задач
        delete("/tasks", (req, res) -> {
            taskManager.deleteTasks();
            res.status(200);
            return "";
        });

        // Удаление всех эпиков
        delete("/epics", (req, res) -> {
            taskManager.deleteEpics();
            res.status(200);
            return "";
        });

        // Удаление всех подзадач
        delete("/subtasks", (req, res) -> {
            taskManager.deleteSubtasks();
            res.status(200);
            return "";
        });

        // Получение истории просмотров (HTML или JSON)
        get("/history", (req, res) -> {
            String accept = req.headers("Accept");
            if (accept != null && accept.contains("application/json")) {
                res.type("application/json");
                return gson.toJson(taskManager.getHistory());
            }
            res.type("text/html");
            return getClass().getResourceAsStream("/public/templates/history.html");
        });

        // Получение приоритетных задач (HTML или JSON)
        get("/prioritized", (req, res) -> {
            String accept = req.headers("Accept");
            if (accept != null && accept.contains("application/json")) {
                res.type("application/json");
                return gson.toJson(taskManager.getPrioritizedTasks());
            }
            res.type("text/html");
            return getClass().getResourceAsStream("/public/templates/prioritized.html").readAllBytes();
        });
    }

    public void stop() {
        spark.Spark.stop();
        DatabaseInitializer.close();
    }
} 