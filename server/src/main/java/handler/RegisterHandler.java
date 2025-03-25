package handler;

import service.UserService;
import service.request.RegisterRequest;
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
        Object[] resultArray = userService.register(registerRequest);
        response.status((int) resultArray[0]);
        response.body(toJSON(resultArray[1]));
        System.out.println(response.body());
        return response.body();
    }
}
