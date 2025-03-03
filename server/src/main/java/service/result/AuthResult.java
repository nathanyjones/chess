package service.result;

public abstract class AuthResult {
    private final String username;
    private final String authToken;
    private final String message;

    public AuthResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
        this.message = null;
    }

    public AuthResult(String message) {
        this.message = message;
        this.username = null;
        this.authToken = null;
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