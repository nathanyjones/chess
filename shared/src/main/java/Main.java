import exception.ResponseException;
import model.AuthData;
import model.UserData;
import server.ServerFacade;

public class Main {
    public static void main(String[] args) {
        ServerFacade facade = new ServerFacade("http://localhost:8080");

        UserData user = new UserData("testUser2", "password", "test@example.com");

        try {
            facade.clear();
            AuthData response = facade.register(user);
            AuthData loginResponse = facade.login(user);
            System.out.println("Registration Successful! Received: " + response);
            System.out.println("Login Successful! Received: " + loginResponse);
        } catch (ResponseException e) {
            System.out.println("Request failed: " + e.getMessage());
        }
    }
}