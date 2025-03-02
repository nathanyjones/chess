package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import service.UserService;
import spark.*;
import handler.*;

public class Server {

    public int run(int desiredPort) {
        final DataAccess dataAccess = new MemoryDataAccess();
        final UserService userService = new UserService(dataAccess);

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", new RegisterHandler(userService));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
