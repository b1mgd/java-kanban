package tracker.server;

import com.google.gson.*;
import tracker.model.Epic;

import java.lang.reflect.Type;

public class EpicTypeAdapter implements JsonDeserializer<Epic> {
    @Override
    public Epic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        
        // Создаем эпик через конструктор
        Epic epic = new Epic(name, description);
        
        // Устанавливаем дополнительные поля, если они есть
        if (jsonObject.has("id")) {
            epic.setId(jsonObject.get("id").getAsInt());
        }
        if (jsonObject.has("status")) {
            epic.setStatus(context.deserialize(jsonObject.get("status"), tracker.model.TaskStatus.class));
        }
        
        return epic;
    }
} 