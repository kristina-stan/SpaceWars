package game.network.dto;

import java.util.List;

public class GameStateMessage extends GameMessage {
    private GameStateDTO state;
    
    public GameStateMessage() {
        super("state_update");
    }
    
    public GameStateDTO getState() { return state; }
    public void setState(GameStateDTO state) { this.state = state; }
    
    public static class GameStateDTO {
        private PlayerDTO player1;
        private PlayerDTO player2;
        private List<EnemyDTO> enemies;
        
        public PlayerDTO getPlayer1() { return player1; }
        public void setPlayer1(PlayerDTO player1) { this.player1 = player1; }
        
        public PlayerDTO getPlayer2() { return player2; }
        public void setPlayer2(PlayerDTO player2) { this.player2 = player2; }
        
        public List<EnemyDTO> getEnemies() { return enemies; }
        public void setEnemies(List<EnemyDTO> enemies) { this.enemies = enemies; }
    }
}