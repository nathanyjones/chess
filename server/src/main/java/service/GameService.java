package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import service.request.JoinGameRequest;
import service.result.CreateGameResult;
import service.result.JoinGameResult;
import service.result.ListGamesResult;

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
        System.out.println("GameID: " + gameID);
        try {
            AuthData authData = dataAccess.getAuth(authToken);
            if (color == null || (!color.equals("WHITE") && !color.equals("BLACK")) || gameID == null) {
                return new Object[] {400, new JoinGameResult("Error: bad request")};
            }
            GameData gameData = dataAccess.getGame(gameID);
            if (color.equals("WHITE")) {
                if (gameData.whiteUsername() == null) {
                    dataAccess.updateGame(gameData.gameID(), new GameData(gameData.gameID(), authData.username(),
                            gameData.blackUsername(), gameData.gameName(), gameData.game()));
                    return new Object[]{200, new JoinGameResult(null)};
                } else {
                    return new Object[]{403, new JoinGameResult("Error: already taken")};
                }
            } else {
                if (gameData.blackUsername() == null) {
                    dataAccess.updateGame(gameData.gameID(), new GameData(gameData.gameID(), gameData.whiteUsername(),
                            authData.username(), gameData.gameName(), gameData.game()));
                    return new Object[]{200, new JoinGameResult(null)};
                } else {
                    return new Object[]{403, new JoinGameResult("Error: already taken")};
                }
            }

        } catch (DataAccessException e) {
            return new Object[] {401, new JoinGameResult("Error: unauthorized")};
        } catch (Exception e) {
            return new Object[] {500, new JoinGameResult("Error: " + e.getMessage())};
        }
    }
}

