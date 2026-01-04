package game.input;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import game.core.Game;
import game.ui.Menu;

public class MouseInput implements MouseListener {

    private Game game;
    private Menu menu;

    // MENU buttons
    private Rectangle menuPlay;
    private Rectangle menuHelp;
    private Rectangle menuQuit;

    // PAUSE buttons
    private Rectangle pauseContinue;
    private Rectangle pauseMenu;
    private Rectangle pauseExit;

    // GAME OVER buttons
    private Rectangle goMenu;
    private Rectangle goExit;

    public MouseInput(Game game, Menu menu){
        this.game = game;
        this.menu = menu;

        int center = Game.VIRTUAL_WIDTH / 2 - 100; // width = 200

        menuPlay = new Rectangle(center, 150, 150, 50);
        menuHelp = new Rectangle(center, 250, 150, 50);
        menuQuit = new Rectangle(center, 350, 150, 50);

        pauseContinue = new Rectangle(center, 175 - 30, 200, 40);
        pauseMenu     = new Rectangle(center, 235 - 30, 200, 40);
        pauseExit     = new Rectangle(center, 295 - 30, 200, 40);

        goMenu = new Rectangle(center, 235 - 30, 200, 40);
        goExit = new Rectangle(center, 295 - 30, 200, 40);
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

            if (menuPlay.contains(mx, my)) {
                Game.State = Game.STATE.GAME;
                game.resetHealth();
                return;
            }
            if (menuHelp.contains(mx, my)) {
                Game.State = Game.STATE.HELP;
                return;
            }
            if (menuQuit.contains(mx, my)) {
                System.exit(0);
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
                Game.State = Game.STATE.MENU;
                return;
            }

            if (goExit.contains(mx, my)) {
                System.exit(0);
            }
        }
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
