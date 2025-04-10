package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("We made it to the onMessage for the WS Handler.");
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT:
                System.out.println("The command is a connect command.");
                ConnectCommand connectCommand = new Gson().fromJson(message, ConnectCommand.class);
                connect(connectCommand.getAuthToken(), connectCommand.getUsername(),
                        connectCommand.getGameID(), connectCommand.getColor(), session);
                break;
            case MAKE_MOVE:
                MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(makeMoveCommand.getAuthToken(), makeMoveCommand.getMove());
                break;
            case LEAVE:
                leave(userGameCommand.getAuthToken());
                break;
        }
    }

    private void connect(String authToken, String username, int gameID,
                         String color, Session session) throws IOException {
        connections.add(authToken, username, gameID, session);
        var message = String.format("%s joined the game as ", username);
        if (color.isEmpty()) {
            message += "an observer.";
        } else if (color.equals("WHITE")) {
            message += "white.";
        } else if (color.equals("BLACK")) {
            message += "black.";
        }
        var notification = new NotificationMessage(message);
        connections.broadcast(authToken, notification, false);
    }

    private void makeMove(String authToken, ChessMove move) throws IOException {
        String username = connections.getUsername(authToken);
        char startColumn = (char) (move.getStartPosition().getColumn() - 1 + 'a');
        int startRow = move.getStartPosition().getRow();
        char endColumn = (char) (move.getEndPosition().getColumn() - 1 + 'a');
        int endRow = move.getStartPosition().getRow();

        String moveString = "from " + startColumn + startRow + " to " + endColumn + endRow;
        var message = String.format("%s moved " + moveString + ".", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(authToken, notification, false);
    }

    private void leave(String authToken) throws IOException {
        String username = connections.remove(authToken);
        var message = String.format("%s left the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcast(authToken, notification, false);
    }

}