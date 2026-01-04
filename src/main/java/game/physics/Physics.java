package game.physics;

import java.util.LinkedList;

import game.entities.interfaces.EntityA;
import game.entities.interfaces.EntityB;

public class Physics {

    // Collision between Enemy/Player && Bullets
    public static boolean Collision(EntityA enta, EntityB entb){
        return enta.getBounds().intersects(entb.getBounds());
    }
    public static boolean Collision(EntityB entb, EntityA enta){
        return entb.getBounds().intersects(enta.getBounds());
    }
    public static boolean Collision(EntityA enta1, EntityA enta2){
        return enta1.getBounds().intersects(enta2.getBounds());
    }
    public static boolean CollisionAList(EntityA enta, LinkedList<EntityA> entityAList){
        for (EntityA entityA : entityAList) {
            if (enta.getBounds().intersects(entityA.getBounds())) {
                return true;
            }
        }
        return false;
    }

    public static boolean CollisionBList(EntityA enta, LinkedList<EntityB> entityBList){
        for (EntityB entityB : entityBList) {
            if (enta.getBounds().intersects(entityB.getBounds())) {
                return true;
            }
        }
        return false;
    }

}
