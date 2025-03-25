package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_WHITE + WHITE_KING +
                "Welcome to 240 Chess! Type help to get started." + WHITE_KING);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
//        var result = "";
        String line = "";
//        while (!result.equals("quit")) {
        while (!line.equals("quit")) {
            printPrompt();
            line = scanner.nextLine();

//            try {
//                result = client.eval(line);
//                System.out.print(BLUE + result);
//            } catch (Throwable e) {
//                var msg = e.toString();
//                System.out.print(msg);
//            }
        }
        System.out.println();
    }

//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}