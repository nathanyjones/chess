package dataaccess;

import model.*;

import java.util.Collection;

public interface DataAccess {
    void clear() throws DataAccessException;

    void createUser() throws DataAccessException;
    UserData getUser() throws DataAccessException;

    void createGame() throws DataAccessException;
    GameData getGame() throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame() throws DataAccessException;

    void createAuth() throws DataAccessException;
    AuthData getAuth() throws DataAccessException;
    void deleteAuth() throws DataAccessException;
}