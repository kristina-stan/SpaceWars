package game.input;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JOptionPane;

import game.core.Game;
import game.ui.Menu;

public class MouseInput implements MouseListener {

    private Game game;
    @SuppressWarnings("unused")
    private Menu menu;

    // MENU buttons
    private Rectangle menuPlay;
    private Rectangle menuHost;
    private Rectangle menuJoin;
    private Rectangle menuHelp;
    private Rectangle menuQuit;

    // PAUSE buttons
    private Rectangle pauseContinue;
    private Rectangle pauseMenu;
    private Rectangle pauseExit;

    // GAME OVER buttons
    private Rectangle goMenu;
    private Rectangle goExit;

    // WAITING ROOM buttons
    private Rectangle waitingCancel;

    // HOST screen buttons
    private Rectangle hostStart;
    private Rectangle hostBack;

    // JOIN screen buttons
    private Rectangle joinConnect;
    private Rectangle joinBack;

    public MouseInput(Game game, Menu menu){
        this.game = game;
        this.menu = menu;

        int center = Game.VIRTUAL_WIDTH / 2 - 100; // width = 200

        // Main menu
        menuPlay = new Rectangle(center, 150, 200, 50);
        menuHost = new Rectangle(center, 220, 200, 50);
        menuJoin = new Rectangle(center, 290, 200, 50);
        menuHelp = new Rectangle(center, 360, 200, 50);
        menuQuit = new Rectangle(center, 430, 200, 50);

        // Pause screen
        pauseContinue = new Rectangle(center, 175 - 30, 200, 40);
        pauseMenu     = new Rectangle(center, 235 - 30, 200, 40);
        pauseExit     = new Rectangle(center, 295 - 30, 200, 40);

        // Game over
        goMenu = new Rectangle(center, 235 - 30, 200, 40);
        goExit = new Rectangle(center, 295 - 30, 200, 40);

        // Waiting room
        waitingCancel = new Rectangle(center, 400, 200, 50);

        // Host screen
        hostStart = new Rectangle(center, 300, 200, 50);
        hostBack = new Rectangle(center, 370, 200, 50);

        // Join screen
        joinConnect = new Rectangle(center, 300, 200, 50);
        joinBack = new Rectangle(center, 370, 200, 50);
    }

    @Override
    public void mousePressed(MouseEvent e) {

        // 1. Get the Raw Screen coordinates (0,0 is top-left of the window)
        int rawX = e.getX();
        int rawY = e.getY();

        // 2. Convert to Game Coordinates
        // Formula: (RawPosition - BlackBarSize) / ScaleFactor
        int mx = (int) ((rawX - game.currentOffsetX) / game.currentScale);
        int my = (int) ((rawY - game.currentOffsetY) / game.currentScale);

        // ==============================
        // MENU
        // ==============================
        if (Game.State == Game.STATE.MENU) {

            // Single Player
            if (menuPlay.contains(mx, my)) {
                game.getNetworkManager().setMultiplayerMode(false);
                Game.State = Game.STATE.GAME;
                game.resetHealth();
                return;
            }

            // Host Game
            if (menuHost.contains(mx, my)) {
                Game.State = Game.STATE.HOST;
                return;
            }

            // Join Game
            if (menuJoin.contains(mx, my)) {
                Game.State = Game.STATE.JOIN;
                return;
            }

            // Help
            if (menuHelp.contains(mx, my)) {
                Game.State = Game.STATE.HELP;
                return;
            }

            // Quit
            if (menuQuit.contains(mx, my)) {
                System.exit(0);
            }
        }

        // ==============================
        // HOST SCREEN
        // ==============================
        else if (Game.State == Game.STATE.HOST) {

            // Start Hosting button
            if (hostStart.contains(mx, my)) {
                handleHostGame();
                return;
            }

            // Back button
            if (hostBack.contains(mx, my)) {
                Game.State = Game.STATE.MENU;
                return;
            }
        }

        // ==============================
        // JOIN SCREEN
        // ==============================
        else if (Game.State == Game.STATE.JOIN) {

            // Connect button
            if (joinConnect.contains(mx, my)) {
                handleJoinGame();
                return;
            }

            // Back button
            if (joinBack.contains(mx, my)) {
                Game.State = Game.STATE.MENU;
                return;
            }
        }

        // ==============================
        // WAITING ROOM
        // ==============================
        else if (Game.State == Game.STATE.WAITING_ROOM) {

            // Cancel button
            if (waitingCancel.contains(mx, my)) {
                game.getNetworkManager().disconnect();
                Game.State = Game.STATE.MENU;
                return;
            }
        }

        // ==============================
        // IN-GAME → pause button
        // ==============================
        else if (Game.State == Game.STATE.GAME) {
            int px1 = Game.VIRTUAL_WIDTH - 75;
            int px2 = Game.VIRTUAL_WIDTH - 10;

            if (mx >= px1 && mx <= px2 && my >= 10 && my <= 30) {
                Game.State = Game.STATE.PAUSE;
            }
        }

        // ==============================
        // PAUSE SCREEN
        // ==============================
        else if (Game.State == Game.STATE.PAUSE) {

            if (pauseContinue.contains(mx, my)) {
                Game.State = Game.STATE.GAME;
                return;
            }

            if (pauseMenu.contains(mx, my)) {
                // Disconnect from multiplayer if active
                if (game.getNetworkManager().isMultiplayerMode()) {
                    game.getNetworkManager().disconnect();
                }
                Game.State = Game.STATE.MENU;
                return;
            }

            if (pauseExit.contains(mx, my)) {
                System.exit(0);
            }
        }

        // ==============================
        // GAME OVER
        // ==============================
        else if (Game.State == Game.STATE.GAMEOVER) {

            if (goMenu.contains(mx, my)) {
                // Disconnect from multiplayer if active
                if (game.getNetworkManager().isMultiplayerMode()) {
                    game.getNetworkManager().disconnect();
                }
                Game.State = Game.STATE.MENU;
                return;
            }

            if (goExit.contains(mx, my)) {
                System.exit(0);
            }
        }
    }

    /**
     * Handle hosting a multiplayer game
     */
    private void handleHostGame() {
        try {
            // Get local IP address
            String localIp = java.net.InetAddress.getLocalHost().getHostAddress();

            // Connect to localhost (we're the host)
            game.getNetworkManager().connect("localhost", 4040);
            game.getNetworkManager().setHosting(true, localIp);

            // Change to waiting room
            Game.State = Game.STATE.WAITING_ROOM;

            System.out.println("Hosting game on IP: " + localIp);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to start server: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
            Game.State = Game.STATE.MENU;
        }
    }

    /**
     * Handle joining a multiplayer game
     */
    private void handleJoinGame() {
        // Show input dialog for server IP
        String serverIp = JOptionPane.showInputDialog(
                null,
                "Enter the host's IP address:",
                "Join Game",
                JOptionPane.QUESTION_MESSAGE
        );

        // If user entered something
        if (serverIp != null && !serverIp.trim().isEmpty()) {
            serverIp = serverIp.trim();

            try {
                // Try to connect
                game.getNetworkManager().connect(serverIp, 4040);
                game.getNetworkManager().setHosting(false, serverIp);

                // Change to waiting room
                Game.State = Game.STATE.WAITING_ROOM;

                System.out.println("Connecting to: " + serverIp);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Failed to connect to " + serverIp + "\n" + ex.getMessage(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE
                );
                ex.printStackTrace();
                Game.State = Game.STATE.MENU;
            }
        } else {
            // User cancelled or entered nothing
            Game.State = Game.STATE.MENU;
        }
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}