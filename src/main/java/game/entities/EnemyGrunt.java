package game.entities;

import game.controller.Controller;
import game.core.Game;
import game.graphics.Textures;

public class EnemyGrunt extends Enemy {
    public EnemyGrunt(double x, double y, Textures tex, Controller c, Game game) {
        super(x, y, tex, c, game, "grunt", game.getConfig());
        this.speed = 2 + r.nextInt(2);
    }

    @Override
    public void tick() {
        super.tick();
        // maybe small side-to-side movement
        x += Math.sin(y / 20.0);
    }
}
