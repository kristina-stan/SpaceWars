package game.entities.interfaces;


import java.awt.Graphics;
import java.awt.Rectangle;

public interface EntityB {

    public void tick(double deltaTime);
    public void render(Graphics g);
    public Rectangle getBounds();

    public double getX();
    public double getY();

}