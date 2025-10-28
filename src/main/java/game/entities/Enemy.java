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

    Random r = new Random();
    Animation anim;

    public Enemy(double x, double y, Textures tex, Controller c, Game game,
        String type, GameConfig config){
        super(x, y, tex, game, c);
        this.type = type;

        GameConfig.EnemyConfig eConfig = config.enemies.get(type);
        this.max_health = eConfig.max_health;
        this.damage = eConfig.damage;
        this.reward = eConfig.reward;
        this.speed = eConfig.speed;
        this.spawn_rate = eConfig.spawn_rate;

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
