package game.net.dto;

public class ConnectionMessage extends GameMessage {
    private int playerId;
    
    public ConnectionMessage() {
        super("connection");
    }
    
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
}