package game.core;

public class GameLoop implements Runnable {

    private static final int TARGET_FPS = 60;
    private static final double NS_PER_TICK = 1_000_000_000.0 / TARGET_FPS;

    private final Game game;
    private Thread thread;
    private boolean running = false;

    public GameLoop(Game game) {
        this.game = game;
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        thread = new Thread(this, "GameLoop Thread");
        thread.start();
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    @Override
    public void run() {
        game.init();

        long lastTime = System.nanoTime();
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        int updates = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / NS_PER_TICK;
            lastTime = now;

            while (delta >= 1) {
                game.tick(delta);
                updates++;
                delta--;
            }

            game.render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames + " | TICKS: " + updates);
                frames = 0;
                updates = 0;
            }
        }

        stop();
    }
}
