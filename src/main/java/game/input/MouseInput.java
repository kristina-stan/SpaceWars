package game.input;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import game.core.Game;

public class MouseInput implements MouseListener {

    private Game game;

    public MouseInput(Game game){
        this.game = game;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

        int mx = e.getX();
        int my = e.getY();

        // BUTTONS IN MENU
        if(Game.State == Game.STATE.MENU) {
            if (mx >= Game.WIDTH / 2 + 120 && mx <= Game.WIDTH / 2 + 220) {
                if (my >= 150 && my <= 200) {
                    //pressed Play Button
                    Game.State = Game.STATE.GAME;
                    game.resetHealth();
                    // have to reset enemies
                } else if (my >= 250 && my <= 300) {
                    //pressed Help Button

                } else if (my >= 350 && my <= 400) {
                    //pressed Quit Button
                    System.exit(1);
                }
            }
        }

        // BUTTON IN GAME
        else if(Game.State == Game.STATE.GAME) {
            if (mx >= (Game.WIDTH * Game.SCALE) - 50 && mx <= (Game.WIDTH * Game.SCALE) - 20) {
                if(my >= 10 && my <= 30)
                    Game.State = Game.STATE.PAUSE;
            }
        }

        // BUTTON IN PAUSE
        else if (Game.State == Game.STATE.PAUSE) {
            if(my >= 150 && my <= 180) {
                if(mx >= 270 && mx <= 410)
                    Game.State = Game.STATE.GAME;
            }
            if(my >= 210 && my <= 245) {
                if (mx >= 250 && mx <= 450)
                    Game.State = Game.STATE.MENU;
            }
            if(my >= 270 && my <= 310) {
                if (mx >= 305 && mx <= 350)
                    System.exit(1);
            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseReleased'");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseEntered'");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mouseExited'");
    }

}
