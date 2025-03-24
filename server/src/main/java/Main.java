import chess.*;
import exception.ResponseException;
import model.UserData;
import server.Server;
import server.ServerFacade;

public class Main {
    public static void main(String[] args) {
        Server chessServer = new Server();
        chessServer.run(8080);

    }
}