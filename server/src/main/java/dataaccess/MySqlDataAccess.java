package dataaccess;

import com.google.gson.Gson;
import jdk.jshell.spi.ExecutionControl;
import model.UserData;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws ResponseException {
        configureDatabase();
    }

    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Auth");
            stmt.executeUpdate("DELETE FROM Games");
            stmt.executeUpdate("DELETE FROM Users");
            stmt.executeUpdate("ALTER TABLE Games AUTO_INCREMENT = 1");
        } catch (SQLException | ResponseException e) {
            throw new DataAccessException("Unable to clear database: " + e.getMessage());
        }
    }

    public void createUser(UserData user) throws DataAccessException {
        throw new DataAccessException("Didn't implement yet");
    }
    public UserData getUser(String username) throws DataAccessException {
        throw new DataAccessException("Didn't implement yet");
    }

    public int createGame(String gameName) throws DataAccessException {
        throw new DataAccessException("Didn't implement yet");
    }
    public GameData getGame(int gameId) throws DataAccessException {
        throw new DataAccessException("Didn't implement yet");
    }
    public Collection<GameData> listGames() throws DataAccessException {
        throw new DataAccessException("Didn't implement yet");
    }
    public void updateGame(int gameId, GameData updatedGame) throws DataAccessException {
        throw new DataAccessException("Didn't implement yet");
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        throw new DataAccessException("Didn't implement yet");
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        throw new DataAccessException("Didn't implement yet");
    }
    public void deleteAuth(String authToken) throws DataAccessException {
        throw new DataAccessException("Didn't implement yet");
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                          `id` INT AUTO_INCREMENT,
                          `username` varchar(255) NOT NULL UNIQUE,
                          `password` varchar(255) NOT NULL,
                          `email` varchar(255) NOT NULL,
                          PRIMARY KEY (id),
                          INDEX(username)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
                          `id` INT AUTO_INCREMENT,
                          `gameData` JSON NOT NULL,
                          PRIMARY KEY (id)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
                          `authToken` INT UNIQUE NOT NULL,
                          `username` varchar(255) NOT NULL,
                          PRIMARY KEY (authToken),
                          FOREIGN KEY (username) REFERENCES users(username)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws ResponseException {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new ResponseException(String.format("Unable to create database: %s", e.getMessage()));
        }
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | ResponseException e) {
            throw new ResponseException(String.format("Unable to configure database: %s", e.getMessage()));
        }
    }

}
