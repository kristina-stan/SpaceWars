package game.net;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class RemoteEnemy {
    private String id;
    private double x;
    private double y;
    private String enemyType;
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;

    public RemoteEnemy(String id, double x, double y, String enemyType) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.enemyType = enemyType;
    }

    public void render(Graphics2D g) {
        // Draw enemy as a simple square (different from local player)
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
