package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T parseJson(String body, Class<T> clazz) {
        JsonParser parser =  new JsonParser();
        JsonElement element = parser.parse(body);
        JsonObject jsonObject = element.getAsJsonObject();

        T result = null;
        try {
            result = objectMapper.readValue(jsonObject.toString(), clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
