package service.result;

import model.GameData;
import java.util.Collection;

public class ListGamesResult {
    private final Collection<GameData> games;
    private final String message;

    public ListGamesResult(Collection<GameData> games) {
        this.games = games;
        this.message = null;
    }

    public ListGamesResult(String message) {
        this.message = message;
        this.games = null;
    }

    public Collection<GameData> getGameList() {
        return this.games;
    }

    public String getMessage() {
        return this.message;
    }

}