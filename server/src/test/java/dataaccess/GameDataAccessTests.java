package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class GameDataAccessTests {
    private DataAccess dataAccess;
    private Connection connection;

    @BeforeEach
    void setup() {
        try {
            dataAccess = new MySqlDataAccess();
            connection = DatabaseManager.getConnection();
            try (var stmt = connection.createStatement()) {
                stmt.execute("DELETE FROM games");
                stmt.execute("DELETE FROM auths");
                stmt.execute("DELETE FROM users");
            }
        } catch (Exception e) {
            System.err.println("Error initializing data access: " + e.getMessage());
        }
    }

    @Test
    void createGameSuccess() {
        try {
            String gameName = "game1";
            int gameId = dataAccess.createGame(gameName);

            var statement = "SELECT * FROM games WHERE id = ?";
            try (var stmt = connection.prepareStatement(statement)) {
                stmt.setInt(1, gameId);
                var rs = stmt.executeQuery();
                assertTrue(rs.next());
                assertEquals(gameId, rs.getInt("id"));
                assertNotNull(rs.getString("gameData"));
            }

        } catch (SQLException e) {
            fail("Error: SQL Exception: " + e.getMessage());

        } catch (DataAccessException e) {
            fail("Error: DataAccess Exception: " + e.getMessage());
        }
    }

    @Test
    void createGameFailNoNameProvided() {
        try {
            int gameId = dataAccess.createGame(null);
            fail("Game created with no name provided");
        } catch (DataAccessException e) {
            assertEquals("Error: No game name provided.", e.getMessage());
        }
    }

    @Test
    void getGameSuccess() {
        try {
            String gameName = "game1";
            int id = dataAccess.createGame(gameName);
            GameData retrievedGame = dataAccess.getGame(id);

            assertEquals(gameName, retrievedGame.gameName());

        } catch (DataAccessException e) {
            fail("Error: DataAccess Exception: " + e.getMessage());
        }
    }

    @Test
    void getGameFailNotFound() {
        String gameName = "game1";
        int gameId = -1;
        try {
            gameId = dataAccess.createGame(gameName);
            GameData retrievedGame = dataAccess.getGame(gameId + 1);

            fail("Retrieving nonexistent game did not throw an exception.");

        } catch (DataAccessException e) {
            assertEquals("Error: " + (gameId+1) + " not found", e.getMessage());
        }
    }

//    @Test
//    void listGamesSuccess() {
//        try {
//            String gameName = "game1";
//            int id = dataAccess.createGame(gameName);
//            Collection<ChessGame> = dataAccess.getGame(id);
//
//            assertEquals(gameName, retrievedGame.gameName());
//
//        } catch (DataAccessException e) {
//            fail("Error: DataAccess Exception: " + e.getMessage());
//        }
//    }

}
