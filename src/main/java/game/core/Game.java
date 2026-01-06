package game.core;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import game.net.NetworkManager;
import game.ui.Menu;

public class Game extends Canvas implements Runnable {

    //---------- WINDOW SETTINGS ----------
    public static final int VIRTUAL_WIDTH = 800; 
    public static final int VIRTUAL_HEIGHT = 720;

    public double currentScale = 1.0;
    public int currentOffsetX = 0;   
    public int currentOffsetY = 0;

   // public static final int SCALE = 2;
    public final String TITLE = "2D Space Game";

    //---------- GAME SET ----------
    private boolean running = false;
    private Thread thread;

    private static final int TARGET_FPS =120;
    private static final double NS_PER_FRAME = 1_000_000_000.0 / TARGET_FPS;

    //buffers the whole window
    private BufferedImage image = new BufferedImage(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
    private BufferedImage spriteSheet = null;
    private BufferedImage background = null;

    //---------- ENEMY MANAGEMENT ----------
    private int enemy_count = 4; // how many to spawn
    private int enemy_killed = 0; // check how many lower types enemys to spawn

    //---------- GAME OBJECTS ----------
    private Player p;
    private Controller c;
    private Textures tex;
    private Menu menu;
    private GameConfig config;
    //private PlayerInputs playerInputs;
   // private MovingBackground mb;

   // --------- MANAGERS ----------
   private NetworkManager networkManager;
   private EnemySpawner enemySpawner;
   private PlayerUpgader playerManager;
   private UpgradeManager upgradeManager;

    public LinkedList<EntityA> ea; // bullet
    public LinkedList<EntityB> eb; // enemy

    private long lastSurvivalPointTime = System.currentTimeMillis();
    private final int SURVIVAL_POINT_INTERVAL_MS = 1000; // 1 second
    private final int SURVIVAL_POINTS_PER_INTERVAL = 2;

    public static enum STATE{
        MENU,
        GAME,
        PAUSE,
        HELP,
        GAMEOVER,
        WAITING_ROOM,
        HOST,
        JOIN,
    };
    public static STATE State = STATE.MENU;

    public void init(){
        requestFocus();
        networkManager = new NetworkManager(this);

        //---------- LOAD SPRITESHEETS ----------
        BufferedImageLoader loader = new BufferedImageLoader();
        spriteSheet = loader.loadSpriteSheet();
        background = loader.loadBackground();

        //---------- LOAD GAME CONFIGURATION ----------
        config = ConfigLoader.loadConfig();

        //---------- INITIALISE OBJECTS ----------
        tex = new Textures(this);
        c = new Controller(tex, this);
        p = new Player(VIRTUAL_WIDTH/2, 0, tex, c, this, config);
        menu = new Menu();

        //playerInputs = new PlayerInputs(p);
        this.addKeyListener((KeyListener) new KeyInput(this));
        this.addMouseListener((MouseListener) new MouseInput(this, menu));

        enemySpawner = new EnemySpawner(this, tex, c);
        //playerManager = new PlayerUpgader(p, this, tex);
        //upgradeManager = new UpgradeManager(this, playerManager, config);

        ea = c.getEntityA();
        eb = c.getEntityB();

    }

    //---------- THREAD MANAGEMENT ----------
    private synchronized void start(){ // start the Thread
        if(running)
            return;
        running = true;
        thread = new Thread(this);
        thread.start();
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
            deltaTime = (now - lastTime) / ns;
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
//System.out.println("FPS: " + frames + ", TICKS: " + updates);
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

            // Add network updates
            if (networkManager != null) {
                networkManager.tick();
                networkManager.update(deltaTime);
            }

            long currentWaveTime = System.currentTimeMillis();

            // Only spawn enemies if single player OR host in multiplayer
            // Clients receive enemies from server, so they don't spawn
            boolean canSpawnEnemies = !networkManager.isMultiplayerMode() ||
                    (networkManager.isMultiplayerMode() &&
                            networkManager.isHosting());

            if (canSpawnEnemies) {
                if (currentWaveTime - lastWaveTime >= waveInterval){
                    lastWaveTime = currentWaveTime;
                    enemySpawner.spawnGruntWave();
                }
                if (currentWaveTime - lastShooterTime >= shooterInterval){
                    lastShooterTime = currentWaveTime;
                    enemySpawner.spawnShooter();
                }
            }

            // Survival points over time
            long currentPointTime = System.currentTimeMillis();
            if (currentPointTime - lastSurvivalPointTime >= SURVIVAL_POINT_INTERVAL_MS) {
                lastSurvivalPointTime = currentPointTime;
                p.addPoints(SURVIVAL_POINTS_PER_INTERVAL);
            }
        }

        // Check if multiplayer game should start
        if (State == STATE.WAITING_ROOM) {
            if (networkManager != null && networkManager.isGameReady()) {
                State = STATE.GAME;
            }
        }
    }

