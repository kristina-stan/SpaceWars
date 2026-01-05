package game.net.dto;

public class PlayerUpdateMessage extends GameMessage {
    private double x;
    private double y;
    private int health;
    private int points;

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

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}