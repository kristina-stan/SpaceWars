package game.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Iterator;

import game.config.GameConfig;
import game.controller.Controller;
import game.core.Game;
import game.entities.interfaces.EntityA;
import game.entities.interfaces.EntityB;
import game.entities.interfaces.Shooter;
import game.graphics.Animation;
import game.graphics.Textures;
import game.physics.Physics;

public class Player extends GameObject implements EntityA, Shooter {

    // right++; left-- (original) and up--; down++; (inverted)

    //position
    private double velX = 0;
    private double velY = 0;

    @SuppressWarnings("unused")
    private int max_health, current_health, hp_regen, points;
    @SuppressWarnings("unused")
    private double damage, speed, fire_rate, shield_duration;

    // hit flash effect
    private boolean isHit = false;
    private long hitStartTime = 0;
    private final int HIT_FLASH_DURATION_MS = 300;

    // firing control
    private long lastFireTime = 0;
    
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

        anim[0] = new Animation(Textures.player[0], Textures.player[1]); // idle
        anim[1] = new Animation(Textures.player[2], Textures.player[3]); // moving up
        anim[2] = new Animation(Textures.player[4]); // moving right
        anim[3] = new Animation(Textures.player[5]); // moving left
        
    }

    @Override
    public void tick(double deltaTime){
        x += velX;
        y += velY;
        
        // hit flash effect
        if(isHit){
            long elapsed = System.currentTimeMillis() - hitStartTime;
            if(elapsed > HIT_FLASH_DURATION_MS){
                isHit = false;
            }
        }

        // collition with end of screen
        if(x <= 0) x = 0;
        else if (x >= (Game.VIRTUAL_WIDTH - 32)) x = Game.VIRTUAL_WIDTH - 32;
        if(y <= 0) y = 0;
        else if (y >= (Game.VIRTUAL_HEIGHT - 32)) y = Game.VIRTUAL_HEIGHT - 32;

        // check collision with enemies
        Iterator<EntityB> it = game.eb.iterator();
        while (it.hasNext()) { 
            EntityB enemy = it.next();

            if (Physics.Collision(this, enemy)) {
                it.remove();
                this.hit(10);
                removePoints(5); // penalty for getting hit by enemy
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
        // Flash settings
        long elapsed = System.currentTimeMillis() - hitStartTime;
        long stepDuration = 40; // milliseconds per flash step
        int stepsPerCycle = 5;   // number of steps in a full blink cycle

        // Only draw the player if not hit or currently in visible part of flash cycle
        if(!isHit || (elapsed / stepDuration) % stepsPerCycle == 0){
           if(getVelX() > 0) anim[2].drawAnimation(g, x, y, 0);
            else if(getVelX() < 0) anim[3].drawAnimation(g, x, y, 0);
            else if(getVelY() < 0) anim[1].drawAnimation(g, x, y, 0);
            else anim[0].drawAnimation(g, x, y, 0);
        }
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
    
    @Override
    public boolean getIsFriendly() {
        return true;
    }
    @Override
    public void shoot(){
        c.addEntity(new Bullet(getX(), getY() + 30, tex, game, true));       
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
    public void addPoints(int bonusPoints){
        this.points += bonusPoints;
    }
    public void removePoints(int penaltyPoints){
        this.points -= penaltyPoints;
        if(this.points < 0){
            this.points = 0;
        }
    }
    public int getPoints(){
        return this.points;
    }
    public void setPoints(int points){
        this.points = points;
    }
    public int getCurrent_health(){
        return this.current_health;
    }
    public void setCurrent_health(int health){
        this.current_health = health;
    }
    public int getMax_health(){
        return this.max_health;
    }
    public void heal(int amount){
        this.max_health += amount;
        if(this.max_health > 100) this.max_health = 100;
    }
    
    public void hit(double damage){
        this.current_health -= damage;
        isHit = true;
        hitStartTime = System.currentTimeMillis();

        if(this.current_health <= 0){
            game.gameOver();
        }
    }

    public boolean canShoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFireTime >= fire_rate * 700) {
        lastFireTime = currentTime;
        return true;
        }
        return false;
    }


}