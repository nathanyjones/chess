import exception.ResponseException;
import server.ServerFacade;

public class Main {
    public static void main(String[] args) {
        ServerFacade facade = new ServerFacade("http://localhost:8080");

        try {
            facade.clear();
        } catch (ResponseException e) {
            System.out.println("Request failed: " + e.getMessage());
        }
    }
}