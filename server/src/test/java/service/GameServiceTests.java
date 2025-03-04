package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.*;
import service.result.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GameServiceTests {
    private GameService gameService;
    private String user1AuthToken;
    private String user2AuthToken;
    private final String invalidAuthToken = "invalid_auth_token";

    @BeforeEach
    void setup() {
        DataAccess dataAccess = new MemoryDataAccess();
        ClearService clearService = new ClearService(dataAccess);
        UserService userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);

        clearService.clear();
        Object[] registerUserObj1 = userService.register(new RegisterRequest("user1",
                "12345", "user1@mail.com"));
        Object[] registerUserObj2 = userService.register(new RegisterRequest("user2",
                "54321", "user2@mail.com"));
        user1AuthToken = ((RegisterResult) registerUserObj1[1]).getAuthToken();
        user2AuthToken = ((RegisterResult) registerUserObj2[1]).getAuthToken();
    }

    @Test
    void createGameSuccess() {
        String gameName = "game1";
        Object[] createGameOutput = gameService.createGame(user1AuthToken, gameName);
        int statusCode = (int) createGameOutput[0];
        CreateGameResult result = (CreateGameResult) createGameOutput[1];
        assertEquals(200, statusCode);
        assertNotNull(result.getGameID());
    }

    @Test
    void createGameFailUnauthorized() {
        String gameName = "game1";
        Object[] createGameOutput = gameService.createGame(invalidAuthToken, gameName);
        int statusCode = (int) createGameOutput[0];
        CreateGameResult result = (CreateGameResult) createGameOutput[1];
        assertEquals(401, statusCode);
        assertEquals("Error: unauthorized", result.getMessage());
    }

    @Test
    void listGamesSuccess() {
        assertEquals(0, ((ListGamesResult) gameService.listGames(user1AuthToken)[1]).getGameList().size());
        gameService.createGame(user1AuthToken, "game1");
        gameService.createGame(user1AuthToken, "game2");
        Object[] listGamesOutput = gameService.listGames(user1AuthToken);
        int statusCode = (int) listGamesOutput[0];
        ListGamesResult result = (ListGamesResult) listGamesOutput[1];
        assertEquals(200, statusCode);
        assertNotNull(result.getGameList());
        assertEquals(2, result.getGameList().size());
    }

    @Test
    void listGamesFailUnauthorized() {
        gameService.createGame(user1AuthToken, "game1");
        gameService.createGame(user1AuthToken, "game2");
        Object[] listGamesOutput = gameService.listGames(invalidAuthToken);
        int statusCode = (int) listGamesOutput[0];
        ListGamesResult result = (ListGamesResult) listGamesOutput[1];
        assertEquals(401, statusCode);
        assertEquals("Error: unauthorized", result.getMessage());
    }

    @Test
    void joinGameSuccess() {
        String gameName = "game1";
        Object[] createGameOutput = gameService.createGame(user1AuthToken, gameName);
        int gameID = ((CreateGameResult) createGameOutput[1]).getGameID();

        Object[] joinGameOutput = gameService.joinGame(user1AuthToken, new JoinGameRequest("WHITE", gameID));
        int statusCode = (int) joinGameOutput[0];
        JoinGameResult result = (JoinGameResult) joinGameOutput[1];
        assertEquals(200, statusCode);
        assertEquals(new JoinGameResult(null), result);

        Object[] joinGameOutput2 = gameService.joinGame(user2AuthToken, new JoinGameRequest("BLACK", gameID));
        int statusCode2 = (int) joinGameOutput2[0];
        JoinGameResult result2 = (JoinGameResult) joinGameOutput2[1];
        assertEquals(200, statusCode2);
        assertEquals(new JoinGameResult(null), result2);
    }

    @Test
    void joinGameFailTakenColor() {
        String gameName = "game1";
        Object[] createGameOutput = gameService.createGame(user1AuthToken, gameName);
        int gameID = ((CreateGameResult) createGameOutput[1]).getGameID();

        Object[] joinGameOutput = gameService.joinGame(user1AuthToken, new JoinGameRequest("WHITE", gameID));
        int statusCode = (int) joinGameOutput[0];
        JoinGameResult result = (JoinGameResult) joinGameOutput[1];
        assertEquals(200, statusCode);
        assertEquals(new JoinGameResult(null), result);

        Object[] joinGameOutput2 = gameService.joinGame(user2AuthToken, new JoinGameRequest("WHITE", gameID));
        int statusCode2 = (int) joinGameOutput2[0];
        JoinGameResult result2 = (JoinGameResult) joinGameOutput2[1];
        assertEquals(403, statusCode2);
        assertEquals("Error: already taken", result2.message());
    }

}
