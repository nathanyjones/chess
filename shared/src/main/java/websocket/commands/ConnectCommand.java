package websocket.commands;

public class ConnectCommand extends ConnectResignParentCommand {

    public ConnectCommand(String authToken, Integer gameID, String username, String color) {
        super(CommandType.CONNECT, authToken, gameID, username, color);
    }

}
