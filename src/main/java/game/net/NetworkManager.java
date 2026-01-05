package game.net;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import game.core.Game;
import game.entities.Enemy;
import game.entities.Player;
import game.entities.interfaces.EntityA;
import game.entities.interfaces.EntityB;
import game.net.dto.BulletDTO;
import game.net.dto.EnemyDTO;
import game.net.dto.GameStateMessage;
import game.net.dto.PlayerDTO;

public class NetworkManager {
    private GameClient client;
    private Game game;
    private boolean multiplayerMode;
    private boolean isHosting;
    private String serverIp;
    private Map<String, RemoteEnemy> remoteEnemies = new HashMap<>();
    private LinkedList<BulletDTO> bulletCache = new LinkedList<>();

    public NetworkManager(Game game) {
        this.game = game;
        this.multiplayerMode = false;
        this.isHosting = false;
        this.serverIp = "";
    }

    public void connect(String host, int port) {
        client = new GameClient(host, port);
        multiplayerMode = true;
    }

    public void setMultiplayerMode(boolean enabled) {
        this.multiplayerMode = enabled;
    }

    public void setHosting(boolean hosting, String ip) {
        this.isHosting = hosting;
        this.serverIp = ip;
    }

    public boolean isHosting() {
        return isHosting;
    }

    public String getServerIp() {
        return serverIp;
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

        // Player 1 (host) sends enemy updates
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

        // Both players send their bullets
        bulletCache.clear();
        for (EntityA bullet : game.ea) {
            bulletCache.add(new BulletDTO(
                String.valueOf(System.identityHashCode(bullet)),
                bullet.getX(),
                bullet.getY()
            ));
        }
        client.sendBulletUpdate(bulletCache);

        // Receive and apply game state
        GameStateMessage.GameStateDTO state = client.getLatestState();
        if (state != null) {
            updateFromState(state);
        }
    }

    private void updateFromState(GameStateMessage.GameStateDTO state) {
        // Create or update the remote player as an actual Player object
        PlayerDTO remoteDto = null;
        if (client.getMyPlayerId() == 1 && state.getPlayer2() != null) {
            remoteDto = state.getPlayer2();
        } else if (client.getMyPlayerId() == 2 && state.getPlayer1() != null) {
            remoteDto = state.getPlayer1();
        }

        if (remoteDto != null) {
            if (game.getRemotePlayer() == null) {
                System.out.println("Creating new remote player: " + remoteDto.getId());
                // Create a Player object for the remote player (positioned on the right side)
                Player rp = new Player(remoteDto.getX(), remoteDto.getY(), game.tex, game.getController(), game, game.getConfig());
                game.setRemotePlayer(rp);
            }
            
            // Update remote player position and health
            Player rp = game.getRemotePlayer();
            rp.x = remoteDto.getX();
            rp.y = remoteDto.getY();
            rp.setCurrent_health(remoteDto.getHealth());
            System.out.println("Updated remote player at (" + remoteDto.getX() + ", " + remoteDto.getY() + ")");
        }

        // Update remote enemies from server
        if (state.getEnemies() != null) {
            System.out.println("Updating " + state.getEnemies().size() + " remote enemies");
            updateRemoteEnemies(state.getEnemies());
        } else {
            System.out.println("No enemies in state update");
        }
    }

    private void updateRemoteEnemies(List<EnemyDTO> enemies) {
        // Clear the controller's enemy list and add all enemies from server
        // This ensures both players see the exact same enemies
        if (!isHosting) {
            System.out.println("Client: Syncing " + enemies.size() + " enemies from server");
            
            // Store which enemies we've seen in this update
            Map<String, EnemyDTO> serverEnemies = new HashMap<>();
            for (EnemyDTO enemy : enemies) {
                serverEnemies.put(enemy.getId(), enemy);
            }
            
            // Remove enemies that no longer exist on server
            List<EntityB> localEnemies = game.getController().getEntityB();
            List<EntityB> toRemove = new ArrayList<>();
            for (EntityB localEnemy : localEnemies) {
                String id = String.valueOf(System.identityHashCode(localEnemy));
                if (!serverEnemies.containsKey(id)) {
                    toRemove.add(localEnemy);
                }
            }
            
            // Remove dead enemies
            for (EntityB enemy : toRemove) {
                game.getController().removeEntity(enemy);
                System.out.println("Removed enemy from local list");
            }
            
            // Update positions of existing enemies
            for (EntityB localEnemy : localEnemies) {
                String id = String.valueOf(System.identityHashCode(localEnemy));
                if (serverEnemies.containsKey(id)) {
                    EnemyDTO dto = serverEnemies.get(id);
                    // Direct assignment since x,y are public in GameObject
                    ((Enemy) localEnemy).x = dto.getX();
                    ((Enemy) localEnemy).y = dto.getY();
                }
            }
        } else {
            System.out.println("Host: Not syncing enemies - using local spawned enemies");
        }
    }

    public void render(Graphics2D g) {
        if (!multiplayerMode) {
            return;
        }

        // Note: Remote player is now a real Player object and is rendered
        // by the Game class in the main render loop, not here
    }

    public boolean isMultiplayerMode() {
        return multiplayerMode;
    }

    public boolean isGameReady() {
        return multiplayerMode && client != null && client.isGameStarted();
    }

    public int getPlayerCount() {
        if (!multiplayerMode || client == null) return 1;
        return client.isGameStarted() ? 2 : 1;
    }

    public void disconnect() {
        if (client != null) {
            client.disconnect();
        }
        multiplayerMode = false;
        // Remote player will be cleaned up when game resets
        remoteEnemies.clear();
        isHosting = false;
        serverIp = "";
    }

}

