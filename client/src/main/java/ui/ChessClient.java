package ui;

import ui.EscapeSequences;

public class ChessClient {
    private final String serverURL;
    private State state;

    public ChessClient(String serverURL) {
        this.serverURL = serverURL;
        this.state = State.SIGNED_OUT;
    }

    public String help() {
        if (this.state == State.SIGNED_OUT) {
            String template = """
                    \t{{bluet}}register <USERNAME> <PASSWORD> <EMAIL> {{blackt}}- to create an account
                    \t{{bluet}}login <USERNAME> <PASSWORD> {{blackt}}- to play chess
                    \t{{bluet}}quit {{blackt}}- playing chess
                    \t{{bluet}}help {{blackt}}- with possible commands
                    """;
            String message = template.replace("{{bluet}}", EscapeSequences.SET_TEXT_COLOR_BLUE);
            message = message.replace("{{blackt}}", EscapeSequences.SET_TEXT_COLOR_WHITE);
            return message;
        } else {
            return """
                    
                    """;
        }
    }
}
