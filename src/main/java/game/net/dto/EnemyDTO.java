package game.network.dto;

public class EnemyDTO {
    private String id;
    private float x;
    private float y;
    private int health;
    private String enemyType;
    
    public EnemyDTO() {}
    
    public EnemyDTO(String id, float x, float y, int health, String enemyType) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.health = health;
        this.enemyType = enemyType;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    
    public String getEnemyType() { return enemyType; }
    public void setEnemyType(String enemyType) { this.enemyType = enemyType; }
}