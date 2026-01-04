package game.core;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class GameWindow {
    public GameWindow(Game game) {
        JFrame frame = new JFrame("2D Game");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.setVisible(true);
        //game.start();
    }
}
