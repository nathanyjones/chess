package client;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

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
        UserData user1 = new UserData("user1", "12345", "email@mail.com");
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
        UserData user1 = new UserData("user1", "12345", "email@mail.com");
        UserData otherUser1 = new UserData("user1", "54321", "newEmail@mail.com");
        try {
            facade.register(user1);
            facade.register(otherUser1);
            fail("Duplicate user was registered with no raised exception.");
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
    }

}
