package game.entities;


import java.awt.Rectangle;

import game.controller.Controller;
import game.core.Game;
import game.graphics.Textures;

public abstract class GameObject {

    public double x,y;
    public Textures tex;
    public Game game;
    public Controller c;

    GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }
    GameObject(double x, double y, Textures tex, Game game, Controller c) {
        this.x = x;
        this.y = y;
        this.tex = tex;
        this.game = game;
        this.c = c;
    }
    GameObject(double x, double y, Textures tex, Game game) {
        this.x = x;
        this.y = y;
        this.tex = tex;
        this.game = game;
    }

    public Rectangle getBounds(int width, int height){
        return new Rectangle((int)x, (int)y, width, height);
    }

}
