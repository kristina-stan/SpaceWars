package game.managers;

import java.util.List;
import java.util.Random;

import game.controller.Controller;
import game.core.Game;
import game.entities.EnemyGrunt;
import game.graphics.Textures;

public class EnemySpawner {

    private Game game;
    private Textures tex;
    private Controller controller;
    private Random random = new Random();

    public EnemySpawner(Game game, Textures tex, Controller controller) {
        this.game = game;
        this.tex = tex;
        this.controller = controller;
    }

    public void spawnGruntWave() {
        int waveSize = 5 + random.nextInt(6); // Spawn 5-10 grunts per wave
        List<int[]> pattern = EnemyGrunt.getPattern(waveSize);

        int startX = random.nextInt(Game.WIDTH - 32);
        int startY = -10;
        int spacingX = 80;
        int spacingY = 60;

        for (int row = 0; row < pattern.size(); row++){
            for (int col : pattern.get(row)) {
                int x = startX + col * spacingX;
                int y = startY - row * spacingY;
                controller.addEntity(new EnemyGrunt(x, y, tex, controller, game));
            }
            System.out.println("Spawned grunt wave with " + waveSize + " enemies.");
        }

    }

    public void spawnShooter() {
        double x = random.nextInt(Game.WIDTH - 32);
        double y = -10;
        controller.addEntity(new EnemyGrunt(x, y, tex, controller, game));
        System.out.println("Spawned shooter.");
    }
    
}
