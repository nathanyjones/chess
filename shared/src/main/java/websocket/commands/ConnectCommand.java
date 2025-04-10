package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    private final String username;

    public ConnectCommand(String authToken, Integer gameID, String username) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}
