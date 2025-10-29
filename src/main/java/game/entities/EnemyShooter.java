package game.entities;

import game.controller.Controller;
import game.core.Game;
import game.graphics.Textures;

// EnemyShooter is an enemy type that shoots projectiles at the player
public class EnemyShooter extends Enemy {
    private int shootTimer = 0;

    public EnemyShooter(double x, double y, Textures tex, Controller c, Game game) {
        super(x, y, tex, c, game, "shooter", game.getConfig());
        this.speed = 1;
    }

    @Override
    public void tick() {
        super.tick();
        shootTimer++;

        if (shootTimer >= 120) { // fires every 2 seconds at 60 FPS
            shootTimer = 0;
            c.addEntity(new Bullet(x + 16, y + 32, tex, game)); // shoot downward
        }
    }

    public void spawnEnemy() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'spawnEnemy'");
    }
}
