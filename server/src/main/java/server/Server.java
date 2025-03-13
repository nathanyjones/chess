package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;
import handler.*;

public class Server {

    public int run(int desiredPort) {
        final DataAccess dataAccess = new MySqlDataAccess();
        final UserService userService = new UserService(dataAccess);
        final GameService gameService = new GameService(dataAccess);
        final ClearService clearService = new ClearService(dataAccess);

        final RegisterHandler registerHandler = new RegisterHandler(userService);
        final LoginHandler loginHandler = new LoginHandler(userService);
        final LogoutHandler logoutHandler = new LogoutHandler(userService);
        final ListGamesHandler listGamesHandler = new ListGamesHandler(gameService);
        final CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
        final ClearHandler clearHandler = new ClearHandler(clearService);
        final JoinGameHandler  joinGameHandler = new JoinGameHandler(gameService);

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", registerHandler);
        Spark.post("/session", loginHandler);
        Spark.delete("/session", logoutHandler);
        Spark.get("/game", listGamesHandler);
        Spark.post("/game", createGameHandler);
        Spark.delete("/db", clearHandler);
        Spark.put("/game", joinGameHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
