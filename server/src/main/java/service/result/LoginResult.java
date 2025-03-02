package service.result;

public class LoginResult extends AuthResult {

    public LoginResult(int status, String username, String authToken) {
        super(status, username, authToken);
    }

    public LoginResult(int status, String message) {
        super(status, message);
    }
}