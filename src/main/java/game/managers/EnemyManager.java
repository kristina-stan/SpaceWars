package game.managers;

import java.util.Random;

import game.controller.Controller;
import game.core.Game;
import game.entities.EnemyGrunt;
import game.entities.EnemyShooter;
import game.graphics.Textures;

public class EnemyManager {
    private Controller c;
    private Game game;
    private Textures tex;
    private Random rand = new Random();

    public EnemyManager(Controller c, Game game, Textures tex) {
        this.c = c;
        this.game = game;
        this.tex = tex;
    }

    public void spawnEnemies(int count) {
        for (int i = 0; i < count; i++) {
            int type = rand.nextInt(3);
            double x = rand.nextInt(Game.WIDTH * Game.SCALE);
            double y = -rand.nextInt(400);

            switch (type) {
                case 0 -> c.addEntity(new EnemyGrunt(x, y, tex, c, game));
                case 1 -> c.addEntity(new EnemyShooter(x, y, tex, c, game));
                //case 2 -> c.addEntity(new EnemyTank(x, y, tex, c, game));
            }
        }
    }
}
