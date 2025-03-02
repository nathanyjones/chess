package service.result;

public abstract class AuthResult {
    private final int status;
    private final String username;
    private final String authToken;
    private final String message;

    public AuthResult(int status, String username, String authToken) {
        this.status = status;
        this.username = username;
        this.authToken = authToken;
        this.message = null;
    }

    public AuthResult(int status, String message) {
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