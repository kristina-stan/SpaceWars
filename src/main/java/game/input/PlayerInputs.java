package game.input;

import java.awt.event.KeyEvent;

import game.core.Game.STATE;
import static game.core.Game.State;
import game.entities.Player;

public class PlayerInputs {
    
    private Player p;
    private boolean is_shootinng = false;
    
    public PlayerInputs(Player player) {
        this.p = player;
    }

     //---------- INPUT HANDLING ----------
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();

        if(null != State) switch (State) {
            case GAME -> {
                if (key == KeyEvent.VK_RIGHT) {
                    p.setVelX(3);
                } else if (key == KeyEvent.VK_LEFT) {
                    p.setVelX(-3);
                } else if (key == KeyEvent.VK_DOWN) {
                    p.setVelY(3);
                } else if (key == KeyEvent.VK_UP) {
                    p.setVelY(-3);
                } else if (key == KeyEvent.VK_SPACE && !is_shootinng) {
                    is_shootinng = true;
                    if (p.canShoot())
                        p.shoot();
                } else if (key == KeyEvent.VK_ESCAPE) {
                    State = STATE.PAUSE;
                }
            }
            case MENU -> {
                if (key == KeyEvent.VK_ENTER) {
                    State = STATE.GAME;
                }
            }
            case PAUSE -> {
                if(key == KeyEvent.VK_ESCAPE) {
                    State = STATE.GAME;
                }
            }
            case GAMEOVER -> {
                if (key == KeyEvent.VK_ENTER) {
                    State = STATE.GAME;
                }
                else if (key == KeyEvent.VK_ESCAPE) {
                    State = STATE.MENU;
                }
            }
            default -> {
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_RIGHT -> p.setVelX(0);
            case KeyEvent.VK_LEFT -> p.setVelX(0);
            case KeyEvent.VK_DOWN -> p.setVelY(0);
            case KeyEvent.VK_UP -> p.setVelY(0);
            case KeyEvent.VK_SPACE -> is_shootinng = false;
            default -> {
            }
        }
    }
}
