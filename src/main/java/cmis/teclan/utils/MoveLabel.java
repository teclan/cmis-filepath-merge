package cmis.teclan.utils;


import javax.swing.*;
import java.awt.*;

public class MoveLabel  extends JLabel implements Runnable {

    private String text = null;

    private Thread thread = null;

    private int x = 0;

    private int w = 0, h = 0;

    public MoveLabel(String text) {
        super(text);
        this.text = text;
        thread = new Thread(this);
        thread.start();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        super.setText(text);
        this.text = text;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(this.getBackground());
        g.fillRect(0, 0, w = this.getWidth(), h = this.getHeight());
        g.setColor(this.getForeground());
        g.setFont(this.getFont());
        g.drawString(text, x, h - 2);
    }

    public void run() {
        while (true) {
            x -= 2;


            try {
                if (x < 0) {
                    x = w;
                }else {
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                e.fillInStackTrace();
            }


            this.repaint();


        }
    }
}
