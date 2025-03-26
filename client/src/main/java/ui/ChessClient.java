package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;

import java.util.ArrayList;
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
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "list" -> listGames();
                case "logout" -> logout();
                case "quit" -> "Exiting...";
                case "help" -> help();
                default -> SET_TEXT_COLOR_RED + "Unknown Command\n" +
                        "Try one of these:\n" + help();
            };
        }
    }

    private String register(String... params) throws ResponseException {
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

    private String login(String... params) throws ResponseException {
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

    private String logout() throws ResponseException {
        try {
            server.logout(this.authToken);
            this.authToken = null;
            return "Successfully Logged Out";
        } catch (Exception e) {
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    private String createGame(String... params) throws ResponseException {
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

    private String joinGame(String... params) throws ResponseException {
        if (params.length < 2) {
            throw new ResponseException(400, "Insufficient information. Must provide game ID <ID> and " +
                    "color [WHITE|BLACK].\n");
        }
        String color = params[1];
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new ResponseException(400, "Invalid color type. Color must be either WHITE or BLACK");
        }
        try {
            int gameID = Integer.parseInt(params[0]);
            server.joinGame(this.authToken, gameID, color);
            return "Game " + gameID + " joined as " + color + ".\n\n" + drawBoard(gameID, color);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Invalid GameID. Please provide a valid GameID (number).\nUse command " +
                     SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_RED + " to view joinable games, or " +
                    SET_TEXT_COLOR_BLUE + "create <NAME>" + SET_TEXT_COLOR_RED + " to create your own game.");
        } catch (ResponseException e) {
            if (e.StatusCode() == 403) {
                throw new ResponseException(403, "Color already taken by another user.");
            }
            throw new ResponseException(401, "Game not found. Game ID may be invalid or expired.");
        } catch (Exception e) {
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    private String observeGame(String... params) throws ResponseException {
        if (params.length < 1) {
            throw new ResponseException(400, "Invalid color type. Color must be either WHITE or BLACK");
        }
        try {
            int gameID = Integer.parseInt(params[0]);
            String boardDrawing = drawBoard(gameID, "WHITE");
            return "Joined game " + gameID + " as a spectator.\n\n" + boardDrawing;
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Invalid GameID. Note that <ID> should be an integer.\nUse command " +
                    SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_RED + " to view joinable games, or " +
                    SET_TEXT_COLOR_BLUE + "create <NAME>" + SET_TEXT_COLOR_RED + " to create your own game.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    private String drawBoard(int gameID, String color) throws ResponseException {
        StringBuilder boardDrawing = new StringBuilder();
        GameData gameData;
        try {
            gameData = server.getGame(this.authToken, gameID);
        } catch (Exception e) {
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }

        ChessBoard board = gameData.game().getBoard();

        for (int i = 1; i < 9; i += 1) {
            int row = color.equals("BLACK") ? 9-i : i;
            for (int j = 1; j < 9; j++) {
                int col = color.equals("BLACK") ? 9-j : j;
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if ((i + j) % 2 == 0) {
                    boardDrawing.append(SET_BG_COLOR_LIGHT_GREY);
                } else {
                    boardDrawing.append(SET_BG_COLOR_BLACK);
                }

                if (piece != null) {
                    ChessPiece.PieceType pieceType = piece.getPieceType();
                    ChessGame.TeamColor pieceColor = piece.getTeamColor();

                    if (pieceColor == ChessGame.TeamColor.WHITE) {
                        boardDrawing.append(SET_TEXT_COLOR_RED);
                    } else {
                        boardDrawing.append(SET_TEXT_COLOR_WHITE);
                    }
                    if (pieceType == ChessPiece.PieceType.PAWN) {
                        boardDrawing.append(" P ");
                    } else if (pieceType == ChessPiece.PieceType.ROOK) {
                        boardDrawing.append(" R ");
                    } else if (pieceType == ChessPiece.PieceType.BISHOP) {
                        boardDrawing.append(" B ");
                    } else if (pieceType == ChessPiece.PieceType.QUEEN) {
                        boardDrawing.append(" Q ");
                    } else if (pieceType == ChessPiece.PieceType.KING) {
                        boardDrawing.append(" K ");
                    } else if (pieceType == ChessPiece.PieceType.KNIGHT) {
                        boardDrawing.append(" N ");
                    }
                } else {
                    boardDrawing.append("   ");
                }
            }
            boardDrawing.append(RESET + "\n");
        }
        return boardDrawing.toString();
    }

    private String listGames() throws ResponseException {
        StringBuilder printedList = new StringBuilder();
        try {
            ArrayList<GameData> gameList = (ArrayList<GameData>) server.listGames(this.authToken);
            for (int i = 1; i <= gameList.size(); i++) {
                String gameInfo = getGameInfoString(gameList, i);
                printedList.append(gameInfo);
            }
            return printedList.toString();
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private static String getGameInfoString(ArrayList<GameData> gameList, int i) {
        GameData gameData = gameList.get(i-1);
        String gameName = gameData.gameName();
        int gameID = gameData.gameID();
        String whitePlayer = gameData.whiteUsername();
        whitePlayer = whitePlayer == null ? "None" : whitePlayer;
        String blackPlayer = gameData.blackUsername();
        blackPlayer = blackPlayer == null ? "None" : blackPlayer;
        return """
               %d.
               \t%s:
               \t\tID: %d
               \t\tUser Playing White: %s
               \t\tUser Playing Black: %s
               """.formatted(i, gameName, gameID, whitePlayer, blackPlayer);
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
