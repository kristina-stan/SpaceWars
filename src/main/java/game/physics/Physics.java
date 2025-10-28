package game.physics;

import game.entities.EntityA;
import game.entities.EntityB;

public class Physics {

    // Collision between Enemy/Player && Bullets
    public static boolean Collision(EntityA enta, EntityB entb){
        if (enta.getBounds().intersects(entb.getBounds())){
            return true;
        }
        return false;
    }
    public static boolean Collision(EntityB entb, EntityA enta){
        if (entb.getBounds().intersects(enta.getBounds())){
            return true;
        }
        return false;
    }

}
