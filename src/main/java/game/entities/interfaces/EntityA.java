package game.entities.interfaces;


import java.awt.Graphics;
import java.awt.Rectangle;

public interface EntityA {

    public void tick(double deltaTime);
    public void render(Graphics g);
    public Rectangle getBounds();
    public boolean getIsFriendly();

    public double getX();
    public double getY();

}