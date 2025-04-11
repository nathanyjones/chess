package websocket.commands;

public class ConnectResignParentCommand extends UserGameCommand {
    private final String username;
    private final String color;

    public ConnectResignParentCommand(CommandType commandType, String authToken, Integer gameID,
                                      String username, String color) {
        super(commandType, authToken, gameID);
        this.username = username;
        this.color = color;
    }

    public String getUsername() {
        return this.username;
    }

    public String getColor() {return this.color;}
}
