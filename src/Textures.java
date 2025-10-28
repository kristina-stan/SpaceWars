

import java.awt.image.BufferedImage;

public class Textures {

    public static BufferedImage[] player = new BufferedImage[6];// for staying on one place
    public static BufferedImage[] missle = new BufferedImage[2];
    public static BufferedImage[] enemy = new BufferedImage[6]; //for the 3 types
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

       // green enemy
       enemy[0] = ss.grabImage(3,1,32,32);
       enemy[1] = ss.grabImage(3,2,32,32);
       // blue enemy
       enemy[2] = ss.grabImage(4,1,32,32);
       enemy[3] = ss.grabImage(4,2,32,32);
       // yellow enemy
       enemy[4] = ss.grabImage(5,1,32,32);
       enemy[5] = ss.grabImage(5,2,32,32);
    }
}
