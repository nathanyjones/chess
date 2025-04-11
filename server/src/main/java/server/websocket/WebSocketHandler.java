package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
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
                makeMove(makeMoveCommand.getAuthToken(), makeMoveCommand.getGameID(),
                        makeMoveCommand.getMove(), session);
                break;
            case LEAVE:
                leave(userGameCommand.getAuthToken(), userGameCommand.getGameID());
                break;
            case RESIGN:
                resign(userGameCommand.getAuthToken(), userGameCommand.getGameID(), session);
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

    private void makeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException {
        try {
            GameData gameData = getGameData(authToken, gameID);
            ChessGame game = gameData.game();
            if (game.getGameOver()) {
                sendErrorMessage(session, "Game has ended. Cannot make additional moves.");
                return;
            }
            String playerColorString = getColor(authToken, gameID);
            if (playerColorString.isEmpty()) {
                sendErrorMessage(session, "Cannot make moves as observer. Join a game as a player to play.");
                return;
            }
            ChessGame.TeamColor playerColor = playerColorString.equals("WHITE") ?
                    ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            if (game.getTeamTurn() != playerColor) {
                sendErrorMessage(session, "Cannot move on other player's turn.");
                return;
            } else if (game.getBoard().getPiece(move.getStartPosition()).getTeamColor() != playerColor) {
                sendErrorMessage(session, "Cannot move other player's piece.");
                return;
            }

            gameData.game().makeMove(move);
            gameService.updateGame(authToken, gameID, gameData);

            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            connections.broadcast(authToken, loadGameMessage, true);

            NotificationMessage notification = getMoveNotification(authToken, move);
            connections.broadcast(authToken, notification, false);

            ChessGame gameRef = gameData.game();
            if (gameRef.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                game.setGameOver(true);
                var msg = new NotificationMessage(String.format("%s is in checkmate!", gameData.whiteUsername()));
                connections.broadcast(authToken, msg, true);
            } else if (gameRef.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                game.setGameOver(true);
                var msg = new NotificationMessage(String.format("%s is in checkmate!", gameData.blackUsername()));
                connections.broadcast(authToken, msg, true);
            } else if (gameRef.isInCheck(ChessGame.TeamColor.WHITE)) {
                var msg = new NotificationMessage(String.format("%s is in check.", gameData.whiteUsername()));
                connections.broadcast(authToken, msg, true);
            } else if (gameRef.isInCheck(ChessGame.TeamColor.BLACK)) {
                var msg = new NotificationMessage(String.format("%s is in check.", gameData.blackUsername()));
                connections.broadcast(authToken, msg, true);
            } else if (gameRef.isInStalemate(ChessGame.TeamColor.WHITE)) {
                game.setGameOver(true);
                var msg = new NotificationMessage("Stalemate!");
                connections.broadcast(authToken, msg, true);
            }

            gameService.updateGame(authToken, gameID, gameData);

        } catch (InvalidMoveException e) {
            sendErrorMessage(session, "Not a legal chess move.");
        } catch (Exception e) {
            sendErrorMessage(session, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    private NotificationMessage getMoveNotification(String authToken, ChessMove move) {
        String username = connections.getUsername(authToken);
        char startColumn = (char) (move.getStartPosition().getColumn() - 1 + 'a');
        int startRow = move.getStartPosition().getRow();
        char endColumn = (char) (move.getEndPosition().getColumn() - 1 + 'a');
        int endRow = move.getStartPosition().getRow();

        String moveString = "from " + startColumn + startRow + " to " + endColumn + endRow;
        String message = String.format("%s moved " + moveString + ".", username);
        return new NotificationMessage(message);
    }

    private void leave(String authToken, int gameID) throws IOException {
        String color = "...";
        try {
            color = getColor(authToken, gameID);
        } catch (Exception e) {
            //
        }
        try {
            String username = getUsername(authToken);

            boolean isPlayer = !color.isEmpty();

            GameData gameData = (GameData) gameService.getGame(authToken, gameID)[1];
            if (color.equals("WHITE")) {
                gameService.updateGame(authToken, gameID, new GameData(gameID, null,
                        gameData.blackUsername(), gameData.gameName(), gameData.game()));
            } else if (color.equals("BLACK")) {
                gameService.updateGame(authToken, gameID, new GameData(gameID, gameData.whiteUsername(),
                        null, gameData.gameName(), gameData.game()));
            }
            var message = String.format("%s left the game", username);
            var notification = new NotificationMessage(message);
            System.out.println("The color is: " + color);
            if (isPlayer) {
                connections.broadcast(authToken, notification, false);
            }
            connections.remove(authToken);
        } catch (Exception e) {
            throw new IOException();
        }

    }

    private void resign(String authToken, int gameID, Session session) throws IOException {
        try {
            String username = getUsername(authToken);
            String color = getColor(authToken, gameID);
            if (color.isEmpty()) {
                sendErrorMessage(session, "Cannot resign as an observer.");
                return;
            }
            GameData gameData = getGameData(authToken, gameID);
            if (gameData.game().getGameOver()) {
                sendErrorMessage(session, "The game has ended. Cannot resign.");
                return;
            }
            gameData.game().setGameOver(true);
            gameService.updateGame(authToken, gameID, gameData);
            var message = String.format("%s (%s) has resigned.", username, color);
            var notification = new NotificationMessage(message);
            connections.broadcast(authToken, notification, true);
        } catch (Exception e) {
            throw new IOException();
        }

    }

    private String getUsername(String authToken) throws IOException {
        try {
            UserData userData = userService.getUser(authToken);
            return userData.username();
        } catch (Exception e) {
            throw new IOException("Failed to retrieve username.");
        }
    }

    private String getColor(String authToken, int gameID) throws IOException {
        try {
            UserData userData = userService.getUser(authToken);
            String username = userData.username();
            GameData gameData = getGameData(authToken, gameID);
            if (gameData.blackUsername().equals(username)) {
                return "BLACK";
            } else if (gameData.whiteUsername().equals(username)) {
                return "WHITE";
            } else {
                return "";
            }
        } catch (Exception e) {
            throw new IOException("Failed to retrieve color.");
        }
    }

    private GameData getGameData(String authToken, int gameID) throws IOException {
        try {
            return (GameData) gameService.getGame(authToken, gameID)[1];
        } catch (Exception e) {
            if (e.getMessage().contains(" not found")) {
                throw new IOException("Game not found.");
            }
            throw new IOException("Internal Server Error: Failed to retrieve Game.");
        }
    }

}