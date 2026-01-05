package game.network.dto;

public class PlayerDTO {
    private int id;
    private float x;
    private float y;
    private int health;
    private int points;
    
    public PlayerDTO() {}
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
