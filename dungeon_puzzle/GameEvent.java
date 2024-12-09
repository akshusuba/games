package games.dungeon_puzzle;

public class GameEvent {
    private String type;
    private String message;

    public GameEvent(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
} 