package game.net.dto;

import com.google.gson.annotations.SerializedName;

public class ConnectionMessage extends GameMessage {
    @SerializedName("player_id")
    private int playerId;

    public ConnectionMessage() {
        super("connection");
    }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
}