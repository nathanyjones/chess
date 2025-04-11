package websocket;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public interface NotificationHandler {
    void notify(NotificationMessage notification);

    void handleError(ErrorMessage serverMessage);

    void loadGame(LoadGameMessage serverMessage);
}
