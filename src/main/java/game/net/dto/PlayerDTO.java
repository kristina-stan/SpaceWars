package game.net.dto;

public class PlayerDTO {
    private int id;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private int health;
    private int points;

    public PlayerDTO() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getVx() { return vx; }
    public void setVx(double vx) { this.vx = vx; }

    public double getVy() { return vy; }
    public void setVy(double vy) { this.vy = vy; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
