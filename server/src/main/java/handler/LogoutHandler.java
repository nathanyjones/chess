package handler;

import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler extends Handler implements Route  {

    final private UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public String handle(Request request, Response response) {
        System.out.println("Request: " + request);
        String authToken = request.headers("authorization");
        Object[] resultArray = userService.logout(authToken);
        response.status((int) resultArray[0]);
        response.body(toJSON(resultArray[1]));
        return response.body();
    }
}
