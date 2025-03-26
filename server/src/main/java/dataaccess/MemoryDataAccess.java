package dataaccess;

import chess.ChessGame;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private int nextGameId = 1;
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    final private HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() {
        users.clear();
        games.clear();
        auths.clear();
        nextGameId = 1;
    }

    public void createUser(UserData user) throws DataAccessException {
        String username = user.username();
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        if (users.containsKey(username)) {
            throw new DataAccessException("Error: already taken");
        } else {
            users.put(username, new UserData(user.username(), hashedPassword, user.email()));
        }
    }
    public UserData getUser(String username) throws DataAccessException {
        if (users.containsKey(username)) {
            return users.get(username);
        } else {
            throw new DataAccessException("Error: " + username + " not found");
        }
    }

    public int createGame(String gameName) {
        GameData game = new GameData(nextGameId, null, null,
                gameName, new ChessGame());
        games.put(nextGameId, game);
        nextGameId += 1;
        return nextGameId - 1;
    }
    public GameData getGame(int gameId) throws DataAccessException {
        if (games.containsKey(gameId)) {
            return games.get(gameId);
        } else {
            throw new DataAccessException("Error: Game " + gameId + " not found");
        }
    }
    public Collection<GameData> listGames() {
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
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

}