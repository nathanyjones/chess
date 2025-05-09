package server;

import dataaccess.DataAccess;
import dataaccess.MySqlDataAccess;
import dataaccess.ResponseException;
import server.websocket.WebSocketHandler;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;
import handler.*;

public class Server {

    public int run(int desiredPort) {
        final DataAccess dataAccess;
        try {
            dataAccess = new MySqlDataAccess();
        } catch (ResponseException e) {
            System.err.println("Could not initialize MySQLDataAccess: " + e.getMessage());
            return -1;
        }
        final UserService userService = new UserService(dataAccess);
        final GameService gameService = new GameService(dataAccess);
        final ClearService clearService = new ClearService(dataAccess);

        final RegisterHandler registerHandler = new RegisterHandler(userService);
        final LoginHandler loginHandler = new LoginHandler(userService);
        final LogoutHandler logoutHandler = new LogoutHandler(userService);
        final ListGamesHandler listGamesHandler = new ListGamesHandler(gameService);
        final CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
        final ClearHandler clearHandler = new ClearHandler(clearService);
        final JoinGameHandler joinGameHandler = new JoinGameHandler(gameService);
        final GetGameHandler getGameHandler = new GetGameHandler(gameService);
        final UpdateBoardHandler updateBoardHandler = new UpdateBoardHandler(gameService);
        final WebSocketHandler webSocketHandler = new WebSocketHandler(userService, gameService);

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

        Spark.post("/user", registerHandler);
        Spark.post("/session", loginHandler);
        Spark.delete("/session", logoutHandler);
        Spark.get("/game", listGamesHandler);
        Spark.post("/game", createGameHandler);
        Spark.delete("/db", clearHandler);
        Spark.put("/game", joinGameHandler);
        Spark.put("/game/:gameID", updateBoardHandler);
        Spark.get("/game/:gameID", getGameHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
