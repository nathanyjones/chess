package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private int nextGameId = 1;
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    final private HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        games.clear();
        auths.clear();
        nextGameId = 1;
    }

    public void createUser(UserData user) throws DataAccessException {
        String username = user.username();
        if (users.containsKey(username)) {
            throw new DataAccessException("Error: Username already in use");
        } else {
            users.put(username, user);
        }
    }
    public UserData getUser(String username) throws DataAccessException {
        if (users.containsKey(username)) {
            return users.get(username);
        } else {
            throw new DataAccessException("Error: " + username + " not found");
        }
    }

    public void createGame(String gameName) throws DataAccessException {
        GameData game = new GameData(nextGameId, null, null,
                gameName, new ChessGame());
        games.put(nextGameId, game);
        nextGameId += 1;
    }
    public GameData getGame(int gameId) throws DataAccessException {
        if (games.containsKey(gameId)) {
            return games.get(gameId);
        } else {
            throw new DataAccessException("Error: Game " + gameId + " not found");
        }
    }
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }
    public void updateGame(int gameId, GameData updatedGame) throws DataAccessException {
        if (games.containsKey(gameId)) {
            games.put(gameId, updatedGame);
        } else {
            throw new DataAccessException("Error: Game " + gameId + " not found");
        }
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        String authToken = auth.authToken();
        if (!auths.containsKey(authToken)) {
            auths.put(authToken, auth);
        } else {
            throw new DataAccessException("Error: AuthToken already exists");
        }
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (auths.containsKey(authToken)) {
            return auths.get(authToken);
        } else {
            throw new DataAccessException("Error: AuthToken " + authToken + " not found");
        }
    }
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }

}