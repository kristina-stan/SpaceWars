package game.graphics;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class MovingBackground extends JPanel {
    private static final int DIM_W = 350;
    private static final int DIM_H = 350;
    private static final int INCREMENT = 10;

    private BufferedImage backgroundImage;

    private int dx1, dy1, dx2, dy2;
    private int srcx1, srcy1, srcx2, srcy2;
    private int IMAGE_WIDTH;

    public MovingBackground(BufferedImage backgroundImage) {
        initImagePoints();
        Timer timer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveBackground();
                repaint();
            }
        });
        timer.start();

        FlowLayout layout = (FlowLayout)getLayout();
        layout.setHgap(0);
        layout.setVgap(0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(backgroundImage, dx1, dy1, dx2, dy2, srcx1, srcy1,
                srcx2, srcy2, this);
        //g.drawImage(runnerImage, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 350);
    }

    private void initImagePoints() {
        dx1 = 0;
        dy1 = 0;
        dx2 = DIM_W;
        dy2 = DIM_H;
        srcx1 = 0;
        srcy1 = 0;
        srcx2 = DIM_W;
        srcy2 = DIM_H;
    }



    private void moveBackground() {
        if (srcx1 > IMAGE_WIDTH) {
            srcx1 = 0 - DIM_W;
            srcx2 = 0;
        } else {
            srcx1 += INCREMENT;
            srcx2 += INCREMENT;
        }
    }
}