package handler;

import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler extends Handler implements Route  {

    final private ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public String handle(Request request, Response response) {
        Object[] resultArray = clearService.clear();
        response.status((int) resultArray[0]);
        response.body(toJSON(resultArray[1]));
        return response.body();
    }
}
