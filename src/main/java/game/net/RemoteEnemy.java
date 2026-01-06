package game.net;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import game.graphics.Animation;
import game.graphics.Textures;

public class RemoteEnemy {
    private final String id;
    private double x;
    private double y;
    private double targetX;
    private double targetY;
    private final String enemyType;
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    private final double smoothing = 12.0;
    private final Animation anim;

    public RemoteEnemy(String id, double x, double y, String enemyType, Textures tex) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.enemyType = enemyType;

        // Choose animation based on enemy type (handle both class names and type strings)
        String typeUpper = enemyType != null ? enemyType.toLowerCase() : "";
        if (typeUpper.contains("grunt")) {
            this.anim = new Animation(tex.bEnemy[0], tex.bEnemy[1]);
        } else if (typeUpper.contains("shooter")) {
            this.anim = new Animation(tex.yEnemy[0]);
        } else {
            // default
            this.anim = new Animation(tex.gEnemy[0], tex.gEnemy[1]);
        }
    }

    private double lastServerX;
    private double lastServerY;
    private long lastServerTime;
    private double vx;
    private double vy;
    private final double extrapolationSeconds = 0.1; // predict 100ms ahead

    public void update(double deltaTime) {
        double t = Math.min(1.0, smoothing * deltaTime);
        this.x += (targetX - this.x) * t;
        this.y += (targetY - this.y) * t;
        if (anim != null) anim.runAnimation();
    }

    public void setTarget(double tx, double ty) {
        this.targetX = tx;
        this.targetY = ty;
    }

    public void setTargetFromServer(double tx, double ty, long serverRecvTimeMs) {
        // Deprecated path: compute velocity from previous sample
        if (lastServerTime > 0) {
            long dtMs = serverRecvTimeMs - lastServerTime;
            // Avoid tiny dt causing huge velocities
            if (dtMs < 20) dtMs = 20;
            double dt = dtMs / 1000.0;
            this.vx = (tx - lastServerX) / dt;
            this.vy = (ty - lastServerY) / dt;
        } else {
            this.vx = 0;
            this.vy = 0;
        }
        applyVelocityAndTarget(tx, ty, serverRecvTimeMs);
    }

    // New: accept authoritative vx/vy from server if available
    public void setTargetFromServer(double tx, double ty, double svx, double svy, long serverRecvTimeMs) {
        this.vx = svx;
        this.vy = svy;
        applyVelocityAndTarget(tx, ty, serverRecvTimeMs);
    }

    private void applyVelocityAndTarget(double tx, double ty, long serverRecvTimeMs) {
        // Cap velocities to reasonable ranges to avoid wild extrapolation
        double maxV = 1000.0; // pixels per second
        this.vx = Math.max(-maxV, Math.min(maxV, this.vx));
        this.vy = Math.max(-maxV, Math.min(maxV, this.vy));

        this.targetX = tx + vx * extrapolationSeconds;
        this.targetY = ty + vy * extrapolationSeconds;
        this.lastServerX = tx;
        this.lastServerY = ty;
        this.lastServerTime = serverRecvTimeMs;
    }

    // *** NEW: Add getBounds() method for collision detection ***
    public Rectangle getBounds() {
        // Return a Rectangle representing the enemy's collision box
        // This should match your local enemy hitbox size
        return new Rectangle((int)x - WIDTH/2, (int)y - HEIGHT/2, WIDTH, HEIGHT);
    }

    public double getVx() { return vx; }
    public double getVy() { return vy; }

    public void render(Graphics2D g) {
        // Draw enemy animation using proper animation rendering (matches host side)
        if (anim != null) {
            anim.drawAnimation(g, x, y, WIDTH/2);
        }

        // Draw type indicator
        g.setFont(new Font("Arial", Font.BOLD, 10));
        String label = enemyType.contains("Shooter") ? "S" : "G";
        g.drawString(label, (int)x - 5, (int)y + 5);
    }

    // Getters and Setters
    public String getId() { return id; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public String getEnemyType() { return enemyType; }
}