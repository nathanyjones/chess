package ui;

import exception.ResponseException;
import websocket.NotificationHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_YELLOW + WHITE_KING +
                "Welcome to 240 Chess! Type help to get started." + WHITE_KING);
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("Exiting...")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.println(SET_TEXT_COLOR_YELLOW + result);
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.println(SET_TEXT_COLOR_RED + msg);
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    public void notify(NotificationMessage notification) {
        System.out.println(SET_TEXT_COLOR_MAGENTA + notification.getMessage());
        printPrompt();
    }

    public void handleError(ErrorMessage errorMessage) {
        System.out.println(SET_TEXT_COLOR_RED + errorMessage.getMessage());
        printPrompt();
    }

    public void loadGame(LoadGameMessage loadGameMessage) {
        try {
            System.out.println();
            client.updateGame(loadGameMessage.getGame());
            printPrompt();
        } catch (ResponseException e) {
            this.handleError(new ErrorMessage("Internal Server Error. Check Your Internet " +
                    "Connection and Try Again."));
        }
    }

}