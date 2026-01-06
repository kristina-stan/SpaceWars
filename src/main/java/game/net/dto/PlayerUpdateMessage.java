package game.net.dto;

import java.util.List;

public class PlayerUpdateMessage extends GameMessage {
    private double x;
    private double y;
    private int health;
    private int points;
    private List<BulletDTO> bullets;

    public PlayerUpdateMessage() {
        super("player_update");
    }

    public PlayerUpdateMessage(double x, double y, int health, int points) {
        super("player_update");
        this.x = x;
        this.y = y;
        this.health = health;
        this.points = points;
    }

    public PlayerUpdateMessage(double x, double y, int health, int points, List<BulletDTO> bullets) {
        super("player_update");
        this.x = x;
        this.y = y;
        this.health = health;
        this.points = points;
        this.bullets = bullets;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public List<BulletDTO> getBullets() { return bullets; }
    public void setBullets(List<BulletDTO> bullets) { this.bullets = bullets; }
}