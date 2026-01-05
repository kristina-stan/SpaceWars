package game.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import game.core.Game;

public class Menu {

    private Graphics2D g2d;

    // Main menu buttons (centered)
    public Rectangle playButton = new Rectangle
            (Game.VIRTUAL_WIDTH / 2 - 100, 150, 200, 50);
    public Rectangle hostButton = new Rectangle
            (Game.VIRTUAL_WIDTH / 2 - 100, 220, 200, 50);
    public Rectangle joinButton = new Rectangle
            (Game.VIRTUAL_WIDTH / 2 - 100, 290, 200, 50);
    public Rectangle helpButton = new Rectangle
            (Game.VIRTUAL_WIDTH / 2 - 100, 360, 200, 50);
    public Rectangle quitButton = new Rectangle
            (Game.VIRTUAL_WIDTH / 2 - 100, 430, 200, 50);

    private int centerTextX(Graphics g, String text) {
        return (Game.VIRTUAL_WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
    }

    public void renderMenu(Graphics g){
        g2d = (Graphics2D) g;

        Font fnt0 = new Font("arial", Font.BOLD, 50);
        g.setFont(fnt0);
        g.setColor(Color.white);
        String title = "SPACE WARS";
        g.drawString(title, centerTextX(g, title), 100);

        Font fnt1 = new Font("arial", Font.BOLD, 30);
        g.setFont(fnt1);

        // Single Player button
        String playTxt = "Single Player";
        g.drawString(playTxt, centerTextX(g, playTxt), playButton.y + 35);
        g2d.draw(playButton);

        // Host button
        String hostTxt = "Host Game";
        g.drawString(hostTxt, centerTextX(g, hostTxt), hostButton.y + 35);
        g2d.draw(hostButton);

        // Join button
        String joinTxt = "Join Game";
        g.drawString(joinTxt, centerTextX(g, joinTxt), joinButton.y + 35);
        g2d.draw(joinButton);

        // Help button
        String helpTxt = "Help";
        g.drawString(helpTxt, centerTextX(g, helpTxt), helpButton.y + 35);
        g2d.draw(helpButton);

        // Quit button
        String quitTxt = "Quit";
        g.drawString(quitTxt, centerTextX(g, quitTxt), quitButton.y + 35);
        g2d.draw(quitButton);
    }

    public void renderHost(Graphics g){
        g2d = (Graphics2D) g;

        Font fnt0 = new Font("Arial", Font.BOLD, 40);
        g.setFont(fnt0);
        g.setColor(Color.white);
        String title = "HOST GAME";
        g.drawString(title, centerTextX(g, title), 100);

        Font fnt1 = new Font("Arial", Font.PLAIN, 22);
        g.setFont(fnt1);

        String info1 = "You will host a multiplayer game.";
        String info2 = "Your IP will be shown after starting.";
        String info3 = "Share your IP with Player 2.";

        g.drawString(info1, centerTextX(g, info1), 180);
        g.drawString(info2, centerTextX(g, info2), 220);
        g.drawString(info3, centerTextX(g, info3), 260);

        Font fnt2 = new Font("arial", Font.BOLD, 30);
        g.setFont(fnt2);

        int center = Game.VIRTUAL_WIDTH / 2 - 100;

        // Start Hosting button
        String startTxt = "Start Hosting";
        g.drawString(startTxt, centerTextX(g, startTxt), 335);
        g2d.draw(new Rectangle(center, 300, 200, 50));

        // Back button
        String backTxt = "Back";
        g.drawString(backTxt, centerTextX(g, backTxt), 405);
        g2d.draw(new Rectangle(center, 370, 200, 50));
    }

    public void renderJoin(Graphics g){
        g2d = (Graphics2D) g;

        Font fnt0 = new Font("Arial", Font.BOLD, 40);
        g.setFont(fnt0);
        g.setColor(Color.white);
        String title = "JOIN GAME";
        g.drawString(title, centerTextX(g, title), 100);

        Font fnt1 = new Font("Arial", Font.PLAIN, 22);
        g.setFont(fnt1);

        String info1 = "Join a multiplayer game hosted by";
        String info2 = "another player.";
        String info3 = "You will need their IP address.";

        g.drawString(info1, centerTextX(g, info1), 180);
        g.drawString(info2, centerTextX(g, info2), 220);
        g.drawString(info3, centerTextX(g, info3), 260);

        Font fnt2 = new Font("arial", Font.BOLD, 30);
        g.setFont(fnt2);

        int center = Game.VIRTUAL_WIDTH / 2 - 100;

        // Connect button
        String connectTxt = "Connect";
        g.drawString(connectTxt, centerTextX(g, connectTxt), 335);
        g2d.draw(new Rectangle(center, 300, 200, 50));

        // Back button
        String backTxt = "Back";
        g.drawString(backTxt, centerTextX(g, backTxt), 405);
        g2d.draw(new Rectangle(center, 370, 200, 50));
    }

    public void renderWaitingRoom(Graphics g, boolean isHost, String serverIp){
        g2d = (Graphics2D) g;

        Font fnt0 = new Font("Arial", Font.BOLD, 40);
        g.setFont(fnt0);
        g.setColor(Color.white);
        String title = isHost ? "HOSTING GAME" : "JOINING GAME";
        g.drawString(title, centerTextX(g, title), 100);

        Font fnt1 = new Font("Arial", Font.BOLD, 24);
        g.setFont(fnt1);

        if (isHost) {
            String ipLabel = "Your Server IP:";
            g.drawString(ipLabel, centerTextX(g, ipLabel), 200);

            g.setColor(Color.CYAN);
            Font fnt2 = new Font("Arial", Font.BOLD, 32);
            g.setFont(fnt2);
            g.drawString(serverIp, centerTextX(g, serverIp), 240);

            g.setColor(Color.white);
            Font fnt3 = new Font("Arial", Font.PLAIN, 20);
            g.setFont(fnt3);
            String instruction = "Share this IP with Player 2";
            g.drawString(instruction, centerTextX(g, instruction), 280);
        } else {
            String connectingText = "Connecting to:";
            g.drawString(connectingText, centerTextX(g, connectingText), 200);

            g.setColor(Color.CYAN);
            Font fnt2 = new Font("Arial", Font.BOLD, 32);
            g.setFont(fnt2);
            g.drawString(serverIp, centerTextX(g, serverIp), 240);
            g.setColor(Color.white);
        }

        Font fnt4 = new Font("Arial", Font.PLAIN, 26);
        g.setFont(fnt4);
        String waitingText = "Waiting for other player";
        g.drawString(waitingText, centerTextX(g, waitingText), 340);

        // Animated dots
        long time = System.currentTimeMillis() / 500;
        String dots = ".".repeat((int)(time % 4));
        g.drawString(dots, centerTextX(g, waitingText) +
                g.getFontMetrics().stringWidth(waitingText) + 10, 340);

        // Cancel button
        Font fnt5 = new Font("arial", Font.BOLD, 30);
        g.setFont(fnt5);
        String cancelTxt = "Cancel";
        int center = Game.VIRTUAL_WIDTH / 2 - 100;
        g.drawString(cancelTxt, centerTextX(g, cancelTxt), 435);
        g2d.draw(new Rectangle(center, 400, 200, 50));
    }

    public void renderGame(Graphics g, String playerPoints, int currentHealth, int maxHealth) {
        g2d = (Graphics2D) g;

        //HEALTH BAR
        g.setColor(Color.gray);
        g.fillRect(5, 8, 200, 15);

        g.setColor(Color.green);
        int healthWidth = (int)(200.0 * currentHealth / maxHealth);
        g.fillRect(5, 8, healthWidth, 15);

        g.setColor(Color.white);
        g.drawRect(5, 8, 200, 15);

        //PAUSE BUTTON
        Font fnt0 = new Font("ariel", Font.BOLD, 15);
        g.setFont(fnt0);
        g.drawString("Pause", (Game.VIRTUAL_WIDTH) - 75, 25);

        g.drawString("Score: " + playerPoints, 10, 50);
    }

    public void renderGame(Graphics g, String playerPoints, int currentHealth, int maxHealth,
                           boolean isMultiplayer, int playerCount) {
        g2d = (Graphics2D) g;

        //HEALTH BAR
        g.setColor(Color.gray);
        g.fillRect(5, 8, 200, 15);

        g.setColor(Color.green);
        int healthWidth = (int)(200.0 * currentHealth / maxHealth);
        g.fillRect(5, 8, healthWidth, 15);

        g.setColor(Color.white);
        g.drawRect(5, 8, 200, 15);

        //PAUSE BUTTON
        Font fnt0 = new Font("ariel", Font.BOLD, 15);
        g.setFont(fnt0);
        g.drawString("Pause", (Game.VIRTUAL_WIDTH) - 75, 25);

        g.drawString("Score: " + playerPoints, 10, 50);

        // Multiplayer indicator
        if (isMultiplayer) {
            g.setColor(Color.CYAN);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            String multiplayerText = "Multiplayer - " + playerCount + "/2 Players";
            g.drawString(multiplayerText, 10, Game.VIRTUAL_HEIGHT - 10);
        }
    }

    public void renderPause(Graphics g) {
        g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        Font fnt0 = new Font("Arial", Font.BOLD, 35);
        g.setFont(fnt0);
        g.setColor(Color.white);
        String title = "GAME PAUSED";
        g.drawString(title, centerTextX(g, title), 100);

        Font fnt1 = new Font("Arial", Font.TRUETYPE_FONT, 25);
        g.setColor(Color.white);
        g.setFont(fnt1);

        String continueTxt = "Continue";
        String backMenuTxt = "Back to menu";
        String exitTxt = "Exit";

        g.drawString(continueTxt, centerTextX(g, continueTxt), 175);
        g.drawString(backMenuTxt, centerTextX(g, backMenuTxt), 235);
        g.drawString(exitTxt, centerTextX(g, exitTxt), 295);
    }

    public void renderGameOver(Graphics g, int playerPoints) {
        g2d = (Graphics2D) g;

        Font fnt0 = new Font("Arial", Font.BOLD, 35);
        g.setFont(fnt0);
        g.setColor(Color.white);
        String gameOverTxt = "GAME OVER";
        g.drawString(gameOverTxt, centerTextX(g, gameOverTxt), 100);

        Font fnt1 = new Font("Arial", Font.TRUETYPE_FONT, 25);
        g.setColor(Color.white);
        g.setFont(fnt1);

        String finalScoreTxt = "Final Score: " + playerPoints;
        String backMenuTxt = "Back to menu";
        String exitTxt = "Exit";

        g.drawString(finalScoreTxt, centerTextX(g, finalScoreTxt), 175);
        g.drawString(backMenuTxt, centerTextX(g, backMenuTxt), 235);
        g.drawString(exitTxt, centerTextX(g, exitTxt), 295);
    }

    public void renderHelp(Graphics g){
        g2d = (Graphics2D) g;

        Font fnt0 = new Font("Arial", Font.BOLD, 40);
        g.setFont(fnt0);
        g.setColor(Color.white);
        String title = "HOW TO PLAY";
        g.drawString(title, centerTextX(g, title), 80);

        Font fnt1 = new Font("Arial", Font.PLAIN, 20);
        g.setFont(fnt1);

        int y = 140;
        int lineHeight = 30;

        g.drawString("CONTROLS:", 50, y);
        y += lineHeight;
        g.drawString("  Arrow Keys - Move your ship", 70, y);
        y += lineHeight;
        g.drawString("  SPACE - Shoot", 70, y);
        y += lineHeight;
        g.drawString("  ESC - Pause game", 70, y);
        y += lineHeight + 20;

        g.drawString("SINGLE PLAYER:", 50, y);
        y += lineHeight;
        g.drawString("  Fight waves of enemies alone", 70, y);
        y += lineHeight;
        g.drawString("  Survive as long as possible", 70, y);
        y += lineHeight + 20;

        g.drawString("MULTIPLAYER:", 50, y);
        y += lineHeight;
        g.drawString("  Host Game - Start a server", 70, y);
        y += lineHeight;
        g.drawString("  Join Game - Connect to another player", 70, y);
        y += lineHeight;
        g.drawString("  Player 1 spawns enemies", 70, y);
        y += lineHeight;
        g.drawString("  Both players fight together!", 70, y);

        y += lineHeight + 30;
        g.setColor(Color.YELLOW);
        g.drawString("Press ESC to return to menu", centerTextX(g, "Press ESC to return to menu"), y);
    }
}