package game.entities;

import java.util.Iterator;
import java.util.LinkedList;

import game.controller.Controller;
import game.core.Game;
import game.entities.interfaces.EntityA;
import game.entities.interfaces.Shooter;
import game.graphics.Animation;
import game.graphics.Textures;
import game.physics.Physics;

// EnemyShooter is an enemy type that shoots projectiles at the player
public class EnemyShooter extends Enemy implements Shooter {
    private int shootTimer = 0;
    private LinkedList<EntityA> bullets;
 
    public EnemyShooter(double x, double y, Textures tex, Controller c, Game game) {
        super(
            x, y,
            tex, c, game,
            new Animation(tex.yEnemy[0]),
            "shooter",
            game.getConfig()
        );
        this.bullets = new LinkedList<>();
    }

    @Override
    public void tick(double deltaTime) {
        super.tick(deltaTime);
        super.y += speed;
        
        shootTimer++;
        shoot();

        // Check collision of bullets with player
        Iterator<EntityA> it = bullets.iterator();
        while (it.hasNext()) {
            EntityA bullet = it.next();

            if (Physics.Collision(game.getPlayer(), bullet)) {
                c.removeEntity(bullet);
                it.remove();
                game.getPlayer().hit(this.damage);
            }
        }

    }

    @Override
    public void shoot() {
        if (shootTimer >= 120) {
            shootTimer = 0;
            Bullet newBullet = new Bullet(x, y - 30, tex, game, false);
            this.bullets.add(newBullet); // add to local bullet list for THIS enemy
            c.addEntity(newBullet); // shoot downward
            
        }
    }

    public LinkedList<EntityA> getBullets() {
        return bullets;
    }



}
