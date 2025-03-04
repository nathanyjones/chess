package service.result;

public class CreateGameResult {
    private Integer gameID;
    private String message;

    public CreateGameResult(int gameID) {
        this.gameID = gameID;
    }

    public CreateGameResult(String message) {
        this.message = message;
    }

    public Integer getGameID() {
        return this.gameID;
    }

    public String getMessage() {
        return this.message;
    }

}