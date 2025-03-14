package dataaccess;

import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

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
            dataAccess.createGame(null);
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
            dataAccess.getGame(gameId + 1);

            fail("Retrieving nonexistent game did not throw an exception.");

        } catch (DataAccessException e) {
            assertEquals("Error: " + (gameId+1) + " not found", e.getMessage());
        }
    }

    @Test
    void listGamesSuccess() {
        try {
            int id1 = dataAccess.createGame("game1");
            int id2 = dataAccess.createGame("game2");
            Collection<GameData> games = dataAccess.listGames();

            GameData game1 = dataAccess.getGame(id1);
            GameData game2 = dataAccess.getGame(id2);
            Collection<GameData> expectedGames = new ArrayList<>();
            expectedGames.add(game1);
            expectedGames.add(game2);

            assertEquals(expectedGames, games);

        } catch (DataAccessException e) {
            fail("Error: DataAccess Exception: " + e.getMessage());
        }
    }

    @Test
    void listGamesNoGames() {
        try {
            Collection<GameData> games = dataAccess.listGames();
            Collection<GameData> expectedGames = new ArrayList<>();

            assertEquals(expectedGames, games);

        } catch (DataAccessException e) {
            fail("Error: DataAccess Exception: " + e.getMessage());
        }
    }

    @Test
    void updateGameSuccessAddPlayerWhite() {
        try {
            String gameName = "game1";
            String username = "user1";
            UserData userData = new UserData(username, "12345", "email@mail.com");
            int gameId = dataAccess.createGame(gameName);
            dataAccess.createUser(userData);

            GameData oldGame = dataAccess.getGame(gameId);
            GameData updatedGame = new GameData(username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
            dataAccess.updateGame(gameId, updatedGame);

            GameData retrievedGame = dataAccess.getGame(gameId);

            assertEquals(updatedGame, retrievedGame);
            assertEquals(oldGame.blackUsername(), retrievedGame.blackUsername());
            assertEquals(oldGame.gameName(), retrievedGame.gameName());

        } catch (DataAccessException e) {
            fail("Error: DataAccess Exception: " + e.getMessage());
        }
    }

    @Test
    void updateGameFailInvalidId() {
        int gameId = -1;
        try {
            String gameName = "game1";
            String username = "user1";
            UserData userData = new UserData(username, "12345", "email@mail.com");
            gameId = dataAccess.createGame(gameName);
            dataAccess.createUser(userData);

            GameData oldGame = dataAccess.getGame(gameId);
            GameData updatedGame = new GameData(username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game());
            dataAccess.updateGame(gameId+1, updatedGame);

            fail("Updated game with invalid gameId");

        } catch (DataAccessException e) {
            assertEquals("Error: " + (gameId+1) + " not found", e.getMessage());
        }
    }



}
