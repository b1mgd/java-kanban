package tracker.controllers;

import tracker.controllers.exceptions.*;
import tracker.model.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final int MAX_LINE_LENGTH = 9;
    private final Path backUpFile;

    private FileBackedTaskManager(Path path) {
        super();
        this.backUpFile = path;
    }

    private void save() {
        if (Files.notExists(backUpFile.getParent())) {
            throw new ManagerSaveException("Указанного файла не существует");
        }
        StringBuilder stringBuilder
                = new StringBuilder("id,type,name,status,description,duration,startTime,endTime,epic\n");
        saveTasksToStringBuilder(stringBuilder);
        Path tempFile;
        try {
            tempFile = Files.createTempFile("backUpTemp", ".csv");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile(), CHARSET))) {
                writer.write(stringBuilder.toString());
            }
            Files.move(tempFile, backUpFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка в процессе сохранения файла");
        }
    }

    private void saveTasksToStringBuilder(StringBuilder stringBuilder) {
        for (Task task : getAllTasks()) {
            stringBuilder.append(FileBackedTaskManager.toString(task)).append("\n");
        }
        for (Epic epic : getAllEpics()) {
            stringBuilder.append(FileBackedTaskManager.toString(epic)).append("\n");
        }
        for (Subtask subtask : getAllSubtasks()) {
            stringBuilder.append(FileBackedTaskManager.toString(subtask)).append("\n");
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        try {
            List<String> lines = Files.readAllLines(path, CHARSET);

            for (int i = 1; i < lines.size(); i++) {
                Task task = fromString(lines.get(i));
                TaskType type = TaskType.valueOf(lines.get(i).split(",")[1]);

                switch (type) {
                    case TASK:
                        manager.createTask(task);
                        break;

                    case EPIC:
                        manager.createEpic((Epic) task);
                        break;

                    case SUBTASK:
                        manager.createSubtask((Subtask) task);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка в процессе загрузки файла");
        }
        return manager;
    }

    private static String toString(Task task) {
        return task.writeToFile();
    }

    private static Task fromString(String value) {
        String[] taskInformation = value.split(",");
        int id = Integer.parseInt(taskInformation[0]);
        TaskType type = TaskType.valueOf(taskInformation[1]);
        String name = taskInformation[2];
        TaskStatus status = TaskStatus.valueOf(taskInformation[3]);
        String description = taskInformation[4];
        long duration = Long.parseLong(taskInformation[5]);
        String startTime = taskInformation[6];
        String endTime = taskInformation[7];

        int epicId;
        if (taskInformation.length == MAX_LINE_LENGTH) {
            epicId = Integer.parseInt(taskInformation[8]);
        } else {
            epicId = 0;
        }

        switch (type) {
            case TASK:
                Task task = new Task(name, description, duration, startTime);
                task.setId(id);
                task.setStatus(status);
                task.setDuration(duration);
                return task;

            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                epic.setDuration(duration);
                return epic;

            case SUBTASK:
                Subtask subtask =
                        new Subtask(name, description, epicId, duration, startTime);
                subtask.setId(id);
                subtask.setStatus(status);
                subtask.setDuration(duration);
                return subtask;

            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task updatedTask, int id) {
        super.updateTask(updatedTask, id);
        save();
    }

    public static void main(String[] args) {
        HistoryManager historyManager;
        TaskManager taskManager;
        Task task1, task2;
        Epic epic;
        Subtask subtask1, subtask2, subtask3;
        Path path;

        path = Paths.get("src/tracker/controllers/resources/backUpFile.csv");
        historyManager = Managers.getDefaultHistory();
        taskManager = FileBackedTaskManager.loadFromFile(path);

        historyManager.setTaskManager(taskManager);
        taskManager.setHistoryManager(historyManager);

        task1 = new Task("Task1", "Task_1_Description",
                25, "10.01.2018 23:45");
        taskManager.createTask(task1);

        task2 = new Task("Task2", "Task_2_Description",
                20, "10.01.2018 23:24");
        taskManager.createTask(task2);
        task2.setStatus(TaskStatus.DONE);


        epic = new Epic("Epic", "Epic_Description");
        taskManager.createEpic(epic);

        subtask1 = new Subtask("Subtask1", "Subtask_1_Description", epic.getId(),
                60, "25.04.2031 20:30");
        taskManager.createSubtask(subtask1);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);

        subtask2 = new Subtask("Subtask2", "Subtask_2_Description", epic.getId(),
                30, "25.04.2031 21:35");
        taskManager.createSubtask(subtask2);

        subtask3 = new Subtask("Subtask3", "Subtask_3_Description", epic.getId(),
                30, null);
        taskManager.createSubtask(subtask3);

        historyManager = Managers.getDefaultHistory();
        taskManager = FileBackedTaskManager
                .loadFromFile(path);

        historyManager.setTaskManager(taskManager);
        taskManager.setHistoryManager(historyManager);

        List<Task> tasks = taskManager.getAllTasks();
        tasks.addAll(taskManager.getAllEpics());
        tasks.addAll(taskManager.getAllSubtasks());

        System.out.print("Задачи после загрузки файла: \n");
        tasks.forEach(System.out::println);

        try (FileWriter writer = new FileWriter(path.toFile())) {
            System.out.println("\nФайл был очищен после отработки");
        } catch (IOException e) {
            System.out.println("Ошибка при очистке файла при отработке метода main()");
        }
    }
}