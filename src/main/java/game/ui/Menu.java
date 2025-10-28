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
            (Game.WIDTH / 2 + 120, 150, 100, 50);
    public Rectangle helpButton = new Rectangle
            (Game.WIDTH / 2 + 120, 250, 100, 50);
    public Rectangle quitButton = new Rectangle
            (Game.WIDTH / 2 + 120, 350, 100, 50);

    /*public Rectangle backToMenuButton = new Rectangle
            (230, 150, 200, 35);*/

    public void renderMenu(Graphics g){
        g2d = (Graphics2D) g;

        Font fnt0 = new Font("arial", Font.BOLD, 50);
        g.setFont(fnt0);
        g.setColor(Color.white);
        g.drawString("SPACE WARS", Game.WIDTH/2, 100);

        Font fnt1 = new Font("arial", Font.BOLD, 30);
        g.setFont(fnt1);
        g.drawString("Play", playButton.x + 20, playButton.y + 35);
        g2d.draw(playButton);
        g.drawString("Help", helpButton.x + 20, helpButton.y + 35);
        g2d.draw(helpButton);
        g.drawString("Quit", quitButton.x + 20, quitButton.y + 35);
        g2d.draw(quitButton);
    }
    public void renderGame(Graphics g){
        g2d = (Graphics2D) g;

        //HEALTH BAR
        g.setColor(Color.gray);
        g.fillRect(5,8,200, 15);
        g.setColor(Color.green);
        g.fillRect(5,8,Game.HEALTH, 15);
        g.setColor(Color.white);
        g.drawRect(5,8,200, 15);

        //QUIT BUTTON while in game
        Font fnt0 = new Font("ariel", Font.BOLD, 12);
        g.setFont(fnt0);
        g.drawString("Pause", (Game.WIDTH * Game.SCALE) - 50, 25);

    }
    public void renderPause(Graphics g) {
        g2d = (Graphics2D) g;

        // Disable anti-aliasing for text rendering
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        Font fnt0 = new Font("Arial", Font.BOLD, 35);
        g.setFont(fnt0);
        g.setColor(Color.white);
        g.drawString("GAME PAUSED", 200, 100);
        // BACK TO MENU
        Font fnt1 = new Font("Arial", Font.TRUETYPE_FONT ,25);
        g.setColor(Color.white);
        g.setFont(fnt1);
        g.drawString("Continue", 275, 175);
        g.drawString("Back to menu", 250, 235);
        g.drawString("Exit", 305, 295);

    }
    public void renderHelp(Graphics g){
        g2d = (Graphics2D) g;

    }

}
