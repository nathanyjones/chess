package service.request;

public record RegisterRequest(String username, String password, String email) {

    public boolean validateRequest() {
        return this.username != null && !this.username.isEmpty() &&
                this.password != null && !this.password.isEmpty() &&
                this.email != null && !this.email.isEmpty();
    }

}
