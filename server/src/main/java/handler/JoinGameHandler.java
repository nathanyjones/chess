package handler;

import service.GameService;
import service.request.JoinGameRequest;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler extends Handler implements Route  {

    final private GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public String handle(Request request, Response response) {
        String authToken = request.headers("authorization");
        JoinGameRequest joinGameRequest = fromJSON(request.body(), JoinGameRequest.class);
        Object[] resultArray = gameService.joinGame(authToken, joinGameRequest);
        response.status((int) resultArray[0]);
        response.body(toJSON(resultArray[1]));
        return response.body();
    }
}
