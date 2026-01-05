package game.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import game.net.dto.BulletDTO;
import game.net.dto.BulletUpdateMessage;
import game.net.dto.ConnectionMessage;
import game.net.dto.EnemyDTO;
import game.net.dto.EnemyUpdateMessage;
import game.net.dto.GameStateMessage;
import game.net.dto.PlayerUpdateMessage;

public class GameClient {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private Gson gson;
    private int myPlayerId;
    private boolean connected;
    private Thread receiveThread;
    private BlockingQueue<GameStateMessage.GameStateDTO> stateQueue;
    private boolean gameStarted;

    public GameClient(String host, int port) {
        this.gson = new Gson();
        this.connected = false;
        this.gameStarted = false;
        this.stateQueue = new LinkedBlockingQueue<>();
        connect(host, port);
    }

    private void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            connected = true;

            System.out.println("Connected to server: " + host + ":" + port);

            receiveThread = new Thread(this::receiveMessages);
            receiveThread.setDaemon(true);
            receiveThread.start();

        } catch (IOException e) {
            System.err.println("Failed to connect: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        while (connected) {
            try {
                int length = input.readInt();
                byte[] data = new byte[length];
                input.readFully(data);
                String json = new String(data);

                handleMessage(json);

            } catch (IOException e) {
                if (connected) {
                    System.err.println("Connection lost: " + e.getMessage());
                    disconnect();
                }
                break;
            }
        }
    }

    private void handleMessage(String json) {
        JsonObject obj = gson.fromJson(json, JsonObject.class);
        String type = obj.get("type").getAsString();

        switch (type) {
            case "connection":
                ConnectionMessage connMsg = gson.fromJson(json, ConnectionMessage.class);
                myPlayerId = connMsg.getPlayerId();
                System.out.println("Assigned Player ID: " + myPlayerId);
                break;

            case "game_start":
                gameStarted = true;
                System.out.println("Game starting! Both players connected.");
                break;

            case "state_update":
                System.out.println("Received state_update JSON: " + json.substring(0, Math.min(300, json.length())));
                GameStateMessage stateMsg = gson.fromJson(json, GameStateMessage.class);
                if (stateMsg != null && stateMsg.getState() != null) {
                    System.out.println("State deserialized - Player1: " + stateMsg.getState().getPlayer1() + ", Player2: " + stateMsg.getState().getPlayer2() + ", Enemies: " + (stateMsg.getState().getEnemies() != null ? stateMsg.getState().getEnemies().size() : "null"));
                    stateQueue.offer(stateMsg.getState());
                } else {
                    System.out.println("ERROR: StateMessage or State is null");
                }
                break;

            case "player_disconnected":
                gameStarted = false;
                System.out.println("Other player disconnected - waiting for reconnection");
                break;

            case "error":
                System.err.println("Server error: " + obj.get("message").getAsString());
                break;

            default:
                System.out.println("Unknown message type: " + type);
        }
    }

    public void sendPlayerUpdate(double x, double y, int health, int points) {
        if (!connected) return;

        PlayerUpdateMessage msg = new PlayerUpdateMessage(x, y, health, points);
        sendMessage(msg);
    }

    public void sendEnemyUpdate(java.util.List<EnemyDTO> enemies) {
        if (!connected) return;

        EnemyUpdateMessage msg = new EnemyUpdateMessage(enemies);
        sendMessage(msg);
    }

    public void sendBulletUpdate(java.util.List<BulletDTO> bullets) {
        if (!connected) return;

        BulletUpdateMessage msg = new BulletUpdateMessage(bullets);
        sendMessage(msg);
    }

    private void sendMessage(Object message) {
        try {
            String json = gson.toJson(message);
            byte[] data = json.getBytes();

            synchronized (output) {
                output.writeInt(data.length);
                output.write(data);
                output.flush();
            }

        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
            disconnect();
        }
    }

    public GameStateMessage.GameStateDTO getLatestState() {
        return stateQueue.poll();
    }

    public int getMyPlayerId() {
        return myPlayerId;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void disconnect() {
        connected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}