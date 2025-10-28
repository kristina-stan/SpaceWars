package game.core;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import game.config.ConfigLoader;
import game.config.GameConfig;
import game.controller.Controller;
import game.entities.Bullet;
import game.entities.Player;
import game.entities.interfaces.EntityA;
import game.entities.interfaces.EntityB;
import game.graphics.BufferedImageLoader;
import game.graphics.Textures;
import game.input.KeyInput;
import game.input.MouseInput;
import game.managers.EnemyManager;
import game.managers.PlayerManager;
import game.managers.SpawnManager;
import game.managers.UpgradeManager;
import game.ui.Menu;

public class Game extends Canvas implements Runnable {

    //---------- WINDOW SETTINGS ----------
    public static final int WIDTH = 400;
    public static final int HEIGHT = WIDTH / 8 * 9;
    public static final int SCALE = 2;
    public final String TITLE = "2D Space Game";

    //---------- GAME SET ----------
    private boolean running = false;
    private Thread thread;

    //buffers the whole window
    private BufferedImage image = new BufferedImage(WIDTH*SCALE, HEIGHT*SCALE, BufferedImage.TYPE_INT_RGB);
    private BufferedImage spriteSheet = null;
    private BufferedImage background = null;

    //---------- PLAYER SHOOTING CONTROL ----------
    private boolean is_shootinng = false;

    //---------- ENEMY MANAGEMENT ----------
    private int enemy_count = 4; // how many to spawn
    private int enemy_killed = 0; // check how many lower types enemys to spawn

    //---------- GAME OBJECTS ----------
    private Player p;
    private Controller c;
    private Textures tex;
    private Menu menu;
    private GameConfig config;
   // private MovingBackground mb;

   // --------- MANAGERS ----------
   private EnemyManager enemyManager;
   private PlayerManager playerManager;
   private SpawnManager spawnManager;
   private UpgradeManager upgradeManager;

    public LinkedList<EntityA> ea; // bullet
    public LinkedList<EntityB> eb; // enemy

    public static int HEALTH = 100 * 2;

    public static enum STATE{
        MENU,
        GAME,
        PAUSE,
        HELP
    };
    public static STATE State = STATE.MENU;

    public void init(){
        requestFocus();

        //---------- LOAD SPRITESHEETS ----------
        BufferedImageLoader loader = new BufferedImageLoader();
        spriteSheet = loader.loadSpriteSheet();
        background = loader.loadBackground();

        //---------- LOAD GAME CONFIGURATION ----------
        config = ConfigLoader.loadConfig();
        System.out.println("Player max HP: " + config.player.max_health); // TEST -> Example usage of loaded config

        //---------- INITIALISE OBJECTS ----------
        tex = new Textures(this);
        c = new Controller(tex, this);
        p = new Player(400, 700, tex, c, this, config);
        menu = new Menu();

        enemyManager = new EnemyManager(c, this, tex);
        playerManager = new PlayerManager(p, this, tex);
        spawnManager = new SpawnManager(this, enemyManager, config);
        upgradeManager = new UpgradeManager(this, playerManager, config);

        ea = c.getEntityA();
        eb = c.getEntityB();

        this.addKeyListener((KeyListener) new KeyInput(this));
        this.addMouseListener((MouseListener) new MouseInput(this));

        enemyManager.spawnEnemies(5);
        //c.createEnemy(enemy_count);
        //mb = new MovingBackground(background);
    }

