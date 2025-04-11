package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ResponseException;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService;
    private final GameService gameService;

    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT:
                connect(userGameCommand.getAuthToken(), userGameCommand.getGameID(), session);
                break;
            case MAKE_MOVE:
                MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                makeMove(makeMoveCommand.getAuthToken(), makeMoveCommand.getMove());
                break;
            case LEAVE:
                leave(userGameCommand.getAuthToken(), userGameCommand.getGameID());
                break;
            case RESIGN:
                resign(userGameCommand.getAuthToken(), userGameCommand.getGameID());
                break;
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException {
        String username;
        String color;
        try {
            username = getUsername(authToken);
        } catch (IOException e) {
            sendErrorMessage(session, "Invalid AuthToken. Session may have expired.");
            return;
        }
        try {
            color = getColor(authToken, gameID);
        } catch (IOException e) {
            sendErrorMessage(session, "Invalid Game ID. Game may have ended.");
            return;
        }

        connections.add(authToken, username, gameID, session);
        var message = String.format("%s joined the game as ", username);
        if (color.isEmpty()) {
            message += "an observer.";
        } else if (color.equals("WHITE")) {
            message += "white.";
        } else {
            message += "black.";
        }
        var notification = new NotificationMessage(message);
        connections.broadcast(authToken, notification, false);

        try {
            GameData gameData = (GameData) gameService.getGame(authToken, gameID)[1];
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
            connections.sendIndividualMessage(authToken, loadGameMessage);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    private void sendErrorMessage(Session session, String message) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage(message);
        session.getRemote().sendString(new Gson().toJson(errorMessage));
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
        connections.broadcast(authToken, notification, true);
    }

    private void leave(String authToken, int gameID) throws IOException {
        boolean isPlayer;
        try {
            UserData userData = userService.getUser(authToken);
            String username = userData.username();
            GameData gameData = (GameData) gameService.getGame(authToken, gameID)[1];
            isPlayer = gameData.blackUsername().equals(username) || gameData.whiteUsername().equals(username);
        } catch (Exception e) {
            throw new IOException();
        }
        String username = connections.remove(authToken);
        var message = String.format("%s left the game", username);
        if (isPlayer) {
            var notification = new NotificationMessage(message);
            connections.broadcast(authToken, notification, false);
        }
    }

    private void resign(String authToken, int gameID) throws IOException {
        String username;
        String color;
        try {
            username = getUsername(authToken);
            color = getColor(authToken, gameID);
        } catch (Exception e) {
            throw new IOException();
        }

        var message = String.format("%s (%s) has resigned.", username, color);
        var notification = new NotificationMessage(message);
        connections.broadcast(authToken, notification, true);

    }

    private String getUsername(String authToken) throws IOException {
        try {
            UserData userData = userService.getUser(authToken);
            return userData.username();
        } catch (Exception e) {
            throw new IOException();
        }
    }

    private String getColor(String authToken, int gameID) throws IOException {
        try {
            UserData userData = userService.getUser(authToken);
            String username = userData.username();
            GameData gameData = (GameData) gameService.getGame(authToken, gameID)[1];
            if (gameData.blackUsername().equals(username)) {
                return "BLACK";
            } else if (gameData.whiteUsername().equals(username)) {
                return "WHITE";
            } else {
                return "";
            }
        } catch (Exception e) {
            throw new IOException();
        }
    }

}