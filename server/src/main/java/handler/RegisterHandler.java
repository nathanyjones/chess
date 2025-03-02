package handler;

import service.UserService;
import service.request.RegisterRequest;
import service.result.RegisterResult;
import spark.Route;
import spark.Request;
import spark.Response;

public class RegisterHandler extends Handler implements Route  {

    final private UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public String handle(Request request, Response response) {
        String body = request.body();
        RegisterRequest registerRequest = this.fromJSON(body, RegisterRequest.class);
        RegisterResult result = userService.register(registerRequest);
        response.status(result.getStatus());
        response.body(toJSON(result));
        return response.body();
    }
}
