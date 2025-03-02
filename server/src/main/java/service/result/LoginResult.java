package service.result;

public class LoginResult {
    private final int status;
    private final String username;
    private final String authToken;
    private final String message;


    public LoginResult(int status, String username, String authToken) {
        this.status = status;
        this.username = username;
        this.authToken = authToken;
        this.message = null;
    }

    public LoginResult(int status, String message) {
        this.status = status;
        this.message = message;
        this.username = null;
        this.authToken = null;
    }

    public int getStatus() {
        return status;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getMessage() {
        return message;
    }
}
