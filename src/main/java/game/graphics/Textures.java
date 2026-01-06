package game.graphics;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.core.Game;

public final class Textures {

    public static BufferedImage[] player = new BufferedImage[6];// for staying on one place
    public static BufferedImage[] missle = new BufferedImage[2];

    public static BufferedImage[] gEnemy = new BufferedImage[2];
    public static BufferedImage[] bEnemy = new BufferedImage[2];
    public static BufferedImage[] yEnemy = new BufferedImage[2];

    private SpriteSheet ss;

    public Textures(Game game){
        ss = new SpriteSheet(game.getSpriteSheet());
        getTextures();
    }

    public void getTextures(){
       player[0] = ss.grabImage(1,1,32,32); // in place
       player[1] = ss.grabImage(1,2,32,32); // in place
       player[2] = ss.grabImage(1,3,32,32); // speeding
       player[3] = ss.grabImage(1,4,32,32); //speeding
       player[4] = ss.grabImage(1,5,32,32); //going right
       player[5] = ss.grabImage(1,6,32,32); //going left

       missle[0] = ss.grabImage(2,1,32,32);
       missle[1] = ss.grabImage(2,2,32,32);
       
       gEnemy[0] = ss.grabImage(3,1,32,32);
       gEnemy[1] = ss.grabImage(3,2,32,32);

       bEnemy[0] = ss.grabImage(4,1,32,32);
       bEnemy[1] = ss.grabImage(4,2,32,32);
       
       yEnemy[0] = ss.grabImage(5,1,32,32);
       //yEnemy[1] = ss.grabImage(5,2,32,32);
    }

    public BufferedImage rotate(BufferedImage img) {
    int w = img.getWidth();
    int h = img.getHeight();

    BufferedImage result = new BufferedImage(w, h, img.getType());
    Graphics2D g2 = result.createGraphics();
    g2.rotate(Math.toRadians(180), w / 2, h / 2);
    g2.drawImage(img, 0, 0, null);
    g2.dispose();
    return result;
}


}
