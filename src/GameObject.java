

import java.awt.Rectangle;

public class GameObject {

    public double x,y;

    GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds(int width, int height){
        return new Rectangle((int)x, (int)y, width, height);
    }

}
