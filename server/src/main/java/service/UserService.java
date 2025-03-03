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

    public Object[] register(RegisterRequest registerRequest) {
        if (!registerRequest.validateRequest()) {
            return new Object[] {400, new RegisterResult("Error: bad request")};
        }
        UserData userData = new UserData(registerRequest.username(), registerRequest.password(),
                registerRequest.email());
        try {
            dataAccess.createUser(userData);
            String authToken = generateAuthToken();
            dataAccess.createAuth(new AuthData(authToken, userData.username()));
            return new Object[] {200, new RegisterResult(userData.username(), authToken)};
        } catch (DataAccessException e) {
            return new Object[] {403, new RegisterResult("Error: already taken")};
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return new Object[] {500, new RegisterResult("Error: " + e.getMessage())};
        }
    }

    public Object[] login(LoginRequest loginRequest) {
        try {
            dataAccess.getUser(loginRequest.username());
            String authToken = generateAuthToken();
            dataAccess.createAuth(new AuthData(authToken, loginRequest.username()));
            return new Object[] {200, new LoginResult(loginRequest.username(), authToken)};
        } catch (DataAccessException e) {
            return new Object[] {401, new LoginResult("Error: unauthorized")};
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return new Object[] {500, new LoginResult("Error: " + e.getMessage())};
        }
    }

    public Object[] logout(String authToken) {
        try {
            dataAccess.getAuth(authToken);
            dataAccess.deleteAuth(authToken);
            return new Object[] {200, new LogoutResult(null)};
        } catch (DataAccessException e) {
            return new Object[] {401, new LogoutResult("Error: unauthorized")};
        } catch (Exception e) {
            return new Object[] {500, new LogoutResult("Error: " + e.getMessage())};
        }
    }

    private static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

}
