package ui;

import chess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;
import java.util.*;
import static ui.EscapeSequences.*;
import static ui.HelpStringHelper.*;

public class ChessClient {
    private final ServerFacade server;
    private String authToken;
    private String username;
    private boolean loggedIn = false;
    private boolean playingGame = false;
    private boolean observingGame = false;
    private int gameID;
    private ChessGame game;
    private String playerColor;
    private final NotificationHandler notificationHandler;
    private final String serverURL;
    private WebSocketFacade ws;

    public ChessClient(String serverURL, NotificationHandler notificationHandler) {
        this.server = new ServerFacade(serverURL);
        this.notificationHandler = notificationHandler;
        this.serverURL = serverURL;
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
        } else if (this.playingGame) {
            return switch (cmd) {
                case "redraw" -> drawBoard(playerColor);
                case "leave" -> leaveGame();
                case "move" -> makeMove(params);
                case "resign" -> resign();
                case "show" -> showMoves(params);
                case "help" -> help();
                default -> SET_TEXT_COLOR_RED + "Unknown Command\n" +
                        "Try one of these:\n" + help();
            };
        } else if (this.observingGame) {
            return switch (cmd) {
                case "redraw" -> drawBoard("WHITE");
                case "leave" -> leaveGame();
                case "show" -> showMoves(params);
                case "help" -> help();
                default -> SET_TEXT_COLOR_RED + "Unknown Command\n" +
                        "Try one of these:\n" + help();
            };
        } else {
            return switch (cmd) {
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "list" -> listGames(params);
                case "logout" -> logout();
                case "quit" -> "Exiting...";
                case "help" -> help();
                default -> SET_TEXT_COLOR_RED + "Unknown Command\n" +
                        "Try one of these:\n" + help();
            };
        }
    }

    private String register(String... params) throws ResponseException {
        if (params.length != 3) {
            throw new ResponseException(400, "Invalid input. Must provide " +
                    "<USERNAME> <PASSWORD> and <EMAIL>.");
        }
        try {
            AuthData authData = server.register(new UserData(params[0], params[1], params[2]));
            this.authToken = authData.authToken();
            this.loggedIn = true;
            this.username = params[0];
            return "Successfully Registered " + params[0] + "\n" + help();
        } catch (ResponseException e) {
            if (e.getStatusCode() == 403) {
                throw e;
            }
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        } catch (Exception e) {
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    private String login(String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Invalid input. Must provide <USERNAME> and <PASSWORD>.");
        }
        try {
            AuthData authData = server.login(new UserData(params[0], params[1], null));
            this.authToken = authData.authToken();
            this.username = params[0];
            this.loggedIn = true;
            return "Successfully Logged in as " + params[0] + "\n" + help();
        } catch (ResponseException e) {
            if (e.getStatusCode() == 401) {
                throw e;
            }
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        } catch (Exception e) {
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    private String logout(String... params) throws ResponseException {
        if (params.length != 0) {
            throw new ResponseException(400, "Invalid input. Do not provide additional input for " +
                    SET_TEXT_COLOR_BLUE + "logout" + SET_TEXT_COLOR_RED + " command.");
        }
        try {
            server.logout(this.authToken);
            this.authToken = null;
            this.loggedIn = false;
            return "Successfully Logged Out";
        } catch (Exception e) {
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    private String createGame(String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Invalid input. Must provide game name <NAME>.");
        }
        try {
            String gameName = String.join(" ", params);
            int gameID = server.createGame(this.authToken, gameName);
            return "Game '" + gameName + "' created with id: " + gameID + ".\nUse command " + SET_TEXT_COLOR_BLUE +
                    " join " + gameID + " [WHITE|BLACK] " + SET_TEXT_COLOR_YELLOW + " to join this game.";
        } catch (Exception e) {
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    private String joinGame(String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Invalid input. Must provide game ID <ID> and " +
                    "color [WHITE|BLACK].");
        }
        String color = params[1].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new ResponseException(400, "Invalid color type. Color must be either " +
                    SET_TEXT_COLOR_BLUE + "WHITE " + SET_TEXT_COLOR_RED + "or " + SET_TEXT_COLOR_BLUE + "BLACK");
        }
        try {
            int gameID = Integer.parseInt(params[0]);
            server.joinGame(this.authToken, gameID, color);
            this.playerColor = color;
            this.gameID = gameID;
            this.playingGame = true;
            GameData gameData = server.getGame(this.authToken, gameID);
            this.game = gameData.game();
            this.ws = new WebSocketFacade(this.serverURL, this.notificationHandler);
            ws.joinGameAsPlayer(authToken, gameID);
            return "Game " + gameID + " joined as " + color;
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Invalid GameID. Please provide a valid GameID (number).\nUse command " +
                     SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_RED + " to view joinable games, or " +
                    SET_TEXT_COLOR_BLUE + "create <NAME>" + SET_TEXT_COLOR_RED + " to create your own game.");
        } catch (ResponseException e) {
            if (e.getStatusCode() == 403) {
                throw new ResponseException(403, "Color already taken by another user.");
            }
            throw new ResponseException(401, "Game not found. Provided game ID may be invalid or expired.");
        } catch (Exception e) {
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    private String observeGame(String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Invalid input. Please provide a game ID " +
                    SET_TEXT_COLOR_BLUE + "ID" + SET_TEXT_COLOR_RED + ".");
        }
        try {
            int gameID = Integer.parseInt(params[0]);
            this.observingGame = true;
            this.gameID = gameID;
            this.playerColor = "WHITE";
            this.game = server.getGame(this.authToken, gameID).game();
            this.ws = new WebSocketFacade(this.serverURL, this.notificationHandler);
            ws.joinGameAsObserver(authToken, gameID);
            return "Joined game " + gameID + " as an observer.";
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Invalid GameID. Note that <ID> should be an integer.\nUse command " +
                    SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_RED + " to view joinable games, or " +
                    SET_TEXT_COLOR_BLUE + "create <NAME>" + SET_TEXT_COLOR_RED + " to create your own game.");
        } catch (ResponseException e) {
            throw new ResponseException(401, "Game not found. Provided game ID may be invalid or expired.");
        } catch (Exception e) {
            throw new ResponseException(500, "Internal Server Error. Check your internet connection and try again.");
        }
    }

    private String drawBoard(String color) throws ResponseException {
        return drawBoardWithHighlightedSquares(color, "", new HashSet<>());
    }

    private String listGames(String... params) throws ResponseException {
        if (params.length != 0) {
            throw new ResponseException(400, "Invalid input. Do not provide additional input for " +
                    SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_RED + " command.");
        }
        StringBuilder printedList = new StringBuilder();
        try {
            ArrayList<GameData> gameList = (ArrayList<GameData>) server.listGames(this.authToken);
            if (gameList.isEmpty()) {
                return "There are currently no active games.\nUse command " + SET_TEXT_COLOR_BLUE + "create <NAME> " +
                        SET_TEXT_COLOR_YELLOW + "to create a game.";
            }
            for (int i = 1; i <= gameList.size(); i++) {
                String gameInfo = getGameInfoString(gameList, i);
                if (i != gameList.size()) {
                    gameInfo += "\n";
                }
                printedList.append(gameInfo);
            }
            return printedList.toString();
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private String leaveGame() throws ResponseException {
        try {
            if (this.playingGame) {
                ws.leaveGame(authToken, this.gameID, true);
            } else if (this.observingGame) {
                ws.leaveGame(authToken, this.gameID, false);
            }
            this.playingGame = false;
            this.observingGame = false;
            return "Left game '" + gameID + "' successfully.";
        } catch (Exception e) {
            throw new ResponseException(500, "Unable to leave game. Check your internet connection and try again.");
        }
    }

    private String makeMove(String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Invalid input. Must provide start position <[a-h][1-8]> and end " +
                    "position <[a-h][1-8]>.\n Use command " + SET_TEXT_COLOR_BLUE + "show <POSITION>" +
                    SET_TEXT_COLOR_RED + " to see legal moves for a piece.");
        }
        try {
            ChessMove move = parseChessMove(params);
            ws.makeMove(this.authToken, this.gameID, move);

            if (this.game.getGameOver()) {
                String winner = this.game.getWinner();
                return gameOver(winner);
            } else {
                return "";
            }
        } catch (Exception e) {
            if (e.getClass() == ResponseException.class && (e.getMessage().contains("<[a-h][1-8]>") ||
                    e.getMessage().contains("Not your turn."))) {
                throw e;
            } else {
                throw new ResponseException(500, "Internal Server Error. Check your internet " +
                        "connection and try again.");
            }
        }
    }

    private String resign() throws ResponseException{
        Scanner scanner = new Scanner(System.in);
        System.out.println(SET_TEXT_COLOR_YELLOW + "Are you sure you want to resign?" + SET_TEXT_COLOR_BLUE +
                "[YES|NO]");
        System.out.print("\n" + RESET + ">>> " + SET_TEXT_COLOR_GREEN);
        if (scanner.nextLine().equals("YES")) {
            try {
                ws.resign(authToken, gameID, username, playerColor);
                String winner = playerColor.equals("WHITE") ? "BLACK" : "WHITE";
                return gameOver(winner);
            } catch (ResponseException e) {
                throw new ResponseException(500, "Internal Server Error.");
            }
        }
        return "";
    }

    private String gameOver(String winner) {
        if (winner.equals("WHITE")) {
            return "White wins!";
        } else if (winner.equals("BLACK")) {
            return "Black wins!";
        } else {
            return "It's a Draw!";
        }
    }

    private ChessMove parseChessMove(String... moveStrings) throws ResponseException {
        ChessPosition[] positions = new ChessPosition[2];
        for (int i = 0; i < 2; i++) {
            positions[i] = parseChessPosition(moveStrings[i]);
        }
        ChessPosition startPosition;
        ChessPosition endPosition;
        if (playerColor.equals("BLACK")) {
            startPosition = new ChessPosition(positions[0].getRow(), 9-positions[0].getColumn());
            endPosition = new ChessPosition(positions[1].getRow(), 9-positions[1].getColumn());
        } else {
            startPosition = new ChessPosition(9 - positions[0].getRow(), positions[0].getColumn());
            endPosition = new ChessPosition(9 - positions[1].getRow(), positions[1].getColumn());
        }
        return new ChessMove(startPosition, endPosition, null);
    }

    private ChessPosition parseChessPosition(String moveString) throws ResponseException {
        String validColLabel = "abcdefgh";
        String validRowLabel = "12345678";
        if (moveString.length() != 2 || !validColLabel.contains(moveString.substring(0, 1)) ||
                !validRowLabel.contains(moveString.substring(1))) {
            throw new ResponseException(400, "Invalid input. Must provide valid position <[a-h][1-8]>");
        }
        int tempRow = Integer.parseInt(moveString.substring(1));
        int tempCol = 8 - moveString.charAt(0) + 'a';
        int row = playerColor.equals("BLACK") ? tempRow : 9 - tempRow;
        int col = playerColor.equals("BLACK") ? tempCol : 9 - tempCol;
        return new ChessPosition(row, col);
    }

    private String showMoves(String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Invalid input. Must provide valid position <[a-h][1-8]>");
        }
        try {
            ChessPosition position = parseChessPosition(params[0]);
            Collection<ChessMove> validMoves;
            if (playerColor.equals("BLACK")) {
                validMoves = this.game.validMoves(new ChessPosition(position.getRow(), 9 - position.getColumn()));
            } else {
                validMoves = this.game.validMoves(new ChessPosition(9 - position.getRow(), position.getColumn()));
            }
            for (ChessMove move : validMoves) {
                System.out.println(move);
            }
            HashSet<String> highlightedSquarePositions = new HashSet<>();
            for (ChessMove move : validMoves) {
                ChessPosition endPosition = move.getEndPosition();
                if (playerColor.equals("BLACK")) {
                    highlightedSquarePositions.add("" + (9 - endPosition.getRow()) + (9 - endPosition.getColumn()));
                } else {
                    highlightedSquarePositions.add("" + endPosition.getRow() + endPosition.getColumn());
                }
            }
            String startString = "" + position.getRow() + position.getColumn();
            return drawBoardWithHighlightedSquares(playerColor, startString, highlightedSquarePositions);
        } catch (Exception e) {
            if (e.getClass() == ResponseException.class && e.getMessage().contains("<[a-h][1-8]>")) {
                throw e;
            } else {
                throw new ResponseException(500, "Internal Server Error. Check your internet " +
                        "connection and try again.");
            }
        }
    }

    private String drawBoardWithHighlightedSquares(String color,String startPosition,Set<String> highlightedSquares) {
        StringBuilder boardDrawing = new StringBuilder();
        ChessBoard board = this.game.getBoard();
        boolean highlightSquare;
        for (int i = 0; i < 10; i += 1) {
            int row = color.equals("BLACK") ? 9-i : i;
            for (int j = 0; j < 10; j++) {
                highlightSquare = highlightedSquares.contains("" + (9-i) + j);
                int col = color.equals("BLACK") ? 9-j : j;
                int colLabelInt = color.equals("BLACK") ? i : (9-i);
                boardDrawing.append(SET_BG_COLOR_DARK_GREY);
                boardDrawing.append(SET_TEXT_COLOR_LIGHT_GREY);
                if ((j == 0 || j == 9) && i > 0 && i < 9) {
                    String rowLabel = " " + colLabelInt + " ";
                    boardDrawing.append(rowLabel);
                    continue;
                } else if ((i == 0 || i == 9) && j > 0 && j < 9) {
                    char colLabelChar = (char) ('a' + col - 1);
                    String colLabel = " " + colLabelChar + " ";
                    boardDrawing.append(colLabel);
                    continue;
                } else if (i == 0 || i == 9) {
                    boardDrawing.append("   ");
                    continue;
                }
                ChessPosition position = new ChessPosition(9-row, col);
                ChessPiece piece = board.getPiece(position);
                if ((i + j) % 2 == 0) {
                    if (highlightSquare) {
                        boardDrawing.append(SET_BG_COLOR_GREEN);
                    } else {
                        boardDrawing.append(SET_BG_COLOR_LIGHT_GREY);
                    }
                } else {
                    if (highlightSquare) {
                        boardDrawing.append(SET_BG_COLOR_DARK_GREEN);
                    } else {
                        boardDrawing.append(SET_BG_COLOR_BLACK);
                    }
                }
                if (startPosition.equals("" + i + j)) {
                    boardDrawing.append(SET_BG_COLOR_MAGENTA);
                }
                if (piece != null) {
                    ChessPiece.PieceType pieceType = piece.getPieceType();
                    ChessGame.TeamColor pieceColor = piece.getTeamColor();
                    if (pieceColor == ChessGame.TeamColor.WHITE) {
                        boardDrawing.append(SET_TEXT_COLOR_WHITE);
                    } else {
                        boardDrawing.append(SET_TEXT_COLOR_RED);
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
               \t\tUser Playing Black: %s""".formatted(i, gameName, gameID, whitePlayer, blackPlayer);
    }

    public String help() {
        if (this.playingGame) {
            return getPlayingGameHelpString();
        } else if (this.observingGame) {
            return getObservingGameHelpString();
        } else if (this.loggedIn) {
            return getSignedInHelpString();
        } else {
            return getSignedOutHelpString();
        }
    }

    public void updateGame(ChessGame game) throws ResponseException {
        this.game = game;
        try {
            System.out.println(drawBoard(this.playerColor));
        } catch (ResponseException e) {
            throw new ResponseException(500, "Internal server error. Check your internet connection and try again.");
        }
    }
}
