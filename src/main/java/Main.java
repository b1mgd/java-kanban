import tracker.controllers.*;
import tracker.model.*;
import tracker.serverHandlers.HttpServer;

public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        taskManager.setHistoryManager(historyManager);
        historyManager.setTaskManager(taskManager);

        // Initialize and start HTTP server
        HttpServer server = new HttpServer(taskManager);
        server.start(PORT);
        
        System.out.println("HTTP Server started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  GET    /tasks - Get all tasks");
        System.out.println("  GET    /tasks/{id} - Get task by ID");
        System.out.println("  POST   /tasks - Create new task");
        System.out.println("  PUT    /tasks/{id} - Update task");
        System.out.println("  DELETE /tasks/{id} - Delete task");
        System.out.println("  DELETE /tasks - Delete all tasks");
        System.out.println();
        System.out.println("  GET    /epics - Get all epics");
        System.out.println("  GET    /epics/{id} - Get epic by ID");
        System.out.println("  POST   /epics - Create new epic");
        System.out.println("  PUT    /epics/{id} - Update epic");
        System.out.println("  DELETE /epics/{id} - Delete epic");
        System.out.println("  DELETE /epics - Delete all epics");
        System.out.println();
        System.out.println("  GET    /subtasks - Get all subtasks");
        System.out.println("  GET    /subtasks/{id} - Get subtask by ID");
        System.out.println("  POST   /subtasks - Create new subtask");
        System.out.println("  PUT    /subtasks/{id} - Update subtask");
        System.out.println("  DELETE /subtasks/{id} - Delete subtask");
        System.out.println("  DELETE /subtasks - Delete all subtasks");
        System.out.println();
        System.out.println("  GET    /epics/{id}/subtasks - Get all subtasks for epic");
        System.out.println("  GET    /history - Get task history");
    }
}