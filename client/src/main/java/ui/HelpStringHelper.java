package ui;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class HelpStringHelper {

    public static String getSignedOutHelpString() {
        String template = """
                \t{{bluet}}register <USERNAME> <PASSWORD> <EMAIL> {{whitet}}- to create an account
                \t{{bluet}}login <USERNAME> <PASSWORD> {{whitet}}- to play chess
                \t{{bluet}}quit {{whitet}}- playing chess
                \t{{bluet}}help {{whitet}}- with possible commands""";
        String message = template.replace("{{bluet}}", SET_TEXT_COLOR_BLUE);
        message = message.replace("{{whitet}}", SET_TEXT_COLOR_WHITE);
        return message;
    }

    public static String getSignedInHelpString() {
        String template = """
                    \t{{bluet}}create <NAME> {{whitet}}- a game
                    \t{{bluet}}list {{whitet}}- games
                    \t{{bluet}}join <ID> [WHITE|BLACK] {{whitet}}- a game
                    \t{{bluet}}observe <ID> {{whitet}}- a game
                    \t{{bluet}}logout {{whitet}}- when you are done
                    \t{{bluet}}help {{whitet}}- with possible commands""";
        String message = template.replace("{{bluet}}", SET_TEXT_COLOR_BLUE);
        message = message.replace("{{whitet}}", SET_TEXT_COLOR_WHITE);
        return message;
    }

    public static String getPlayingGameHelpString() {
        String template = """
                    \t{{bluet}}redraw {{whitet}}- redraws the chess board
                    \t{{bluet}}leave {{whitet}}- your current game
                    \t{{bluet}}move <Start Position> <End Position> {{whitet}}- a piece (ex. move e2 e4)
                    \t{{bluet}}resign {{whitet}}- forfeit the game
                    \t{{bluet}}show <Position> {{whitet}}- highlight legal moves (ex. show f5)
                    \t{{bluet}}help {{whitet}}- with possible commands""";
        String message = template.replace("{{bluet}}", SET_TEXT_COLOR_BLUE);
        message = message.replace("{{whitet}}", SET_TEXT_COLOR_WHITE);
        return message;
    }

    public static String getObservingGameHelpString() {
        String template = """
                    \t{{bluet}}redraw {{whitet}}- redraws the chess board
                    \t{{bluet}}leave {{whitet}}- stop observing this game
                    \t{{bluet}}show <Position> {{whitet}}- highlight legal moves (ex. show f5)
                    \t{{bluet}}help {{whitet}}- with possible commands""";
        String message = template.replace("{{bluet}}", SET_TEXT_COLOR_BLUE);
        message = message.replace("{{whitet}}", SET_TEXT_COLOR_WHITE);
        return message;
    }

}
