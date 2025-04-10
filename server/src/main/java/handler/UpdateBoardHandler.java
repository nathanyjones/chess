package handler;

import request.UpdateBoardRequest;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class UpdateBoardHandler extends Handler implements Route  {

    final private GameService gameService;

    public UpdateBoardHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public String handle(Request request, Response response) {
        String authToken = request.headers("authorization");
        Integer gameID = Integer.parseInt(request.params("gameID"));
        UpdateBoardRequest updateBoardRequest = fromJSON(request.body(), UpdateBoardRequest.class);
        Object[] resultArray = gameService.updateBoard(authToken, gameID, updateBoardRequest);
        response.status((int) resultArray[0]);
        response.body(toJSON(resultArray[1]));
        return response.body();
    }
}
