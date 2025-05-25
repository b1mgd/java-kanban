package tracker.server;

import com.google.gson.*;
import tracker.model.Task;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.format(formatter));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        String dateStr = json.getAsString();
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            // Сначала пробуем распарсить как ZonedDateTime
            ZonedDateTime zdt = ZonedDateTime.parse(dateStr);
            return zdt.toLocalDateTime();
        } catch (Exception e) {
            // Если не получилось, пробуем как LocalDateTime
            return LocalDateTime.parse(dateStr, formatter);
        }
    }
} 