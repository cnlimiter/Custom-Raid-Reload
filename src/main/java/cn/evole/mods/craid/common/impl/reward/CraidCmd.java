package cn.evole.mods.craid.common.impl.reward;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @Name: CraidCmd
 * @Author: cnlimiter
 * @CreateTime: 2024/12/14 13:00
 * @Description: 
 *
**/
public record CraidCmd(String cmd, boolean global) implements JsonDeserializer<CraidCmd> {

    @Override
    public CraidCmd deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
        }

        return null;
    }
}
