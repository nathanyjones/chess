package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;
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
        if (!loginRequest.validateRequest()) {
            return new Object[] {400, new RegisterResult("Error: bad request")};
        }
        try {
            UserData userData = dataAccess.getUser(loginRequest.username());
            var hashedPassword = userData.password();
            if (BCrypt.checkpw(loginRequest.password(), hashedPassword)) {
                String authToken = generateAuthToken();
                dataAccess.createAuth(new AuthData(authToken, loginRequest.username()));
                return new Object[] {200, new LoginResult(loginRequest.username(), authToken)};
            }
            return new Object[] {401, new LoginResult("Error: unauthorized")};
        } catch (DataAccessException e) {
            if (e.getMessage().contains(" not found") ||
                    e.getMessage().contains("Error: AuthToken already exists")) {
                return new Object[] {401, new LoginResult("Error: unauthorized")};
            } else {
                return new Object[] {500, new LoginResult("Error: " + e.getMessage())};
            }
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return new Object[] {500, new LoginResult("Error: " + e.getMessage())};
        }
    }

    public Object[] logout(String authToken) {
        try {
            System.out.println("Getting auth token");
            dataAccess.getAuth(authToken);
            System.out.println("Deleting auth token");
            dataAccess.deleteAuth(authToken);
            System.out.println("Auth token deleted!");
            return new Object[] {200, new LogoutResult(null)};
        } catch (DataAccessException e) {
            if (e.getMessage().contains(" not found")) {
                return new Object[] {401, new LogoutResult("Error: unauthorized")};
            } else {
                return new Object[] {500, new LogoutResult("Error: " + e.getMessage())};
            }
        } catch (Exception e) {
            return new Object[] {500, new LogoutResult("Error: " + e.getMessage())};
        }
    }

    private static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

}
