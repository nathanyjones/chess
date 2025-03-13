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

    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Auth");
            stmt.executeUpdate("DELETE FROM Games");
            stmt.executeUpdate("DELETE FROM Users");
            stmt.executeUpdate("ALTER TABLE Games AUTO_INCREMENT = 1");
        } catch (SQLException e) {
            throw new DataAccessException("Connection failed: " + e.getMessage());
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

}
