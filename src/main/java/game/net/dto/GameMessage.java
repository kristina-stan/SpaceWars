package game.net.dto;

public class GameMessage {
    private String type;
    
    public GameMessage() {}
    public GameMessage(String type) {
        this.type = type;
    }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}