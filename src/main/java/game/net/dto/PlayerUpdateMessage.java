package game.network.dto;

public class PlayerUpdateMessage extends GameMessage {
    private float x;
    private float y;
    private int health;
    private int points;
    
    public PlayerUpdateMessage() {
        super("player_update");
    }
    
    public PlayerUpdateMessage(float x, float y, int health, int points) {
        super("player_update");
        this.x = x;
        this.y = y;
        this.health = health;
        this.points = points;
    }
    
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}