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

    public Rectangle playButton = new Rectangle
            (Game.VIRTUAL_WIDTH / 2 + 120, 150, 100, 50);
    public Rectangle helpButton = new Rectangle
            (Game.VIRTUAL_WIDTH / 2 + 120, 250, 100, 50);
    public Rectangle quitButton = new Rectangle
            (Game.VIRTUAL_WIDTH / 2 + 120, 350, 100, 50);

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

        // Center Play button
        String playTxt = "Play";
        int playX = centerTextX(g, playTxt);
        g.drawString(playTxt, playX, playButton.y + 35);
        g2d.draw(new Rectangle(playX - 20, playButton.y,
                g.getFontMetrics().stringWidth(playTxt) + 40, 50));

        // Center Help button
        String helpTxt = "Help";
        int helpX = centerTextX(g, helpTxt);
        g.drawString(helpTxt, helpX, helpButton.y + 35);
        g2d.draw(new Rectangle(helpX - 20, helpButton.y,
                g.getFontMetrics().stringWidth(helpTxt) + 40, 50));

        // Center Quit button
        String quitTxt = "Quit";
        int quitX = centerTextX(g, quitTxt);
        g.drawString(quitTxt, quitX, quitButton.y + 35);
        g2d.draw(new Rectangle(quitX - 20, quitButton.y,
                g.getFontMetrics().stringWidth(quitTxt) + 40, 50));
    }

    public void renderGame(Graphics g, String playerPoints, int currentHealth, int maxHealth) {
        g2d = (Graphics2D) g;

        //HEALTH BAR
        g.setColor(Color.gray);
        g.fillRect(5,8,200, 15);

        g.setColor(Color.green);
        int healthWidth = (int)(200.0 * currentHealth / maxHealth);
        g.fillRect(5,8, healthWidth, 15);

        g.setColor(Color.white);
        g.drawRect(5,8,200, 15);

        //QUIT BUTTON while in game
        Font fnt0 = new Font("ariel", Font.BOLD, 15);
        g.setFont(fnt0);
        g.drawString("Pause", (Game.VIRTUAL_WIDTH) - 75, 25);

        g.drawString("Score: " + playerPoints, 10, 50);

    }

    public void renderPause(Graphics g) {
        g2d = (Graphics2D) g;

        // Disable anti-aliasing for text rendering
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        Font fnt0 = new Font("Arial", Font.BOLD, 35);
        g.setFont(fnt0);
        g.setColor(Color.white);
        String title = "GAME PAUSED";
        g.drawString(title, centerTextX(g, title), 100);

        Font fnt1 = new Font("Arial", Font.TRUETYPE_FONT ,25);
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

        Font fnt1 = new Font("Arial", Font.TRUETYPE_FONT ,25);
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

    }

}
