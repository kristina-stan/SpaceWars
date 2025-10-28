package game.entities;


import java.awt.Graphics;
import java.awt.Rectangle;

import game.config.GameConfig;
import game.controller.Controller;
import game.core.Game;
import game.entities.interfaces.EntityA;
import game.entities.interfaces.EntityB;
import game.graphics.Animation;
import game.graphics.Textures;
import game.physics.Physics;

public class Player extends GameObject implements EntityA {

    // right++; left-- (original) and up--; down++; (inverted)

    //position
    private double velX = 0;
    private double velY = 0;

    private int max_health, current_health, hp_regen, points;
    private double damage, speed, fire_rate, shield_duration;
    
    Animation[] anim = new Animation[4];

    public Player(double x, double y, Textures tex, Controller c, Game game, GameConfig config) {
        super(x, y, tex, game, c);

        GameConfig.PlayerConfig pConfig = config.player;
        this.max_health = pConfig.max_health;
        this.current_health = pConfig.current_health;
        this.damage = pConfig.damage;
        this.speed = pConfig.speed;
        this.fire_rate = pConfig.fire_rate;
        this.hp_regen = pConfig.hp_regen;
        this.shield_duration = pConfig.shield_duration;
        this.points = pConfig.points;

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

        // check collision with enemies
        for(int i = 0; i < game.eb.size(); i++){
            EntityB tempEnt = game.eb.get(i); // enemy

            if(Physics.Collision(this, tempEnt)){
                c.removeEntity(tempEnt); // remove enemy

                max_health -= 10; // reduce max health
                game.setEnemy_killed(game.getEnemy_killed() + 1); // spawn numKilled + 1
                
                if(max_health <= 0){
                    Game.State = Game.STATE.MENU;
                }
            }

        }

        // animation movement
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
