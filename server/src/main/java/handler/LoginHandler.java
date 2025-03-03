package handler;

import service.UserService;
import service.request.LoginRequest;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler extends Handler implements Route  {

    final private UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public String handle(Request request, Response response) {
        String body = request.body();
        LoginRequest loginRequest = this.fromJSON(body, LoginRequest.class);
        Object[] resultArray = userService.login(loginRequest);
        response.status((int) resultArray[0]);
        response.body(toJSON(resultArray[1]));
        return response.body();
    }
}
