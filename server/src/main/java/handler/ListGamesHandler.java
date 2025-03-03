package handler;

import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler extends Handler implements Route  {

    final private GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public String handle(Request request, Response response) {
        String authToken = request.headers("authorization");
        Object[] resultArray = gameService.listGames(authToken);
        response.status((int) resultArray[0]);
        response.body(toJSON(resultArray[1]));
        return response.body();
    }
}
