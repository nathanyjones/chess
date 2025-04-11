package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, String> authUsernameMap = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, HashSet<String>> gameAuthMap = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Integer> authGameMap = new ConcurrentHashMap<>();

    public void add(String authToken, String username, int gameID, Session session) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
        authUsernameMap.put(authToken, username);
        gameAuthMap.computeIfAbsent(gameID, k -> new HashSet<>()).add(authToken);
        authGameMap.put(authToken, gameID);
    }

    public String remove(String authToken) {
        int gameID = authGameMap.get(authToken);
        String username = authUsernameMap.get(authToken);
        gameAuthMap.get(gameID).remove(authToken);
        authGameMap.remove(authToken);
        connections.remove(authToken);
        return username;
    }

    public String getUsername(String authToken) {
        return authUsernameMap.get(authToken);
    }

    public void broadcast(String initiatorAuth, ServerMessage message,
                          boolean sendToInitiator) throws IOException {
        var removeList = new ArrayList<Connection>();
        int gameID = authGameMap.get(initiatorAuth);
        HashSet<String> authsToSend = gameAuthMap.get(gameID);
        for (String authToken : authsToSend) {
            var c = connections.get(authToken);
            if (c != null && c.session.isOpen()) {
                if (!c.authToken.equals(initiatorAuth) || sendToInitiator) {
                    c.send(message.toString());
                }
            } else if (c != null) {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }

    public void sendIndividualMessage(String authToken, ServerMessage message) throws IOException {
        Connection c = connections.get(authToken);

        if (c != null && c.session.isOpen()) {
            String jsonMessage = message.toString();
            c.send(jsonMessage);
        } else {
            if (c != null) {
                connections.remove(authToken);
            }
        }
    }

}