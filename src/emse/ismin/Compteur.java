package emse.ismin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 *  Timer class with its display
 *  @author Truki
 */

public class Compteur extends JPanel implements Runnable {

    private static final long serialVersionUID = 6323791352452768050L;
    final private static int WIDTH = 35;
    final private static int HEIGHT = 25;

    private int time;
    private Thread timerThread;

    /**
     * Thread Run
     */
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

    /**
     * Paint compteur
     * @param g
     */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.orange);
        g.drawString(String.valueOf(time), getWidth() / 2, getHeight() / 2);
        g.drawRect(0, 0, getWidth() - 1 , getHeight() - 1);
    }

    /**
     * Constructor
     */
    public Compteur(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
    }

    /**
     * Reset the value of the timer
     */
    protected void resetTimer(){
        time = 0;
        timerThread = null;
        repaint();
    }

    /**
     * Start the timer
     */
    protected void startTimer(){
        time = 0;
        timerThread = new Thread(this);
        timerThread.start();
    }

    /**
     * Stop the timer
     */
    protected void stopTimer(){
        timerThread = null;
    }

    /**
     * Get the time value
     * @return time int
     */
    public int getTime() {
        return time;
    }

}