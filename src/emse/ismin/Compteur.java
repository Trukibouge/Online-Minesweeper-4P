package emse.ismin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *  @author Truki
 */

public class Compteur extends JPanel implements Runnable {

    final private static int WIDTH = 35;
    final private static int HEIGHT = 25;

    private int time;
    private Thread timerThread;

    @Override
    public void run(){
        while(timerThread != null){
            try{
                time++;
                repaint();  
                timerThread.sleep(1000);
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.orange);
        g.drawString(String.valueOf(time), getWidth() / 2, getHeight() / 2);
        g.drawRect(0, 0, getWidth() - 1 , getHeight() - 1);
    }

    public Compteur(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
    }

    protected void resetTimer(){
        time = 0;
        timerThread = null;
        repaint();
    }

    protected void startTimer(){
        time = 0;
        timerThread = new Thread(this);
        timerThread.start();
    }

    protected void stopTimer(){
        timerThread = null;
    }

    public int getTime() {
        return time;
    }

}