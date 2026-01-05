package game.net;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.core.Game;
import game.entities.Player;
import game.entities.interfaces.EntityB;
import game.net.dto.EnemyDTO;
import game.net.dto.GameStateMessage;
import game.net.dto.PlayerDTO;

public class NetworkManager {
    private GameClient client;
    private Game game;
    private RemotePlayer remotePlayer;
    private boolean multiplayerMode;
    private boolean isHosting;
    private String serverIp;
    private Map<String, RemoteEnemy> remoteEnemies = new HashMap<>();
    private Map<String, Double> lastEnemyX = new HashMap<>();
    private Map<String, Double> lastEnemyY = new HashMap<>();
    private Map<String, Long> lastEnemyTime = new HashMap<>();
    private static final boolean DEBUG = false; // set true to enable verbose networking logs

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
            long nowMs = System.currentTimeMillis();
            for (EntityB enemy : game.eb) {
                String id = String.valueOf(System.identityHashCode(enemy));
                double x = enemy.getX();
                double y = enemy.getY();

                double vx = 0.0;
                double vy = 0.0;
                if (lastEnemyX.containsKey(id) && lastEnemyY.containsKey(id) && lastEnemyTime.containsKey(id)) {
                    double lastX = lastEnemyX.get(id);
                    double lastY = lastEnemyY.get(id);
                    long lastT = lastEnemyTime.get(id);
                    long dtMs = nowMs - lastT;
                    if (dtMs < 20) dtMs = 20; // avoid tiny dt
                    double dt = dtMs / 1000.0;
                    vx = (x - lastX) / dt;
                    vy = (y - lastY) / dt;
                }

                // Save for next tick
                lastEnemyX.put(id, x);
                lastEnemyY.put(id, y);
                lastEnemyTime.put(id, nowMs);

                enemyDTOs.add(new EnemyDTO(
                        id,
                        x,
                        y,
                        enemy.getClass().getSimpleName(),
                        vx,
                        vy
                ));
            }
            client.sendEnemyUpdate(enemyDTOs);
        }

        // Receive and apply game state
        GameClient.StateWithTimestamp srt = client.getLatestState();
        if (srt != null) {
            updateFromState(srt.state, srt.timestamp);
        }
    }

