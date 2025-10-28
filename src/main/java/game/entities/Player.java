package game.entities;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import game.controller.Controller;
import game.core.Game;
import game.graphics.Animation;
import game.graphics.Textures;
import game.physics.Physics;

public class Player extends GameObject implements EntityA {

    // right++; left-- (original) and up--; down++; (inverted)

    private double velX = 0;
    private double velY = 0;
    private Textures tex;
    private Game game;
    private Controller c;

    Animation[] anim = new Animation[4];

    public Player(double x, double y, Textures tex, Controller c, Game game){
        super(x, y);
        this.tex = tex;
        this.c = c;
        this.game = game;

        anim[0] = new Animation(3,tex.player[0], tex.player[1]);
        anim[1] = new Animation(3,tex.player[2], tex.player[3]);
        anim[2] = new Animation(3,tex.player[4]);
        anim[3] = new Animation(3,tex.player[5]);
    }

    @Override
    public void tick(){
        x += velX;
        y += velY;

        // collition
        if(x <= 0) x = 0;
        else if (x >= (Game.WIDTH * Game.SCALE) - 32) x = (Game.WIDTH * Game.SCALE) - 32;
        if(y <= 0) y = 0;
        else if(y >= (Game.HEIGHT * Game.SCALE) - 32) y = (Game.HEIGHT * Game.SCALE) - 32;

        for(int i = 0; i < game.eb.size(); i++){
            EntityB tempEnt = game.eb.get(i); // enemy

            if(Physics.Collision(this, tempEnt)){ // if enemy and player crashed
                c.removeEntity(tempEnt); // remove enemy
                Game.HEALTH -= 10; // remove health
                game.setEnemy_killed(game.getEnemy_killed() + 1); // spawn numKilled + 1
                if(Game.HEALTH <= 0){
                    Game.State = Game.STATE.MENU;
                }
            }

        }

        if(getVelX() > 0) anim[2].runAnimation();
        else if (getVelX() < 0) anim[3].runAnimation();
        else if (getVelY() < 0) anim[1].runAnimation();
        else if(getVelY() > 0 || getVelY() == 0) anim[0].runAnimation();
    }

    @Override
    public void render(Graphics g){

        if(getVelX() > 0) anim[2].drawAnimation(g, x, y, 0);
        else if(getVelX() < 0) anim[3].drawAnimation(g, x, y, 0);
        else if(getVelY() < 0) anim[1].drawAnimation(g, x, y, 0);
        else anim[0].drawAnimation(g, x, y, 0);

    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    
    @Override
    public double getX() {
        return x;
    }
    
    @Override
    public double getY() {
        return y;
    }

    @Override
    public Rectangle getBounds(){
        return new Rectangle((int) x, (int) y, 32, 32);
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }
    public void setVelY(double velY) {
        this.velY = velY;
    }
    public double getVelX(){
        return this.velX;
    }
    public double getVelY(){
        return this.velY;
    }

}
