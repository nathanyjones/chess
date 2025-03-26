package result;

import model.GameData;

public class GetGameResult {
    private final GameData game;
    private final String message;

    public GetGameResult(GameData game) {
        this.game = game;
        this.message = null;
    }

    public GetGameResult(String message) {
        this.message = message;
        this.game = null;
    }

    public GameData getGame() {
        return this.game;
    }

    public String getMessage() {
        return this.message;
    }

}