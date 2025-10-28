package game.entities;


import java.awt.Graphics;
import java.awt.Rectangle;

import game.core.Game;
import game.entities.interfaces.EntityA;
import game.graphics.Animation;
import game.graphics.Textures;

public class Bullet extends GameObject implements EntityA {

    Animation anim;

    public Bullet(double x, double y, Textures tex, Game game){
        super(x, y, tex, game);
        anim = new Animation(3, tex.missle[0], tex.missle[1]);
    }

    @Override
    public void tick(){
        y -= 7;

        anim.runAnimation();
    }
    @Override
    public void render(Graphics g){
        anim.drawAnimation(g, x, y, 0);
    }
    @Override
    public Rectangle getBounds(){
        return new Rectangle((int) x, (int) y, 32, 32);
    }

    @Override
    public double getX() {
        return 0;
    }
    @Override
    public double getY(){
        return y;
    }

}
