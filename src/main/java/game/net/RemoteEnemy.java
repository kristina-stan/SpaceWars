package game.net;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import game.graphics.Textures;
import game.graphics.Animation;

public class RemoteEnemy {
    private String id;
    private double x;
    private double y;
    private double targetX;
    private double targetY;
    private String enemyType;
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    private final double smoothing = 8.0;
    private Animation anim;

    public RemoteEnemy(String id, double x, double y, String enemyType, Textures tex) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.enemyType = enemyType;

        // Choose animation based on enemy type
        if (enemyType != null && enemyType.toLowerCase().contains("grunt")) {
            this.anim = new Animation(tex.bEnemy[0], tex.bEnemy[1]);
        } else if (enemyType != null && enemyType.toLowerCase().contains("shooter")) {
            this.anim = new Animation(tex.yEnemy[0]);
        } else {
            // default
            this.anim = new Animation(tex.gEnemy[0], tex.gEnemy[1]);
        }
    }

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

    public void render(Graphics2D g) {
        if (anim != null) {
            anim.drawAnimation(g, x, y, 0);
            return;
        }

        // Fallback: simple square
        g.setColor(new Color(255, 100, 100)); // Red for enemy
        g.fillRect((int)x - WIDTH/2, (int)y - HEIGHT/2, WIDTH, HEIGHT);

        // Add outline
        g.setColor(Color.WHITE);
        g.drawRect((int)x - WIDTH/2, (int)y - HEIGHT/2, WIDTH, HEIGHT);

        // Draw type indicator
        g.setColor(Color.YELLOW);
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
