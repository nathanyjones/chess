package dataaccess;

import model.*;

import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private int nextId = 1;
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    final private HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        games.clear();
        auths.clear();
        nextId = 1;
    }

    public void createUser(UserData user) throws DataAccessException {
        String username = user.username();
        if (users.containsKey(username)) {
            throw new DataAccessException("Error: Username already in use");
        } else {
            users.put(username, user);
        }
    }
    UserData getUser(String username) throws DataAccessException {
        if (users.containsKey(username)) {
            return users.get(username);
        } else {
            throw new DataAccessException("Error: " + username + " does not exist");
        }
    }

    void createGame() throws DataAccessException;
    GameData getGame() throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame() throws DataAccessException;

    void createAuth() throws DataAccessException;
    AuthData getAuth() throws DataAccessException;
    void deleteAuth() throws DataAccessException;

}