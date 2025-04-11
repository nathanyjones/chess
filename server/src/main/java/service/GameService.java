package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import request.UpdateBoardRequest;
import service.request.JoinGameRequest;
import service.result.CreateGameResult;
import service.result.JoinGameResult;
import result.ListGamesResult;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Object[] listGames(String authToken) {
        try {
            dataAccess.getAuth(authToken);
            return new Object[] {200, new ListGamesResult(dataAccess.listGames())};
        } catch (DataAccessException e) {
            return new Object[] {401, new ListGamesResult("Error: unauthorized")};
        } catch (Exception e) {
            return new Object[] {500, new ListGamesResult("Error: " + e.getMessage())};
        }
    }

    public Object[] createGame(String authToken, String gameName) {
        if (gameName == null || gameName.isEmpty()) {
            return new Object[] {400, new CreateGameResult("Error: bad request")};
        }
        try {
            dataAccess.getAuth(authToken);
            return new Object[] {200, new CreateGameResult(dataAccess.createGame(gameName))};
        } catch (DataAccessException e) {
            return new Object[] {401, new CreateGameResult("Error: unauthorized")};
        } catch (Exception e) {
            return new Object[] {500, new CreateGameResult("Error: " + e.getMessage())};
        }
    }

    public Object[] joinGame(String authToken, JoinGameRequest joinGameRequest) {
        String color = joinGameRequest.playerColor();
        Integer gameID = joinGameRequest.gameID();
        try {
            AuthData authData = dataAccess.getAuth(authToken);
            if (color == null || (!color.equals("WHITE") && !color.equals("BLACK")) || gameID == null) {
                return new Object[] {400, new JoinGameResult("Error: bad request")};
            }
            GameData gameData = dataAccess.getGame(gameID);
            if (color.equals("WHITE")) {
                if (gameData.whiteUsername() == null) {
                    dataAccess.updateGame(gameID, new GameData(gameID, authData.username(),
                            gameData.blackUsername(), gameData.gameName(), gameData.game()));
                    return new Object[]{200, new JoinGameResult(null)};
                } else {
                    return new Object[]{403, new JoinGameResult("Error: already taken")};
                }
            } else {
                if (gameData.blackUsername() == null) {
                    dataAccess.updateGame(gameID, new GameData(gameID, gameData.whiteUsername(),
                            authData.username(), gameData.gameName(), gameData.game()));
                    return new Object[]{200, new JoinGameResult(null)};
                } else {
                    return new Object[]{403, new JoinGameResult("Error: already taken")};
                }
            }

        } catch (Exception e) {
            return handleException(e);
        }
    }

    public Object[] updateBoard(String authToken, Integer gameID, UpdateBoardRequest updateBoardRequest) {
        ChessBoard board = updateBoardRequest.board();
        System.out.println("Okay so the integer now is " + gameID);
        try {
            dataAccess.getAuth(authToken);
            GameData gameData = dataAccess.getGame(gameID);
            ChessGame game = gameData.game();
            game.setBoard(board);
            dataAccess.updateGame(gameID, new GameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), game));
            return new Object[] {200, new JoinGameResult(null)};
        } catch (Exception e) {
            return handleException(e);
        }
    }

    public Object[] updateGame(String authToken, Integer gameID, GameData updatedGameData) {
        System.out.println("Okay so the integer now is " + gameID);
        try {
            dataAccess.getAuth(authToken);
            dataAccess.updateGame(gameID, updatedGameData);
            return new Object[] {200, new JoinGameResult(null)};
        } catch (Exception e) {
            return handleException(e);
        }
    }

    public Object[] getGame(String authToken, Integer gameID) {
        if (gameID == null) {
            return new Object[] {400, new JoinGameResult("Error: bad request")};
        }
        try {
            dataAccess.getAuth(authToken);
            return new Object[] {200, dataAccess.getGame(gameID)};
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private Object[] handleException(Exception e) {
        if (e instanceof DataAccessException) {
            if (e.getMessage().contains("not found")) {
                return new Object[]{401, new JoinGameResult("Error: unauthorized")};
            } else {
                return new Object[] {500, new JoinGameResult("Error: " + e.getMessage())};
            }
        }
        return new Object[] {500, new JoinGameResult("Error: " + e.getMessage())};
    }
}

