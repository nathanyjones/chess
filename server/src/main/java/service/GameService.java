package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import service.result.ListGamesResult;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Object[] listGames(String authToken) {
        try {
            dataAccess.getAuth(authToken);
            return new Object[] {200, new ListGamesResult(dataAccess.listGames())};
        } catch (DataAccessException e) {
            return new Object[] {401, new ListGamesResult("Error: unauthorized")};
        } catch (Exception e) {
            return new Object[] {500, new ListGamesResult("Error: " + e.getMessage())};
        }
    }

//    public Object[] login(LoginRequest loginRequest) {
//        if (!loginRequest.validateRequest()) {
//            return new Object[] {400, new RegisterResult("Error: bad request")};
//        }
//        try {
//            dataAccess.getUser(loginRequest.username());
//            String authToken = generateAuthToken();
//            dataAccess.createAuth(new AuthData(authToken, loginRequest.username()));
//            return new Object[] {200, new LoginResult(loginRequest.username(), authToken)};
//        } catch (DataAccessException e) {
//            return new Object[] {401, new LoginResult("Error: unauthorized")};
//        } catch (Exception e) {
//            System.err.println("Unexpected Error: " + e.getMessage());
//            return new Object[] {500, new LoginResult("Error: " + e.getMessage())};
//        }
//    }
//
//    public Object[] logout(String authToken) {
//        try {
//            dataAccess.getAuth(authToken);
//            dataAccess.deleteAuth(authToken);
//            return new Object[] {200, new LogoutResult(null)};
//        } catch (DataAccessException e) {
//            return new Object[] {401, new LogoutResult("Error: unauthorized")};
//        } catch (Exception e) {
//            return new Object[] {500, new LogoutResult("Error: " + e.getMessage())};
//        }
//    }
}