    //---------- THREAD MANAGEMENT ----------
    private synchronized void start(){ // start the Thread
        if(running)
            return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    private synchronized void stop(){ //synchronized deals with Threads
        if(!running)
            return;
        running = false;
        try {
            thread.join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    // ---------- GAME LOOP ----------
    @Override
    public void run() { // the hearth of the game, the loop that runs the game
        init();

        long lastTime = System.nanoTime(); // nano seconds
        final double amoundOfTicks = 60.0;
        double ns = 1000000000 / amoundOfTicks; // 60 times per second
        double delta = 0; // time passed
        int updates = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();

        while (running){
            long now = System.nanoTime(); // again cause from 37line to 43 it takes time
            delta += (now - lastTime) / ns;
            lastTime = now;
            if(delta >= 1) {
                tick();
                updates++;
                delta--;
            }
            render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println("FPS: " + frames + ", TICKS: " + updates);
                updates = 0;
                frames = 0;
            }
        }
        stop();
    }

    //---------- GAME LOGIC UPDATES each tick ----------
    private void tick(){
        if(State == STATE.GAME) {

            p.tick();
            c.tick();

            if (enemy_killed >= enemy_count) {
                enemy_count += 2;
                enemy_killed = 0;
                c.createEnemy(enemy_count);
            }
        }
    }

    private void render(){ // everything that renders

        // this is the Canvas class; null if BufferStrategy is not created
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3); // images line buffered ready to project, it increases performance
            return;
        }

        Graphics g = bs.getDrawGraphics();

        //---------- DRAWING THE GAME ----------
        g.drawImage(background,0,0, null);

        if(null != State)
        switch (State) {
            case GAME:
                p.render(g);
                c.render(g);
                break;
            case PAUSE:
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this); // rend black screen
                menu.renderPause(g);
                break;
            case MENU:
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this); // rend black screen
                menu.renderMenu(g);
                break;
            case HELP:
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this); // rend black screen
                break;
            default:
                break;
        }
        //////// to here drown
        g.dispose();
        bs.show();

    }

    //---------- INPUT HANDLING ----------
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();

        if(null != State) switch (State) {
            case GAME:
                if (key == KeyEvent.VK_RIGHT) {
                    p.setVelX(3);
                } else if (key == KeyEvent.VK_LEFT) {
                    p.setVelX(-3);
                } else if (key == KeyEvent.VK_DOWN) {
                    p.setVelY(3);
                } else if (key == KeyEvent.VK_UP) {
                    p.setVelY(-3);
                } else if (key == KeyEvent.VK_SPACE && !is_shootinng) {
                    is_shootinng = true;
                    c.addEntity(new Bullet(p.getX(), p.getY(), tex, this)); //creating/shooting a missle
                } else if (key == KeyEvent.VK_ESCAPE) {
                    State = STATE.PAUSE;
                }   break;
            case MENU:
                if (key == KeyEvent.VK_ENTER) {
                    State = STATE.GAME;
                }   break;
            case PAUSE:
                if(key == KeyEvent.VK_ESCAPE) {
                    State = STATE.GAME;
                }   break;
            default:
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_RIGHT:
                p.setVelX(0);
                break;
            case KeyEvent.VK_LEFT:
                p.setVelX(0);
                break;
            case KeyEvent.VK_DOWN:
                p.setVelY(0);
                break;
            case KeyEvent.VK_UP:
                p.setVelY(0);
                break;
            case KeyEvent.VK_SPACE:
                is_shootinng = false;
                break;
            default:
                break;
        }
    }

    public static void main(String args[]) {
        Game game = new Game();

        //Dimenstion - initialises spesified width,height
        game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

        JFrame frame = new JFrame(game.TITLE);
        frame.add(game);
       // frame.add(new MovingBackground(game.background));
        frame.pack(); //size the components accordingly
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false); //u CANT resize it
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.start();
    }

    public BufferedImage getSpriteSheet(){
        return spriteSheet;
    }

    public int getEnemy_count() {
        return enemy_count;
    }
    public int getEnemy_killed() {
        return enemy_killed;
    }

    public void setEnemy_count(int enemy_count) {
        this.enemy_count = enemy_count;
    }
    public void setEnemy_killed(int enemy_killed) {
        this.enemy_killed = enemy_killed;
    }
    public void resetHealth(){
        this.HEALTH = 100 * 2;
    }
    public GameConfig getConfig() {
        return config;
    }
}
