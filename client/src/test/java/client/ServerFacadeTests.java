package client;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    final private static UserData user1 = new UserData("user1", "12345", "u1@mail.com");
    final private static UserData duplicateUser1 = new UserData("user1", "54321", "u11@mail.com");
    final private static String invalidAuthToken = "invalid_auth_token";

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    void setup() {
        try {
            facade.clear();
        } catch (ResponseException e) {
            System.err.println("Error Clearing the Database: " + e.getMessage());
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerSuccessTest() {
        try {
            AuthData authData = facade.register(user1);
            assertEquals("user1", authData.username());
            assertNotNull(authData.authToken());
        } catch (ResponseException e) {
            fail("Unexpected ResponseException: " + e.getMessage());
        }
    }

    @Test
    public void registerFailUserExistsTest() {
        assertThrows(ResponseException.class, () -> {
            facade.register(user1);
            facade.register(duplicateUser1);
        });
    }

    @Test
    public void loginSuccessTest() {
        try {
            AuthData authData = facade.register(user1);
            assertEquals("user1", authData.username());
            assertNotNull(authData.authToken());
        } catch (ResponseException e) {
            fail("Unexpected ResponseException: " + e.getMessage());
        }
    }

    @Test
    public void loginFailInvalidPasswordTest() {
        assertThrows(ResponseException.class, () -> {
            facade.register(user1);
            facade.login(duplicateUser1);
        });
    }

    @Test
    public void createGameSuccessTest() {
        try {
            AuthData authData = facade.register(user1);
            Integer gameID = facade.createGame(authData.authToken(), "Game 1");
            assertNotNull(gameID);
        } catch (ResponseException e) {
            fail("Unexpected ResponseException: " + e.getMessage());
        }
    }

    @Test
    public void createGameFailInvalidAuthTest() {
        assertThrows(ResponseException.class, () -> facade.createGame(invalidAuthToken, "Game 1"));
    }

    @Test
    public void logoutSuccessCreateGameTest() {
        String authToken;
        try {
            authToken = facade.register(user1).authToken();
            facade.logout(authToken);
        } catch (ResponseException e) {
            fail("Unexpected ResponseException from Register or Logout Method: " + e.getMessage());
            return;
        }
        assertThrows(ResponseException.class, () -> facade.createGame(authToken, "Game 1"));
    }

    @Test
    public void logoutFailInvalidAuthTokenTest() {
        try {
            facade.register(user1);
        } catch (ResponseException e) {
            fail("Unexpected ResponseException from Register Method: " + e.getMessage());
            return;
        }
        assertThrows(ResponseException.class, () -> facade.logout(invalidAuthToken));
    }

    @Test
    public void joinGameSuccessTest() {
        try {
            AuthData authData = facade.register(user1);
            Integer gameID = facade.createGame(authData.authToken(), "Game 1");
            facade.joinGame(authData.authToken(), gameID, "WHITE");
        } catch (ResponseException e) {
            fail("Unexpected ResponseException: " + e.getMessage());
        }
    }

    @Test
    public void joinGameFailInvalidGameIDTest() {
        AuthData authData;
        Integer gameID;
        try {
            authData = facade.register(user1);
            gameID = facade.createGame(authData.authToken(), "Game 1");
        } catch (ResponseException e) {
            fail("Unexpected ResponseException from Registration / Creating Game: " + e.getMessage());
            return;
        }
        assertThrows(ResponseException.class, () -> facade.joinGame(authData.authToken(),
                gameID+1, "WHITE"));
    }

    @Test
    public void listGamesSuccessTest() {
        try {
            AuthData authData = facade.register(user1);
            Integer gameID = facade.createGame(authData.authToken(), "Game 1");
            ArrayList<GameData> games = (ArrayList<GameData>) facade.listGames(authData.authToken());
            assertNotNull(games);
            assertEquals(1, games.size());
            assertEquals(gameID, games.getFirst().gameID());
        } catch (ResponseException e) {
            fail("Unexpected ResponseException: " + e.getMessage());
        }
    }

    @Test
    public void listGamesFailInvalidAuthTest() {
        assertThrows(ResponseException.class, () -> facade.listGames(invalidAuthToken));
    }

    @Test
    public void clearSuccessTest() {
        try {
            String authToken = facade.register(user1).authToken();
            facade.createGame(authToken, "Game 1");
            facade.createGame(authToken, "Game 2");
            int numGames = facade.listGames(authToken).size();
            assertEquals(2, numGames);
            facade.clear();
            authToken = facade.register(user1).authToken();
            Collection<GameData> games = facade.listGames(authToken);
            assertEquals(0, games.size());
        } catch (ResponseException e) {
            fail("Unexpected ResponseException: " + e.getMessage());
        }
    }

}
