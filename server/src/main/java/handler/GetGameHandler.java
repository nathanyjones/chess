package handler;

import service.GameService;
import service.request.GetGameRequest;
import service.request.JoinGameRequest;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetGameHandler extends Handler implements Route  {

    final private GameService gameService;

    public GetGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public String handle(Request request, Response response) {
        String authToken = request.headers("authorization");
        Integer gameID = Integer.parseInt(request.params("gameID"));
        Object[] resultArray = gameService.getGame(authToken, gameID);
        response.status((int) resultArray[0]);
        response.body(toJSON(resultArray[1]));
        System.out.println("This is the response body:");
        System.out.println(response.body());
        System.out.println("Status: " + response.status());
        return response.body();
    }
}
