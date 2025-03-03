package service.request;

public record LoginRequest(String username, String password) {
    public boolean validateRequest() {
        return this.username != null && !this.username.isEmpty() &&
                this.password != null && !this.password.isEmpty();
    }
}
