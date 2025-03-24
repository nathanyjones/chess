import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;

public class Main {
    public static void main(String[] args) {
        ServerFacade facade = new ServerFacade("http://localhost:8080");

        UserData user = new UserData("testUser2", "password", "test@example.com");

        try {
            facade.clear();
            AuthData response = facade.register(user);
            System.out.println("Registration Successful! Received: " + response);
            AuthData loginResponse = facade.login(user);
            String authToken = loginResponse.authToken();
            System.out.println("Login Successful! Received: " + loginResponse);
            System.out.println(authToken);
//            facade.logout(authToken);
//            System.out.println("Logout Successful!");
            GameData gameData = facade.createGame(authToken, "Game1");
            System.out.println("Game created successfully: " + gameData.gameID());
        } catch (ResponseException e) {
            System.out.println("Request failed: " + e.getMessage());
        }
    }
}