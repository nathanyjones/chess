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
            Integer gameID = facade.createGame(authToken1, "Game1");
            System.out.println("About to get game: " + gameID);
            GameData gameData = facade.getGame(authToken1, gameID);
            System.out.println("The retrieved game name is: " + gameData.gameName());
            System.out.println(gameData.toString());
        } catch (ResponseException e) {
            System.out.println("Request failed: " + e.getMessage());
        }
    }
}