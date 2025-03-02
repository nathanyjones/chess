package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import service.request.*;
import service.result.*;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        if (!registerRequest.validateRequest()) {
            return new RegisterResult(400, "Error: bad request");
        }
        UserData userData = new UserData(registerRequest.username(), registerRequest.password(),
                registerRequest.email());
        try {
            dataAccess.createUser(userData);
            String authToken = generateAuthToken();
            dataAccess.createAuth(new AuthData(authToken, userData.username()));
            return new RegisterResult(200, userData.username(), authToken);
        } catch (DataAccessException e) {
            return new RegisterResult(403, "Error: already taken");
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return new RegisterResult(500, "Error: " + e.getMessage());
        }
    }

    public LoginResult login(LoginRequest loginRequest) {
        try {
            dataAccess.getUser(loginRequest.username());
            String authToken = generateAuthToken();
            dataAccess.createAuth(new AuthData(authToken, loginRequest.username()));
            return new LoginResult(200, loginRequest.username(), authToken);
        } catch (DataAccessException e) {
            return new LoginResult(401, "Error: unauthorized");
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return new LoginResult(500, "Error: " + e.getMessage());
        }
    }

    public LogoutResult logout(String authToken) {
        try {
            dataAccess.getAuth(authToken);
            dataAccess.deleteAuth(authToken);
            return new LogoutResult(200, null);
        } catch (DataAccessException e) {
            return new LogoutResult(401, "Error: unauthorized");
        } catch (Exception e) {
            return new LogoutResult(500, "Error: " + e.getMessage());
        }
    }

    private static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

}
