package game.controller;


import java.awt.Graphics;
import java.util.LinkedList;
import java.util.Random;

import game.entities.Enemy;
import game.entities.interfaces.EntityA;
import game.entities.interfaces.EntityB;
import game.core.Game;
import game.graphics.Textures;

public class Controller {

    private LinkedList<EntityA> ea = new LinkedList<EntityA>();
    private LinkedList<EntityB> eb = new LinkedList<EntityB>();
    private Textures tex;
    private Game game;
    EntityA enta;
    EntityB entb;

    Random r = new Random();

    public Controller(Textures tex, Game game){
        this.tex = tex;
        this.game = game;
    }

    public void createEnemy(int enemy_count){
        for(int i = 0; i < enemy_count; i++){
            addEntity(new Enemy(r.nextInt(Game.WIDTH * Game.SCALE), 0, tex, this, game));
        }
    }


    // Spawn moving obj
    public void tick(){
        //A CLASS
        for(int i = 0; i < ea.size(); i++){
            enta = ea.get(i);

            enta.tick();
        }
        //B CLASS
        for(int i = 0; i < eb.size(); i++){
            entb = eb.get(i);

            entb.tick();
        }
    }

    // Undated moving obj
    public void render(Graphics g){
        //A CLASS
        for(int i = 0; i < ea.size(); i++){
            enta = ea.get(i);

            enta.render(g);
        }
        //B CLASS
        for(int i = 0; i < eb.size(); i++){
            entb = eb.get(i);

            entb.render(g);
        }
    }

    public void addEntity(EntityA block){
        ea.add(block);
    }
    public void removeEntity(EntityA block){
        ea.remove(block);
    }
    public void addEntity(EntityB block){
        eb.add(block);
    }
    public void removeEntity(EntityB block){
        eb.remove(block);
    }

    public LinkedList<EntityA> getEntityA(){
        return ea;
    }
    public LinkedList<EntityB> getEntityB() {
        return eb;
    }
}
