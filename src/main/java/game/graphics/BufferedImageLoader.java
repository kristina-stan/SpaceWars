package game.graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

// gets the sheet with ALL images
public class BufferedImageLoader {

    public BufferedImage loadImage(String path) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(getClass().getClassLoader().getResourceAsStream(path));
            if (img == null) {
                throw new IOException("Cannot find resource: " + path);
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    public BufferedImage loadSpriteSheet() {
        return loadImage("res/sprite_sheets.png");
    }

    public BufferedImage loadBackground(){
        return loadImage("res/background.png");
    }

}

