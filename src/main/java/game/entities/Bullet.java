package game.entities;


import java.awt.Graphics;
import java.awt.Rectangle;

import game.core.Game;
import game.entities.interfaces.EntityA;
import game.graphics.Animation;
import game.graphics.Textures;

public class Bullet extends GameObject implements EntityA {

    private boolean isFriendly = true;
    Animation anim;

    public Bullet(double x, double y ,Textures tex, Game game, boolean isFriendly){
        super(x, y, tex, game);
        this.isFriendly = isFriendly;
        if (isFriendly)
            anim = new Animation(Textures.missle[0], Textures.missle[1]);
        else
            anim = new Animation(tex.rotate(Textures.missle[0]), tex.rotate(Textures.missle[1]));
    }

    @Override
    public boolean getIsFriendly() {
        return isFriendly;
    }

    @Override
    public void tick(double deltaTime){
        if(isFriendly)
            y -= 7;
        else
            y += 7;

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
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    @Override
    public double getY(){
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
    }

}
