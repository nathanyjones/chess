package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import service.result.CreateGameResult;
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

}

