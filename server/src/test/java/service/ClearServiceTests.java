package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.LoginRequest;
import service.request.RegisterRequest;
import service.result.ListGamesResult;
import service.result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
    private ClearService clearService;
    private UserService userService;
    private GameService gameService;


    @BeforeEach
    void setup() {
        DataAccess dataAccess = new MemoryDataAccess();
        clearService = new ClearService(dataAccess);
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
    }

    @Test
    void clearSuccess() {
        clearService.clear();
        Object[] registerResult = userService.register(new RegisterRequest("user1",
                "12345", "user1@mail.com"));
        String authToken = ((RegisterResult) registerResult[1]).getAuthToken();
        gameService.createGame(authToken, "Game 1");
        clearService.clear();

        Object[] loginResultObj = userService.login(new LoginRequest("user1", "12345"));
        int loginStatusCode = (int) loginResultObj[0];
        assertEquals(401, loginStatusCode);

        ListGamesResult listGamesResult = (ListGamesResult) gameService.listGames(authToken)[1];
        assertNull(listGamesResult.getGameList());
    }

}
