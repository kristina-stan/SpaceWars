package game.net;

import game.core.Game;
import game.entities.Player;
import game.entities.interfaces.EntityB;
import game.net.dto.EnemyDTO;
import game.net.dto.GameStateMessage;
import game.net.dto.PlayerDTO;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NetworkManager {
    private GameClient client;
    private Game game;
    private RemotePlayer remotePlayer;
    private boolean multiplayerMode;
    private boolean isHosting;
    private String serverIp;

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
                remotePlayer = new RemotePlayer(remoteDto.getX(), remoteDto.getY(), remoteDto.getId());
            }
            remotePlayer.setX(remoteDto.getX());
            remotePlayer.setY(remoteDto.getY());
            remotePlayer.setHealth(remoteDto.getHealth());
            remotePlayer.setPoints(remoteDto.getPoints());
        }
    }

    public void render(Graphics2D g) {
        if (!multiplayerMode || remotePlayer == null) {
            return;
        }

        remotePlayer.render(g);
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
        isHosting = false;
        serverIp = "";
    }

    public RemotePlayer getRemotePlayer() {
        return remotePlayer;
    }
}
