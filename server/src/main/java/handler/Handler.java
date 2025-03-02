package handler;

import com.google.gson.Gson;

public abstract class Handler {

    private final Gson serializer = new Gson();

    protected String toJSON(Object data) {
        return serializer.toJson(data);
    }

    protected <T> T fromJSON(String json, Class<T> objectType) {
        return serializer.fromJson(json, objectType);
    }
}
