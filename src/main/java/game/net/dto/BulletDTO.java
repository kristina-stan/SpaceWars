package game.net.dto;

public class BulletDTO {
    private String id;
    private double x;
    private double y;
    private boolean friendly;

    public BulletDTO() {}

    public BulletDTO(String id, double x, double y, boolean friendly) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.friendly = friendly;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isFriendly() {
        return friendly;
    }

    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
    }
}
