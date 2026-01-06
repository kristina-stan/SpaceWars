package game.net.dto;

public class EnemyDTO {
    private String id;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private String enemyType;

    public EnemyDTO() { super(); }

    public EnemyDTO(String id, double x, double y, String enemyType) {
        this(id, x, y, enemyType, 0.0, 0.0);
    }

    public EnemyDTO(String id, double x, double y, String enemyType, double vx, double vy) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.enemyType = enemyType;
        this.vx = vx;
        this.vy = vy;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getVx() { return vx; }
    public void setVx(double vx) { this.vx = vx; }

    public double getVy() { return vy; }
    public void setVy(double vy) { this.vy = vy; }

    public String getEnemyType() { return enemyType; }
    public void setEnemyType(String enemyType) { this.enemyType = enemyType; }
}