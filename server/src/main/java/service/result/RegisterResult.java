package service.result;

public class RegisterResult extends AuthResult {

    public RegisterResult(int status, String username, String authToken) {
        super(status, username, authToken);
    }

    public RegisterResult(int status, String message) {
        super(status, message);
    }
}
