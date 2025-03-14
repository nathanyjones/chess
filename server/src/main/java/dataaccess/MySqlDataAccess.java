package dataaccess;

//import com.google.gson.Gson;
//import jdk.jshell.spi.ExecutionControl;
import chess.ChessGame;
import com.google.gson.Gson;
import model.UserData;
import model.AuthData;
import model.GameData;
import org.mindrot.jbcrypt.BCrypt;

//import java.util.ArrayList;
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
            stmt.executeUpdate("DELETE FROM auth");
            stmt.executeUpdate("DELETE FROM games");
            stmt.executeUpdate("DELETE FROM users");
            stmt.executeUpdate("ALTER TABLE games AUTO_INCREMENT = 1");
            stmt.executeUpdate("ALTER TABLE users AUTO_INCREMENT = 1");
        } catch (SQLException | ResponseException e) {
            throw new DataAccessException("Unable to clear database: " + e.getMessage());
        }
    }

    public void createUser(UserData user) throws DataAccessException {
        var checkTakenStatement = "SELECT COUNT(*) FROM users WHERE username = ?";
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        try {
            if (checkExistence(checkTakenStatement, user.username())) {
                executeUpdate(statement, user.username(), hashedPassword, user.email());
            } else {
                throw new DataAccessException("Error: already taken");
            }
        } catch (ResponseException e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"),
                                rs.getString("password"), rs.getString("email"));
                    } else {
                        throw new DataAccessException("Error: " + username + " not found");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Failed to get user: " + e.getMessage());
        }
    }

    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO games (gameData) VALUES (?)";
        GameData game = new GameData(null, null, gameName, new ChessGame());
        Gson serializer = new Gson();
        String gameJSON = serializer.toJson(game);

        try {
            return executeUpdate(statement, gameJSON);
        } catch (ResponseException e) {
            throw new DataAccessException("Failed to create game: " + e.getMessage());
        }

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

    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p)
                        ps.setString(i + 1, p);
                    else if (param instanceof Integer p)
                        ps.setInt(i + 1, p);
                    else if (param == null)
                        ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new ResponseException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private boolean checkExistence(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p)
                        ps.setString(i + 1, p);
                    else if (param instanceof Integer p)
                        ps.setInt(i + 1, p);
                    else if (param == null)
                        ps.setNull(i + 1, NULL);
                }
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
                return false;
            }
        } catch (SQLException e) {
            throw new ResponseException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                          `id` int AUTO_INCREMENT,
                          `username` varchar(255) NOT NULL UNIQUE,
                          `password` varchar(255) NOT NULL,
                          `email` varchar(255) NOT NULL,
                          PRIMARY KEY (id),
                          INDEX(username)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
                          `id` int AUTO_INCREMENT,
                          `gameData` JSON NOT NULL,
                          PRIMARY KEY (id)
                        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
                          `authToken` int UNIQUE NOT NULL,
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
//            System.err.println("Error: " + e.getMessage());
            throw new ResponseException(String.format("Unable to create database: %s", e.getMessage()));
        }
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | ResponseException e) {
//            System.err.println("Error: " + e.getMessage());
            throw new ResponseException(String.format("Unable to configure database: %s", e.getMessage()));
        }
    }

}
