

import java.awt.Graphics;
import java.awt.Rectangle;

public class Bullet extends GameObject implements EntityA {

    private Game game;
    private Textures tex;

    Animation anim;

    public Bullet(double x, double y, Textures tex, Game game){
        super(x, y);
        this.tex = tex;
        this.game = game;

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
