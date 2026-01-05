package game.net.dto;

import java.util.List;

public class BulletUpdateMessage extends GameMessage {
    private List<BulletDTO> bullets;

    public BulletUpdateMessage() {
        super("bullet_update");
    }

    public BulletUpdateMessage(List<BulletDTO> bullets) {
        super("bullet_update");
        this.bullets = bullets;
    }

    public List<BulletDTO> getBullets() { return bullets; }
    public void setBullets(List<BulletDTO> bullets) { this.bullets = bullets; }
}
