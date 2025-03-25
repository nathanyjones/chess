import exception.ResponseException;
import model.GameData;
import model.UserData;
import server.ServerFacade;

import java.util.Collection;

public class Main {
    public static void main(String[] args) {
        ServerFacade facade = new ServerFacade("http://localhost:8080");

        UserData user1 = new UserData("testUser1", "password", "test@example.com");
        UserData user2 = new UserData("testUser2", "password", "test@example.com");

        try {
            facade.clear();
            String authToken1 = facade.register(user1).authToken();
            String authToken2 = facade.register(user2).authToken();

            GameData gameData = facade.createGame(authToken1, "Game1");
            int gameID = gameData.gameID();

            facade.joinGame(authToken2, gameID, "WHITE");
            System.out.println("Join Game Did Not Raise Exception");
            Collection<GameData> gameList = facade.listGames(authToken1);
            System.out.println("Game List Acquired. Size of: " + gameList.size());
        } catch (ResponseException e) {
            System.out.println("Request failed: " + e.getMessage());
        }
    }
}