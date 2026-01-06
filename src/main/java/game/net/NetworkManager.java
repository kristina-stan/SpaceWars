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
    // Track previous enemy samples to compute velocities when hosting
    private static class EnemySample { double x; double y; long timeMs; EnemySample(double x, double y, long t){this.x=x;this.y=y;this.timeMs=t;} }
    private Map<String, EnemySample> prevEnemySamples = new HashMap<>();

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

    // Track previous player sample to compute velocity per second
    private static class PlayerSample { double x; double y; long timeMs; PlayerSample(double x,double y,long t){this.x=x;this.y=y;this.timeMs=t;} }
    private PlayerSample prevPlayerSample = null;

    public void tick() {
        if (!multiplayerMode || client == null || !client.isConnected()) {
            return;
        }

        // Send local player state
        Player localPlayer = game.getPlayer();
        double vx = 0.0, vy = 0.0;
        long now = System.currentTimeMillis();
        if (prevPlayerSample != null) {
            double dt = (now - prevPlayerSample.timeMs) / 1000.0;
            if (dt > 0) {
                vx = (localPlayer.getX() - prevPlayerSample.x) / dt;
                vy = (localPlayer.getY() - prevPlayerSample.y) / dt;
            }
            prevPlayerSample.x = localPlayer.getX(); prevPlayerSample.y = localPlayer.getY(); prevPlayerSample.timeMs = now;
        } else {
            prevPlayerSample = new PlayerSample(localPlayer.getX(), localPlayer.getY(), now);
        }

        client.sendPlayerUpdate(
                localPlayer.getX(),
                localPlayer.getY(),
                vx,
                vy,
                localPlayer.getCurrent_health(),
                localPlayer.getPoints()
        );

        // Player 1 (host) sends enemy updates
        if (client.getMyPlayerId() == 1) {
            List<EnemyDTO> enemyDTOs = new ArrayList<>();
            long nowEnemies = System.currentTimeMillis();
            for (EntityB enemy : game.eb) {
                String id = String.valueOf(System.identityHashCode(enemy));
                double x = enemy.getX();
                double y = enemy.getY();

                // compute velocity since last sample
                double evx = 0.0;
                double evy = 0.0;
                EnemySample sample = prevEnemySamples.get(id);
                if (sample != null) {
                    double dt = (nowEnemies - sample.timeMs) / 1000.0;
                    if (dt > 0) {
                        evx = (x - sample.x) / dt;
                        evy = (y - sample.y) / dt;
                    }
                    // update sample
                    sample.x = x; sample.y = y; sample.timeMs = nowEnemies;
                } else {
                    prevEnemySamples.put(id, new EnemySample(x, y, nowEnemies));
                }

                enemyDTOs.add(new EnemyDTO(
                        id,
                        x,
                        y,
                        evx,
                        evy,
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

    // Called once per game tick to smooth/interpolate remote entities
    public void update(double deltaTime) {
        if (remotePlayer != null) remotePlayer.update(deltaTime);
        for (RemoteEnemy e : remoteEnemies.values()) {
            e.update(deltaTime);
        }
    }

    private void updateFromState(GameStateMessage.GameStateDTO state) {
        // Debug: show who we think we are and what arrived
        if (client != null) {
            System.out.println("NetworkManager.updateFromState: myPlayerId=" + client.getMyPlayerId() + ", hasPlayer1=" + (state.getPlayer1() != null) + ", hasPlayer2=" + (state.getPlayer2() != null));
        }

        // Update remote player
        PlayerDTO remoteDto = null;
        if (client.getMyPlayerId() == 1 && state.getPlayer2() != null) {
            remoteDto = state.getPlayer2();
        } else if (client.getMyPlayerId() == 2 && state.getPlayer1() != null) {
            remoteDto = state.getPlayer1();
        }

        if (remoteDto != null) {
            System.out.println("NetworkManager: received remote player DTO -> id=" + remoteDto.getId() + ", x=" + remoteDto.getX() + ", y=" + remoteDto.getY() + ", vx=" + remoteDto.getVx() + ", vy=" + remoteDto.getVy());
            if (remotePlayer == null) {
                System.out.println("NetworkManager: creating RemotePlayer for id=" + remoteDto.getId());
                remotePlayer = new RemotePlayer(remoteDto.getX(), remoteDto.getY(), remoteDto.getId());
            } else {
                // Debug update
                System.out.println("NetworkManager: updating RemotePlayer target to x=" + remoteDto.getX() + " y=" + remoteDto.getY());
            }
            // apply small lead based on reported velocity to reduce visible lag
            double leadSeconds = 0.06; // 60ms lead
            remotePlayer.setTarget(remoteDto.getX() + remoteDto.getVx() * leadSeconds, remoteDto.getY() + remoteDto.getVy() * leadSeconds);
            remotePlayer.setVelocity(remoteDto.getVx(), remoteDto.getVy());
            remotePlayer.setHealth(remoteDto.getHealth());
            remotePlayer.setPoints(remoteDto.getPoints());
            if (state.getEnemies() != null) {
                updateRemoteEnemies(state.getEnemies());
            }

        }
    }

    private void updateRemoteEnemies(List<EnemyDTO> enemies) {
        if (enemies == null) return;
        // Update existing and add new remote enemies
        for (EnemyDTO enemyDto : enemies) {
            RemoteEnemy remoteEnemy = remoteEnemies.get(enemyDto.getId());
            if (remoteEnemy == null) {
                remoteEnemy = new RemoteEnemy(enemyDto.getId(), enemyDto.getX(), enemyDto.getY(), enemyDto.getEnemyType(), game.getTextures());
                remoteEnemies.put(enemyDto.getId(), remoteEnemy);
            }
            // apply small lead based on reported velocity to reduce visible lag
            double leadSeconds = 0.06; // 60ms lead
            double leadX = enemyDto.getX() + enemyDto.getVx() * leadSeconds;
            double leadY = enemyDto.getY() + enemyDto.getVy() * leadSeconds;
            remoteEnemy.setTarget(leadX, leadY);
            remoteEnemy.setVelocity(enemyDto.getVx(), enemyDto.getVy());
            System.out.println("NetworkManager: remoteEnemy updated id=" + enemyDto.getId() + " target=("+leadX+","+leadY+") vx=" + enemyDto.getVx() + " vy=" + enemyDto.getVy());
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

        System.out.println("NetworkManager.render: isHosting=" + isHosting + ", remoteEnemies=" + remoteEnemies.size());

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

