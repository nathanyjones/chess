package dataaccess;

import model.*;

import java.util.Collection;

public interface DataAccess {
    void clear() throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;

    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameId) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(int gameId, GameData updatedGame) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}