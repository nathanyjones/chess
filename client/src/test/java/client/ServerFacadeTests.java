package client;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    final private static UserData user1 = new UserData("user1", "12345", "u1@mail.com");
    final private static UserData otherUser1 = new UserData("user1", "54321", "u11@mail.com");
    final private static UserData user2 = new UserData("user2", "13579", "u2@mail.com");
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
            facade.register(otherUser1);
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
            facade.login(otherUser1);
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
        assertThrows(ResponseException.class, () -> {
            facade.createGame(invalidAuthToken, "Game 1");
        });
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
        assertThrows(ResponseException.class, () -> {
            facade.createGame(authToken, "Game 1");
        });
    }

    @Test
    public void logoutFailInvalidAuthTokenTest() {
        try {
            facade.register(user1);
        } catch (ResponseException e) {
            fail("Unexpected ResponseException from Register Method: " + e.getMessage());
            return;
        }
        assertThrows(ResponseException.class, () -> {
            facade.logout(invalidAuthToken);
        });
    }




}
