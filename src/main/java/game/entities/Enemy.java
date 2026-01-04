package game.entities;


import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

import game.config.GameConfig;
import game.controller.Controller;
import game.core.Game;
import game.entities.interfaces.EntityA;
import game.entities.interfaces.EntityB;
import game.graphics.Animation;
import game.graphics.Textures;
import game.physics.Physics;

public abstract class Enemy extends GameObject implements EntityB {

    protected int max_health, damage, reward;
    protected double speed, spawn_rate;
    protected String type;
    protected Animation anim;

    Random r = new Random();

    public Enemy(double x, double y, Textures tex, Controller c, Game game,
        Animation anim, String type, GameConfig config){

        super(x, y, tex, game, c);
        this.type = type;

        GameConfig.EnemyConfig eConfig = config.enemies.get(type);
        this.max_health = eConfig.max_health;
        this.damage = eConfig.damage;
        this.reward = eConfig.reward;
        this.speed = eConfig.speed;
        this.spawn_rate = eConfig.spawn_rate;

        this.anim = anim;
    }

    @Override
    public void tick(double deltaTime){ // if it moves any time in the game

        for(int i = 0; i < game.ea.size(); i++){
            EntityA tempEnt = game.ea.get(i);

            if(Physics.Collision(this, tempEnt) && tempEnt.getIsFriendly()) { // enemy and bullet/player
                c.removeEntity(tempEnt); // remove bullet on contact
                c.removeEntity(this); // remove hit enemy
                game.setEnemy_killed(game.getEnemy_killed() + 1); // spawn numKilled + 1
                game.getPlayer().addPoints(this.reward); // hit by player bullet
            }
        }
        if(this.y > Game.VIRTUAL_HEIGHT) c.removeEntity(this);

        // collition with end off screen -> move to visible area
        if (x >= Game.VIRTUAL_HEIGHT - 30) x = Game.VIRTUAL_HEIGHT - 30;

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