    void render(){ // everything that renders
        
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g; // Cast to Graphics2D for scaling
    
        // 1. Calculate the current actual size of the Canvas
        int actualWidth = getWidth();
        int actualHeight = getHeight();
    
        // 2. Determine the uniform scale factor to maintain the aspect ratio (letterboxing)
        double scaleX = (double) actualWidth / VIRTUAL_WIDTH;
        double scaleY = (double) actualHeight / VIRTUAL_HEIGHT;
    
        // 3. Calculate the scaled game area size
        this.currentScale = Math.min(scaleX, scaleY);
        int scaledGameWidth = (int) (VIRTUAL_WIDTH * currentScale);
        int scaledGameHeight = (int) (VIRTUAL_HEIGHT * currentScale);
    
        // 4. Calculate the offset to center the game area
        this.currentOffsetX = (actualWidth - scaledGameWidth) / 2;
        this.currentOffsetY = (actualHeight - scaledGameHeight) / 2;

        // 5. Clear the screen with black (for the letterbox bars)
        g2d.setColor(java.awt.Color.BLACK);
        g2d.fillRect(0, 0, actualWidth, actualHeight);
    
            // 6. Apply the transformation: Translate (move) and then Scale
        g2d.translate(currentOffsetX, currentOffsetY);
        g2d.scale(currentScale, currentScale);
        g2d.drawImage(background,0,0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, null);

        if(null != State)
        switch (State) {
            case GAME -> {
                p.render(g2d); // Use g2d here
                c.render(g2d); // Use g2d here

                if (networkManager != null) {
                    networkManager.render(g2d);
                }
                boolean isMultiplayer = networkManager != null && networkManager.isMultiplayerMode();
                int playerCount = isMultiplayer ? networkManager.getPlayerCount() : 1;

                menu.renderGame(
                        g2d,
                        String.valueOf(p.getPoints()),
                        getPlayer().getCurrent_health(),
                        getPlayer().getMax_health(),
                        isMultiplayer,
                        playerCount
                );
            }
            case PAUSE -> {
                g2d.drawImage(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, this);
                menu.renderPause(g2d);
            }
            case MENU -> {
                g2d.drawImage(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, this);
                menu.renderMenu(g2d);
                resetGame();
            }
            case HELP -> {
                g2d.drawImage(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, this);
                menu.renderHelp(g2d);
            }
            case GAMEOVER -> {
                g2d.drawImage(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, this);
                menu.renderGameOver(g2d, p.getPoints());
            }
            // ADD THESE NEW CASES:
            case HOST -> {
                g2d.drawImage(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, this);
                menu.renderHost(g2d);
            }
            case JOIN -> {
                g2d.drawImage(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, this);
                menu.renderJoin(g2d);
            }
            case WAITING_ROOM -> {
                g2d.drawImage(background, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT, this);
                menu.renderWaitingRoom(g2d, networkManager.isHosting(), networkManager.getServerIp());
            }
            default -> {}
        }
    
        // Since everything is transformed, drawing stops here.
        g.dispose();
        bs.show();
    }

    public static void main(String args[]) {
        Game game = new Game();

        //Dimenstion - initialises spesified width,height
        game.setPreferredSize(new Dimension(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));

        JFrame frame = new JFrame(game.TITLE);
        frame.add(game);

       // frame.add(new MovingBackground(game.background));
        frame.pack(); //size the components accordingly
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setResizable(true); //u CANT resize it
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.start();
    }

    public void gameOver(){
        State = STATE.GAMEOVER;
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

    private boolean isShooting = false;
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
                } else if (key == KeyEvent.VK_SPACE && !isShooting) {
                    isShooting = true;
                    if (p.canShoot())
                        p.shoot();
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
            case GAMEOVER -> {
                if (key == KeyEvent.VK_ENTER) {
                    State = STATE.GAME;
                }
                else if (key == KeyEvent.VK_ESCAPE) {
                    // Disconnect from multiplayer if active
                    if (networkManager != null && networkManager.isMultiplayerMode()) {
                        networkManager.disconnect();
                    }
                    State = STATE.MENU;
                }
            }
            // ADD THESE NEW CASES:
            case HELP -> {
                if (key == KeyEvent.VK_ESCAPE) {
                    State = STATE.MENU;
                }
            }
            case HOST -> {
                if (key == KeyEvent.VK_ESCAPE) {
                    State = STATE.MENU;
                }
            }
            case JOIN -> {
                if (key == KeyEvent.VK_ESCAPE) {
                    State = STATE.MENU;
                }
            }
            case WAITING_ROOM -> {
                if (key == KeyEvent.VK_ESCAPE) {
                    if (networkManager != null) {
                        networkManager.disconnect();
                    }
                    State = STATE.MENU;
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
            case KeyEvent.VK_SPACE -> isShooting = false;
            default -> {
            }
        }
    }

    public void startMultiplayer(String host, int port) {
        networkManager.connect(host, port);
        State = STATE.GAME;
    }

    public BufferedImage getSpriteSheet(){
        return spriteSheet;
    }

    private synchronized void stop(){
        if(!running)
            return;
        running = false;

        // ADD: Disconnect network
        if (networkManager != null) {
            networkManager.disconnect();
        }

        try {
            thread.join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    // Getter for network manager (if needed elsewhere)
    public NetworkManager getNetworkManager() {
        return networkManager;
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
        Player player = getPlayer();
        player.setCurrent_health(player.getMax_health());
    }
    public GameConfig getConfig() {
        return config;
    }
    public Player getPlayer(){
        return this.p;
    }
}