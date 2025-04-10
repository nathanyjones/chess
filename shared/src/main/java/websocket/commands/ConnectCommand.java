package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    private final String username;
    private final String color;

    public ConnectCommand(String authToken, Integer gameID, String username, String color) {
        super(CommandType.CONNECT, authToken, gameID);
        this.username = username;
        this.color = color;
    }

    public String getUsername() {
        return this.username;
    }

    public String getColor() {return this.color;}
}
