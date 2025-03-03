package service.result;

public class LoginResult extends AuthResult {

    public LoginResult(String username, String authToken) {
        super(username, authToken);
    }

    public LoginResult(String message) {
        super(message);
    }
}