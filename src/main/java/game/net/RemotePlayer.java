package game.net;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import game.graphics.Animation;
import game.graphics.Textures;

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
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    private Animation[] anim = new Animation[4];
    private final Textures tex;

    public RemotePlayer(double x, double y, int playerId, Textures tex) {
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
        this.tex = tex;
        
        // Initialize animations with player1 or player2 sprites based on playerId
        java.awt.image.BufferedImage[] playerSprites = (playerId == 2) ? Textures.player2 : Textures.player;
        anim[0] = new Animation(playerSprites[0], playerSprites[1]); // idle
        anim[1] = new Animation(playerSprites[2], playerSprites[3]); // moving up
        anim[2] = new Animation(playerSprites[4]); // moving right
        anim[3] = new Animation(playerSprites[5]); // moving left
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

    // *** NEW: Add getBounds() method for collision detection ***
    public Rectangle getBounds() {
        // Return a Rectangle representing the player's collision box
        // This should match your local Player hitbox size
        return new Rectangle((int)x - WIDTH/2, (int)y - HEIGHT/2, WIDTH, HEIGHT);
    }

    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public double getTargetX() { return targetX; }
    public double getTargetY() { return targetY; }

    public void render(Graphics2D g) {
        // Run animation
        if (anim != null) {
            anim[0].runAnimation(); // Run idle animation for now (can enhance with velocity detection)
            anim[0].drawAnimation(g, x, y, 0);
        }

        // Draw health bar above player
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