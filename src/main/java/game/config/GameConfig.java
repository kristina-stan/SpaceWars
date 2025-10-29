package game.config;

import java.util.Map;

public class GameConfig {
    public PlayerConfig player;
    public Map<String, EnemyConfig> enemies;
    public WavesConfig waves;
    public Map<String, PowerupConfig> powerups;

    public static class PlayerConfig {
        public int max_health;
        public int current_health;
        public int damage;
        public double speed;
        public double fire_rate;
        public double hitbox_scale;
        public int hp_regen;
        public double shield_duration;
        public int points;
    }

    public static class EnemyConfig {
        public int max_health;
        public int damage;
        public int speed;
        public double hitbox_scale;
        public double spawn_rate;
        public int reward;
    }

    public static class WavesConfig {
        public double spawn_interval;
        public double spawn_increase_rate;
        public double enemy_hp_scale;
        public double enemy_speed_scale;
        public double enemy_damage_scale;
    }

    public static class PowerupConfig {
        public double chance;
        public String effect;      // for health_restore
        public double duration;    // for shield, rapid_fire, spread_shot
        public double multiplier;  // for rapid_fire and score_boost
    }
}
