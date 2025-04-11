package websocket.commands;

public class ResignCommand extends ConnectResignParentCommand {

    public ResignCommand(String authToken, Integer gameID, String username, String color) {
        super(CommandType.RESIGN, authToken, gameID, username, color);
    }

}
