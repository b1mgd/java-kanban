package tracker.server;

import com.google.gson.*;
import tracker.model.Subtask;
import tracker.model.TaskStatus;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubtaskTypeAdapter implements JsonDeserializer<Subtask> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public Subtask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        int epicId = jsonObject.get("epicId").getAsInt();
        long duration = jsonObject.has("duration") ? jsonObject.get("duration").getAsLong() : 0L;
        
        String startTimeStr = null;
        if (jsonObject.has("startTime") && !jsonObject.get("startTime").isJsonNull()) {
            startTimeStr = jsonObject.get("startTime").getAsString();
            // Преобразуем ISO формат в формат dd.MM.yyyy HH:mm
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, DateTimeFormatter.ISO_DATE_TIME);
            startTimeStr = startTime.format(DATE_TIME_FORMATTER);
        }

        Subtask subtask = new Subtask(name, description, epicId, duration, startTimeStr);

        // Устанавливаем дополнительные поля, если они есть
        if (jsonObject.has("status")) {
            subtask.setStatus(TaskStatus.valueOf(jsonObject.get("status").getAsString()));
        }
        if (jsonObject.has("id")) {
            subtask.setId(jsonObject.get("id").getAsInt());
        }

        return subtask;
    }
} 