package game.net;

import java.awt.*;

public class RemotePlayer {
    private double x, y;
    private int health;
    private int points;
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;

    public RemotePlayer(double x, double y) {
        this.x = x;
        this.y = y;
        this.health = 100;
        this.points = 0;
    }

    public void render(Graphics2D g) {
        // Draw remote player ship (different color)
        g.setColor(Color.CYAN);

        // Simple ship shape
        int[] xPoints = {(int)x, (int)x - 16, (int)x + 16};
        int[] yPoints = {(int)y, (int)y + 32, (int)y + 32};
        g.fillPolygon(xPoints, yPoints, 3);

        // Draw health bar above ship
        g.setColor(Color.RED);
        g.fillRect((int)x - 20, (int)y - 10, 40, 4);
        g.setColor(Color.GREEN);
        int healthWidth = (int)(40 * (health / 100.0));
        g.fillRect((int)x - 20, (int)y - 10, healthWidth, 4);

        // Draw player label
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("P2", (int)x - 8, (int)y - 15);
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