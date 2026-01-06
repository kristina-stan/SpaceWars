
package game.net;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class RemotePlayer {
    private double x;
    private double y;
    private double targetX;
    private double targetY;
    private double lastServerX;
    private double lastServerY;
    private long lastServerTime;
    private double vx;
    private double vy;
    private final double extrapolationSeconds = 0.18; // predict 180ms ahead
    private int health;
    private int points;
    private final int playerId;
    private final double smoothing = 18.0; // higher = snappier
    private long lastUpdateTime = 0;

    public RemotePlayer(double x, double y, int playerId) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.lastServerX = x;
        this.lastServerY = y;
        this.lastServerTime = 0;
        this.vx = 0;
        this.vy = 0;
        this.health = 100;
        this.points = 0;
        this.playerId = playerId;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    // Call this every tick to smooth movement (deltaTime in seconds)
    public void update(double deltaTime) {
        double t = Math.min(1.0, smoothing * deltaTime);
        this.x += (targetX - this.x) * t;
        this.y += (targetY - this.y) * t;
    }

    // Basic direct set (keeps existing behavior)
    public void setTarget(double tx, double ty) {
        this.targetX = tx;
        this.targetY = ty;
    }

    // Use server-received position + compute velocity for simple extrapolation
    public void setTargetFromServer(double tx, double ty, long serverRecvTimeMs) {
        if (lastServerTime > 0) {
            double dt = (serverRecvTimeMs - lastServerTime) / 1000.0;
            if (dt > 0) {
                this.vx = (tx - lastServerX) / dt;
                this.vy = (ty - lastServerY) / dt;
            }
        }
        // Predict slightly ahead to compensate for latency
        this.targetX = tx + vx * extrapolationSeconds;
        this.targetY = ty + vy * extrapolationSeconds;

        this.lastServerX = tx;
        this.lastServerY = ty;
        this.lastServerTime = serverRecvTimeMs;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public double getTargetX() { return targetX; }
    public double getTargetY() { return targetY; }

    public void render(Graphics2D g) {
        // Draw remote player ship (different color from local player)
        g.setColor(new Color(0, 200, 255)); // Cyan/Blue

        // Simple ship shape (triangle pointing up)
        int[] xPoints = {(int)x, (int)x - 16, (int)x + 16};
        int[] yPoints = {(int)y, (int)y + 32, (int)y + 32};
        g.fillPolygon(xPoints, yPoints, 3);

        // Add outline
        g.setColor(Color.WHITE);
        g.drawPolygon(xPoints, yPoints, 3);

        // Draw health bar above ship
        int barWidth = 40;
        int barHeight = 5;
        int barX = (int)x - barWidth/2;
        int barY = (int)y - 15;

        g.setColor(new Color(50, 50, 50));
        g.fillRect(barX, barY, barWidth, barHeight);

        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);

        g.setColor(Color.GREEN);
        int healthWidth = (int)(barWidth * (health / 100.0));
        g.fillRect(barX, barY, healthWidth, barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);

        // Draw player label
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String label = "P" + playerId;
        int labelWidth = g.getFontMetrics().stringWidth(label);
        g.drawString(label, (int)x - labelWidth/2, (int)y - 20);
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public long getLastUpdateTime() { return lastUpdateTime; }
}