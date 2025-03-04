package service;

import dataaccess.MemoryDataAccess;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import dataaccess.DataAccess;
import service.request.*;
import service.result.*;

public class UserServiceTests {
    private UserService userService;
    private String user1AuthToken;

    @BeforeEach
    void setup() {
        DataAccess dataAccess = new MemoryDataAccess();
        ClearService clearService = new ClearService(dataAccess);
        userService = new UserService(dataAccess);

        clearService.clear();
        Object[] registerUserObj = userService.register(new RegisterRequest("user1",
                "12345", "user1@mail.com"));
        if ((int) registerUserObj[0] == 200) {
            RegisterResult registerResult = (RegisterResult) registerUserObj[1];
            user1AuthToken = registerResult.getAuthToken();
        }
    }

    @Test
    void registerUserSuccess() {
        String username = "user2";
        String password = "54321";
        String email = "user2@mail.com";
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        Object[] registerOutput = userService.register(registerRequest);
        int statusCode = (int) registerOutput[0];
        RegisterResult result = (RegisterResult) registerOutput[1];
        assertEquals(200, statusCode);
        assertNotNull(result.getAuthToken());
        assertEquals(username, result.getUsername());
    }

    @Test
    void registerUserFailAlreadyTaken() {
        String username = "user1";
        String password = "54321";
        String email = "user2@mail.com";
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        Object[] registerOutput = userService.register(registerRequest);
        int statusCode = (int) registerOutput[0];
        RegisterResult result = (RegisterResult) registerOutput[1];
        assertEquals(403, statusCode);
        assertEquals("Error: already taken", result.getMessage());
    }

    @Test
    void loginSuccess() {
        String username = "user1";
        String password = "12345";
        LoginRequest loginRequest = new LoginRequest(username, password);
        Object[] loginOutput = userService.login(loginRequest);
        int statusCode = (int) loginOutput[0];
        LoginResult result = (LoginResult) loginOutput[1];
        assertEquals(200, statusCode);
        assertNotNull(result.getAuthToken());
        assertEquals(username, result.getUsername());
    }

    @Test
    void loginFailIncorrectPassword() {
        String username = "user1";
        String password = "11111";
        LoginRequest loginRequest = new LoginRequest(username, password);
        Object[] loginOutput = userService.login(loginRequest);
        int statusCode = (int) loginOutput[0];
        LoginResult result = (LoginResult) loginOutput[1];
        assertEquals(401, statusCode);
        assertEquals("Error: unauthorized", result.getMessage());
    }

    @Test
    void loginFailInvalidUsername() {
        String username = "user2";
        String password = "12345";
        LoginRequest loginRequest = new LoginRequest(username, password);
        Object[] loginOutput = userService.login(loginRequest);
        int statusCode = (int) loginOutput[0];
        LoginResult result = (LoginResult) loginOutput[1];
        assertEquals(401, statusCode);
        assertEquals("Error: unauthorized", result.getMessage());
    }

    @Test
    void logoutSuccess() {
        Object[] logoutOutput = userService.logout(user1AuthToken);
        int statusCode = (int) logoutOutput[0];
        LogoutResult result = (LogoutResult) logoutOutput[1];
        assertEquals(200, statusCode);
        assertEquals(new LogoutResult(null), result);
    }

    @Test
    void logoutFailInvalidAuthToken() {
        String testAuthToken = "invalid_auth_token_string";
        Object[] logoutOutput = userService.logout(testAuthToken);
        int statusCode = (int) logoutOutput[0];
        LogoutResult result = (LogoutResult) logoutOutput[1];
        assertEquals(401, statusCode);
        assertEquals("Error: unauthorized", result.message());
    }

}
