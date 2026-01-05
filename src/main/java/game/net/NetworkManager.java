package game.net;

import game.core.Game;
import game.entities.Player;
import game.entities.interfaces.EntityB;
import game.net.dto.EnemyDTO;
import game.net.dto.GameStateMessage;
import game.net.dto.PlayerDTO;
import game.graphics.Textures;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager {
    private GameClient client;
    private Game game;
    private RemotePlayer remotePlayer;
    private boolean multiplayerMode;
    
    // For syncing enemies
    private Map<String, EntityB> enemyMap;
    
    public NetworkManager(Game game) {
        this.game = game;
        this.multiplayerMode = false;
        this.enemyMap = new HashMap<>();
    }
    
    public void connect(String host, int port) {
        client = new GameClient(host, port);
        multiplayerMode = true;
    }
    
    public void tick() {
        if (!multiplayerMode || client == null || !client.isConnected()) {
            return;
        }
        
        // Send local player state
        Player localPlayer = game.getPlayer();
        client.sendPlayerUpdate(
            localPlayer.getX(),
            localPlayer.getY(),
            localPlayer.getCurrent_health(),
            localPlayer.getPoints()
        );
        
        // Player 1 sends enemy updates
        if (client.getMyPlayerId() == 1) {
            List<EnemyDTO> enemyDTOs = new ArrayList<>();
            for (EntityB enemy : game.eb) {
                enemyDTOs.add(new EnemyDTO(
                    String.valueOf(System.identityHashCode(enemy)),
                    enemy.getX(),
                    enemy.getY(),
                    enemy.getClass().getSimpleName()
                ));
            }
            client.sendEnemyUpdate(enemyDTOs);
        }
        
        // Receive and apply game state
        GameStateMessage.GameStateDTO state = client.getLatestState();
        if (state != null) {
            updateFromState(state);
        }
    }
    
    private void updateFromState(GameStateMessage.GameStateDTO state) {
        // Update remote player
        PlayerDTO remoteDto = null;
        if (client.getMyPlayerId() == 1 && state.getPlayer2() != null) {
            remoteDto = state.getPlayer2();
        } else if (client.getMyPlayerId() == 2 && state.getPlayer1() != null) {
            remoteDto = state.getPlayer1();
        }
        
        if (remoteDto != null) {
            if (remotePlayer == null) {
                remotePlayer = new RemotePlayer(remoteDto.getX(), remoteDto.getY());
            }
            remotePlayer.setX(remoteDto.getX());
            remotePlayer.setY(remoteDto.getY());
            remotePlayer.setHealth(remoteDto.getHealth());
            remotePlayer.setPoints(remoteDto.getPoints());
        }
        
        // Player 2 syncs enemies from Player 1
        if (client.getMyPlayerId() == 2 && state.getEnemies() != null) {
            syncEnemiesFromServer(state.getEnemies());
        }
    }
    
    private void syncEnemiesFromServer(List<EnemyDTO> enemyDTOs) {
        // This is a simple sync - you might want more sophisticated logic
        // For now, we just trust Player 1's enemy list
        // In a production game, you'd want interpolation and prediction
    }
    
    public void render(Graphics2D g) {
        if (!multiplayerMode || remotePlayer == null) {
            return;
        }
        
        remotePlayer.render(g);
        
        // Draw connection status
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String status = client.isGameStarted() ? "Connected: 2/2 Players" : "Waiting for player...";
        g.drawString(status, 10, Game.VIRTUAL_HEIGHT - 20);
    }
    
    public boolean isMultiplayerMode() {
        return multiplayerMode;
    }
    
    public boolean isGameReady() {
        return multiplayerMode && client != null && client.isGameStarted();
    }
    
    public void disconnect() {
        if (client != null) {
            client.disconnect();
        }
        multiplayerMode = false;
        remotePlayer = null;
    }
    
    public RemotePlayer getRemotePlayer() {
        return remotePlayer;
    }
}