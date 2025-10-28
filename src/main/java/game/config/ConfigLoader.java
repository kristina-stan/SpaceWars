package game.config;

import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;

public class ConfigLoader {
    public static GameConfig loadConfig() {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config/game_balance.json")) {
            if (input == null) {
                throw new RuntimeException("Config file not found in resources!");
            }
            Gson gson = new Gson();
            return gson.fromJson(new InputStreamReader(input), GameConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + e.getMessage(), e);
        }
    }

}
