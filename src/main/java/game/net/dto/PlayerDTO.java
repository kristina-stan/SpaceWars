package game.net.dto;

import java.util.List;

public class PlayerDTO {
    private int id;
    private double x;
    private double y;
    private int health;
    private int points;
    private List<BulletDTO> bullets;

    public PlayerDTO() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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
