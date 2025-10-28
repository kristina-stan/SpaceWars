

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

    Game game;

    public KeyInput(Game game){
        this.game = game;
    }

    @Override
    // KeyAdapter automaticlly calls both methods
    public void keyPressed(KeyEvent e){
        game.keyPressed(e);
    }
    @Override
    public void keyReleased(KeyEvent e) {
        game.keyReleased(e);
    }
}