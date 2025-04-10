package websocket.messages;

import com.google.gson.Gson;

public class NotificationMessage extends ServerMessage  {
    private final String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

 