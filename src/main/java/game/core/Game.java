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
import game.managers.EnemySpawner;
import game.managers.PlayerUpgader;
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

    private static final int TARGET_FPS =120;
    private static final double NS_PER_FRAME = 1_000_000_000.0 / TARGET_FPS;

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
   private EnemySpawner enemySpawner;
   private PlayerUpgader playerManager;
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

        enemySpawner = new EnemySpawner(this, tex, c);
        playerManager = new PlayerUpgader(p, this, tex);
        upgradeManager = new UpgradeManager(this, playerManager, config);

        ea = c.getEntityA();
        eb = c.getEntityB();

        this.addKeyListener((KeyListener) new KeyInput(this));
        this.addMouseListener((MouseListener) new MouseInput(this));

        //enemySpawner.spawnGruntWave();
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
        long timer = System.currentTimeMillis();
        int frames = 0;
        int updates = 0;

        double ns = 1_000_000_000.0;
        double deltaTime = 0; // time passed

        while (running){
            long now = System.nanoTime(); // again cause from 37line to 43 it takes time
            deltaTime += (now - lastTime) / ns;
            lastTime = now;

            tick(deltaTime);
            render();
            updates++;
            frames++;

            long frameTime = System.nanoTime() - now;
            if(frameTime < NS_PER_FRAME) {
                try {
                    Thread.sleep((long) ((NS_PER_FRAME - frameTime) / 1_000_000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println("FPS: " + frames + ", TICKS: " + updates);
                updates = 0;
                frames = 0;
            }
        }
        stop();
    }

    private long lastWaveTime = System.currentTimeMillis();
    private final long waveInterval = 5000; // 5 seconds between waves
    private int shooterInterval = 4000; // 4 seconds between shooters
    private long lastShooterTime = System.currentTimeMillis();

    //---------- GAME LOGIC UPDATES each tick ----------
    void tick(double deltaTime){
        if(State == STATE.GAME) {

            p.tick(deltaTime);
            c.tick(deltaTime);

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastWaveTime >= waveInterval){
                lastWaveTime = currentTime;
                enemySpawner.spawnGruntWave();
            }
            if (currentTime - lastShooterTime >= shooterInterval){
            lastShooterTime = currentTime;
            enemySpawner.spawnShooter();
        }
            
        }
    }

    void render(){ // everything that renders

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
            case GAME -> {
                p.render(g);
                c.render(g);
                menu.renderGame(g, String.valueOf(p.getPoints()));
            }
            case PAUSE -> {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this); // rend black screen
                menu.renderPause(g);
            }
            case MENU -> {
                resetGame();
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this); // rend black screen
                menu.renderMenu(g);
            }
            case HELP -> g.drawImage(background, 0, 0, getWidth(), getHeight(), this); // rend black screen
            default -> {
            }
        }
        //////// to here drown
        g.dispose();
        bs.show();

    }

    //---------- INPUT HANDLING ----------
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();

        if(null != State) switch (State) {
            case GAME -> {
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
                }
            }
            case MENU -> {
                if (key == KeyEvent.VK_ENTER) {
                    State = STATE.GAME;
                }
            }
            case PAUSE -> {
                if(key == KeyEvent.VK_ESCAPE) {
                    State = STATE.GAME;
                }
            }
            default -> {
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_RIGHT -> p.setVelX(0);
            case KeyEvent.VK_LEFT -> p.setVelX(0);
            case KeyEvent.VK_DOWN -> p.setVelY(0);
            case KeyEvent.VK_UP -> p.setVelY(0);
            case KeyEvent.VK_SPACE -> is_shootinng = false;
            default -> {
            }
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

    public void resetGame(){
        p = new Player(400, 700, tex, c, this, config);

        c.getEntityA().clear();
        c.getEntityB().clear();

        enemy_killed = 0;
        lastWaveTime = System.currentTimeMillis();

        //playerManager.resetUpgrades();
        //upgradeManager.resetUpgrades();

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
    public Player getPlayer(){
        return this.p;
    }
}