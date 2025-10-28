

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public class Enemy extends GameObject implements EntityB {

    private Textures tex;
    private Game game;
    private Controller c;

    Random r = new Random();
    private int speed = r.nextInt(3) + 1;
    Animation anim;

    public Enemy(double x, double y, Textures tex, Controller c, Game game){
        super(x, y);
        this.tex = tex;
        this.c = c;
        this.game = game;

        anim = new Animation(10, tex.enemy[2], tex.enemy[3]);
    }

    @Override
    public void tick(){ // if it moves any time in the game
        y += speed;

        // when reached bottom, spawn from the top at random Y
        if(y > (Game.HEIGHT * Game.SCALE)){
            y = 0;
            x = r.nextInt(Game.WIDTH * Game.SCALE);
            speed = r.nextInt(3) + 1;
        }

        for(int i = 0; i < game.ea.size(); i++){
            EntityA tempEnt = game.ea.get(i);

            if(Physics.Collision(this, tempEnt)) { // enemy and bullet/player
                c.removeEntity(tempEnt); // remove bullet on contact
                c.removeEntity(this); // remove hit enemy
                game.setEnemy_killed(game.getEnemy_killed() + 1); // spawn numKilled + 1
            }
        }

        // collition
        if (x >= 640 - 30) x = 640 - 30;

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

    public void setY(double y) {
        this.y = y;
    }
    public void setX(double x) {
        this.x = x;
    }
}
