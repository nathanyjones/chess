package handler;

import com.google.gson.JsonObject;
import service.GameService;
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
        System.out.println(request.body());
        JsonObject jsonObject = fromJSON(request.body(), JsonObject.class);
        String gameName = jsonObject.get("gameName").getAsString();
        Object[] resultArray = gameService.createGame(authToken, gameName);
        response.status((int) resultArray[0]);
        response.body(toJSON(resultArray[1]));
        return response.body();
    }
}
