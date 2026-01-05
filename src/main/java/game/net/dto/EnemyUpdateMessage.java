package game.net.dto;

import java.util.List;

public class EnemyUpdateMessage extends GameMessage {
    private List<EnemyDTO> enemies;
    
    public EnemyUpdateMessage() {
        super("enemy_update");
    }
    
    public EnemyUpdateMessage(List<EnemyDTO> enemies) {
        super("enemy_update");
        this.enemies = enemies;
    }
    
    public List<EnemyDTO> getEnemies() { return enemies; }
    public void setEnemies(List<EnemyDTO> enemies) { this.enemies = enemies; }
}