package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import service.request.*;
import service.result.LoginResult;
import service.result.RegisterResult;
import model.UserData;

import java.util.UUID;
//import service.request.

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        UserData userData = new UserData(registerRequest.username(), registerRequest.password(),
                registerRequest.email());
        try {
            dataAccess.createUser(userData);
            String authToken = generateAuthToken();
            dataAccess.createAuth(new AuthData(authToken, userData.username()));
            return new RegisterResult(200, userData.username(), authToken);
        } catch (DataAccessException e) {
            return new RegisterResult(403, e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return new RegisterResult(500, e.getMessage());
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
            return new LoginResult(500, e.getMessage());
        }
    }



//    public void logout(LogoutRequest logoutRequest) {
//    }

    private static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