<<<<<<< Updated upstream
    private void updateFromState(GameStateMessage.GameStateDTO state) {
=======
    // Called once per game tick to smooth/interpolate remote entities
    public void update(double deltaTime) {
        if (remotePlayer != null) remotePlayer.update(deltaTime);
        for (RemoteEnemy e : remoteEnemies.values()) {
            e.update(deltaTime);
        }
    }

    private void updateFromState(GameStateMessage.GameStateDTO state, long timestampMs) {
        long now = timestampMs > 0 ? timestampMs : System.currentTimeMillis();
        // Debug: show who we think we are and what arrived
        if (client != null && DEBUG) {
            System.out.println("NetworkManager.updateFromState: myPlayerId=" + client.getMyPlayerId() + ", hasPlayer1=" + (state.getPlayer1() != null) + ", hasPlayer2=" + (state.getPlayer2() != null) + ", ts=" + now);
        }

>>>>>>> Stashed changes
        // Update remote player
        PlayerDTO remoteDto = null;
        if (client.getMyPlayerId() == 1 && state.getPlayer2() != null) {
            remoteDto = state.getPlayer2();
        } else if (client.getMyPlayerId() == 2 && state.getPlayer1() != null) {
            remoteDto = state.getPlayer1();
        }

        if (remoteDto != null) {
<<<<<<< Updated upstream
            if (remotePlayer == null) {
                remotePlayer = new RemotePlayer(remoteDto.getX(), remoteDto.getY(), remoteDto.getId());
            }
            remotePlayer.setX(remoteDto.getX());
            remotePlayer.setY(remoteDto.getY());
            remotePlayer.setHealth(remoteDto.getHealth());
            remotePlayer.setPoints(remoteDto.getPoints());
        }

        // Update remote enemies from server
        if (state.getEnemies() != null) {
            updateRemoteEnemies(state.getEnemies());
=======
            if (DEBUG) System.out.println("NetworkManager: received remote player DTO -> id=" + remoteDto.getId() + ", x=" + remoteDto.getX() + ", y=" + remoteDto.getY());

            // If this DTO represents the OTHER player, update/create RemotePlayer
            if (remoteDto.getId() != client.getMyPlayerId()) {
                if (remotePlayer == null) {
                    // Create and register a RemotePlayer instance for this remote id
                    remotePlayer = new RemotePlayer(remoteDto.getX(), remoteDto.getY(), (int) remoteDto.getId());
                    if (DEBUG) System.out.println("NetworkManager: created RemotePlayer id=" + remoteDto.getId());
                } else {
                    // Debug update
                    if (DEBUG) System.out.println("NetworkManager: updating RemotePlayer target to x=" + remoteDto.getX() + " y=" + remoteDto.getY());
                }
                // Smoothly interpolate toward predicted target (extrapolated)
                if (remotePlayer != null) {
                    remotePlayer.setTargetFromServer(remoteDto.getX(), remoteDto.getY(), now);
                    remotePlayer.setHealth(remoteDto.getHealth());
                    remotePlayer.setPoints(remoteDto.getPoints());
                }

            } else {
                // This DTO is for *us* (authoritative server state). Apply health/position from server.
                if (DEBUG) System.out.println("NetworkManager: applying authoritative local player state: x=" + remoteDto.getX() + " y=" + remoteDto.getY() + " health=" + remoteDto.getHealth());
                Player local = game.getPlayer();
                if (local != null) {
                    // Apply health immediately
                    local.setCurrent_health(remoteDto.getHealth());
                    local.setPoints(remoteDto.getPoints());
                    // Smoothly correct position to server (small snap reduction)
                    local.setX(remoteDto.getX());
                    local.setY(remoteDto.getY());
                }
            }
>>>>>>> Stashed changes
        }

        // Always update remote enemies even if there's no remote player DTO
        updateRemoteEnemies(state.getEnemies() != null ? state.getEnemies() : java.util.Collections.emptyList(), now);
    }

    private void updateRemoteEnemies(List<EnemyDTO> enemies, long serverRecvTimeMs) {
        // Update existing and add new remote enemies
        for (EnemyDTO enemyDto : enemies) {
            RemoteEnemy remoteEnemy = remoteEnemies.get(enemyDto.getId());
            if (remoteEnemy == null) {
                remoteEnemy = new RemoteEnemy(enemyDto.getId(), enemyDto.getX(), enemyDto.getY(), enemyDto.getEnemyType());
                remoteEnemies.put(enemyDto.getId(), remoteEnemy);
                if (DEBUG) System.out.println("NetworkManager: created RemoteEnemy id=" + enemyDto.getId() + " type=" + enemyDto.getEnemyType());
            }
            if (enemyDto.getVx() != 0.0 || enemyDto.getVy() != 0.0) {
                remoteEnemy.setTargetFromServer(enemyDto.getX(), enemyDto.getY(), enemyDto.getVx(), enemyDto.getVy(), serverRecvTimeMs);
                if (DEBUG) System.out.println("NetworkManager: enemy id=" + enemyDto.getId() + " dto_vx=" + enemyDto.getVx() + " dto_vy=" + enemyDto.getVy() + " applied_vx=" + remoteEnemy.getVx() + " vy=" + remoteEnemy.getVy());
            } else {
                remoteEnemy.setTargetFromServer(enemyDto.getX(), enemyDto.getY(), serverRecvTimeMs);
                if (DEBUG) System.out.println("NetworkManager: enemy id=" + enemyDto.getId() + " computed_vx=" + remoteEnemy.getVx() + " vy=" + remoteEnemy.getVy());
            }
<<<<<<< Updated upstream
            remoteEnemy.setX(enemyDto.getX());
            remoteEnemy.setY(enemyDto.getY());
=======
>>>>>>> Stashed changes
        }

        // Remove enemies that no longer exist on server
        List<String> toRemove = new ArrayList<>();
        for (String id : remoteEnemies.keySet()) {
            boolean found = enemies.stream().anyMatch(e -> e.getId().equals(id));
            if (!found) {
                toRemove.add(id);
            }
        }
        toRemove.forEach(remoteEnemies::remove);
    }

    public void render(Graphics2D g) {
        if (!multiplayerMode) {
            return;
        }

        // Render remote player
        if (remotePlayer != null) {
            remotePlayer.render(g);
        }

        // Render remote enemies (only if we're NOT hosting - host sees local enemies)
        if (!isHosting) {
            for (RemoteEnemy enemy : remoteEnemies.values()) {
                enemy.render(g);
            }
        }
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
        remotePlayer = null;
        remoteEnemies.clear();
        isHosting = false;
        serverIp = "";
    }

    public RemotePlayer getRemotePlayer() {
        return remotePlayer;
    }

    public Map<String, RemoteEnemy> getRemoteEnemies() {
        return remoteEnemies;
    }
}

