package websocket.commands;

public class LeaveGameCommand extends UserGameCommand {
    private final boolean isPlayer;

    public LeaveGameCommand(String authToken, Integer gameID, boolean isPlayer) {
        super(CommandType.LEAVE, authToken, gameID);
        this.isPlayer = isPlayer;
    }

    public boolean getIsPlayer() {
        return this.isPlayer;
    }
}
