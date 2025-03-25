package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.JoinGameRequest;
import result.ListGamesResult;

import java.io.*;
import java.net.*;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, false, null);
    }

    public AuthData register(UserData user) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, user, AuthData.class, false, null);
    }

    public AuthData login(UserData user) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, user, AuthData.class, false, null);
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, true, authToken);
    }

    public Integer createGame(String authToken, String gameName) throws ResponseException {
        var path = "/game";
        GameData game = this.makeRequest("POST", path, gameName, GameData.class, true, authToken);
        return game.gameID();
    }

    public void joinGame(String authToken, Integer gameID, String playerColor) throws ResponseException {
        var path = "/game";
        JoinGameRequest joinData = new JoinGameRequest(playerColor, gameID);
        this.makeRequest("PUT", path, joinData, null, true, authToken);
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException {
        var path = "/game";
        ListGamesResult result = this.makeRequest("GET", path, null, ListGamesResult.class,
                true, authToken);
        return result.getGameList();
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass,
                              boolean requiresAuth, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (requiresAuth) {
                http.setRequestProperty("authorization", authToken);
            }
            writeBody(request, http);
            int status = http.getResponseCode();
            if (path.equals("/user") && method.equals("POST") && status == 403) {
                throw new ResponseException(403, "Error: username already taken");
            } else if (path.equals("/session") && method.equals("POST") && status == 401) {
                throw new ResponseException(401, "Error: incorrect username or password");
            } else if (path.equals("/game") && method.equals("PUT") && status == 403) {
                throw new ResponseException(403, "Error: Color already taken.");
            }
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }
            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}