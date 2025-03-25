package ui;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import server.ServerFacade;
import java.util.Arrays;
import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private String authToken;
    private boolean loggedIn;

    public ChessClient(String serverURL) {
        this.server = new ServerFacade(serverURL);
        this.loggedIn = false;
    }

    public String eval(String input) throws ResponseException {
        var tokens = input.split(" ");
        var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        if (!this.loggedIn) {
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "Exiting...";
                case "help" -> help();
                default -> SET_TEXT_COLOR_RED + "Unknown Command\n" +
                        "Try one of these:\n" + help();
            };
        } else {
            return switch (cmd) {
                case "create" -> createGame(params);
                case "logout" -> logout();
                case "quit" -> "Exiting...";
                case "help" -> help();
                default -> SET_TEXT_COLOR_RED + "Unknown Command\n" +
                        "Try one of these:\n" + help();
            };
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length < 3) {
            throw new ResponseException(400, "Insufficient information. Must provide " +
                    "<USERNAME> <PASSWORD> and <EMAIL>.");
        }
        try {
            AuthData authData = server.register(new UserData(params[0], params[1], params[2]));
            this.authToken = authData.authToken();
            this.loggedIn = true;
            return "Successfully Registered " + params[0] + "\n" + help();
        } catch (ResponseException e) {
            if (e.StatusCode() == 403) {
                throw e;
            }
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        } catch (Exception e) {
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length < 2) {
            throw new ResponseException(400, "Insufficient information. Must provide <USERNAME> and <PASSWORD>.");
        }
        try {
            AuthData authData = server.login(new UserData(params[0], params[1], null));
            this.authToken = authData.authToken();
            this.loggedIn = true;
            return "Successfully Logged in as " + params[0] + "\n" + help();
        } catch (ResponseException e) {
            if (e.StatusCode() == 401) {
                throw e;
            }
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        } catch (Exception e) {
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    public String logout() throws ResponseException {
        try {
            server.logout(this.authToken);
            this.authToken = null;
            return "Successfully Logged Out";
        } catch (Exception e) {
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length < 1) {
            throw new ResponseException(400, "Insufficient information. Must provide game name <NAME>.\n");
        }
        try {
            String gameName = String.join(" ", params);
            int gameID = server.createGame(this.authToken, gameName);
            return "Game '" + gameName + "' created with id: " + gameID + ".\nUse command 'join " + gameID
                    + " [WHITE|BLACK]' to join this game.\n";
        } catch (Exception e) {
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    public String help() {
        if (this.loggedIn) {
            return getSignedInHelpString();
        } else {
            return getSignedOutHelpString();
        }
    }

    private static String getSignedOutHelpString() {
        String template = """
                \t{{bluet}}register <USERNAME> <PASSWORD> <EMAIL> {{whitet}}- to create an account
                \t{{bluet}}login <USERNAME> <PASSWORD> {{whitet}}- to play chess
                \t{{bluet}}quit {{whitet}}- playing chess
                \t{{bluet}}help {{whitet}}- with possible commands
                """;
        String message = template.replace("{{bluet}}", SET_TEXT_COLOR_BLUE);
        message = message.replace("{{whitet}}", SET_TEXT_COLOR_WHITE);
        return message;
    }

    private static String getSignedInHelpString() {
        String template = """
                    \t{{bluet}}create <NAME> {{whitet}}- a game
                    \t{{bluet}}list {{whitet}}- games
                    \t{{bluet}}join <ID> [WHITE|BLACK] {{whitet}}- a game
                    \t{{bluet}}observe <ID> {{whitet}}- a game
                    \t{{bluet}}logout {{whitet}}- when you are done
                    \t{{bluet}}help {{whitet}}- with possible commands
                    """;
        String message = template.replace("{{bluet}}", SET_TEXT_COLOR_BLUE);
        message = message.replace("{{whitet}}", SET_TEXT_COLOR_WHITE);
        return message;
    }
}
