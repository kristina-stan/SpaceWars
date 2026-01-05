package game.net.dto;

public class EnemyDTO {
    private String id;
    private double x;
    private double y;
    private String enemyType;

    public EnemyDTO(String id, double x, double y, String enemyType) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.enemyType = enemyType;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }


    public String getEnemyType() { return enemyType; }
    public void setEnemyType(String enemyType) { this.enemyType = enemyType; }
}