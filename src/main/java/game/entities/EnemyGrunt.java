package game.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.controller.Controller;
import game.core.Game;
import game.graphics.Animation;
import game.graphics.Textures;

// EnemyGrunt is a basic enemy type with moderate speed and health, but does no damage
public class EnemyGrunt extends Enemy {

    public EnemyGrunt(double x, double y, Textures tex, Controller c, Game game) {
        super(
            x, y,
            tex, c, game,
            new Animation(Textures.bEnemy[0], Textures.bEnemy[1]),
            "grunt",
            game.getConfig()
        );
    }

    @Override
    public void tick(double deltaTime){
        super.tick(deltaTime);
        super.y += speed;
        // maybe small side-to-side movement
        x += Math.sin(y / 20.0);
    }

    private static final Map<Integer, List<int[]>> gruntPatterns = new HashMap<>();
    static {
        // Wave 5
        gruntPatterns.put(5, List.of(
            new int[]{0,1,2},
            new int[]{1,2}
        ));
        // Wave 6
        gruntPatterns.put(6, List.of(
            new int[]{0,1,2},
            new int[]{1,2},
            new int[]{2}
        ));
        // Wave 7
        gruntPatterns.put(7, List.of(
            new int[]{0,1,2,3},
            new int[]{1,3},
            new int[]{2}
        ));
        // Wave 8
        gruntPatterns.put(8, List.of(
            new int[]{0,1,2,3},
            new int[]{1,2,3},
            new int[]{1,2}
        ));
        // Wave 9
        gruntPatterns.put(9, List.of(
            new int[]{0,1,2,3},
            new int[]{1,2,3},
            new int[]{1,2}
        ));
        // Wave 10
        gruntPatterns.put(10, List.of(
            new int[]{0,1,2,3},
            new int[]{1,2,3},
            new int[]{1,2},
            new int[]{2}
            ));
    }

    public static List<int[]> getPattern(int waveSize){
        return gruntPatterns.getOrDefault(waveSize, gruntPatterns.get(5));
    }

}
