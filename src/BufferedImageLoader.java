import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

// gets the sheet with ALL images
public class BufferedImageLoader {

    public BufferedImage loadImage(String path) throws IOException {
        BufferedImage img = ImageIO.read(getClass().getResource(path));
        if (img == null) {
            throw new IOException("Cannot find resource: " + path);
        }
        return img;
    }

}

