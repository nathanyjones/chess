package handler;

import service.GameService;
import service.request.CreateGameRequest;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler extends Handler implements Route  {

    final private GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public String handle(Request request, Response response) {
        String authToken = request.headers("authorization");
        CreateGameRequest createGameRequest = fromJSON(request.body(), CreateGameRequest.class);
        Object[] resultArray = gameService.createGame(authToken, createGameRequest.gameName());
        response.status((int) resultArray[0]);
        response.body(toJSON(resultArray[1]));
        return response.body();
    }
}
