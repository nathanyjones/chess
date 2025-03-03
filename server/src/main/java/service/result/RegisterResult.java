package service.result;

public class RegisterResult extends AuthResult {

    public RegisterResult(String username, String authToken) {
        super(username, authToken);
    }

    public RegisterResult(String message) {
        super(message);
    }
}
