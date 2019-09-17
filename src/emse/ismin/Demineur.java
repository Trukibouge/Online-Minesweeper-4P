package emse.ismin;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Truki
 * @version 0.1, 2019/9/3
 */
public class Demineur extends JFrame {
	
	private Level difficulty = Level.EASY;
	private int score = 0;
	private Champ champ = new Champ(difficulty);
    private DemineurGUI appGui;
    private int remainingSquares = champ.GetDim(difficulty)*champ.GetDim(difficulty) - champ.getInitialMineNumber(difficulty);
    
    private boolean started = false;
    private boolean lost = false;
    private boolean won = false;

    public void setStarted (boolean started){
        this.started = started;
    }

    public boolean isStarted(){
        return started;
    }
	
	public DemineurGUI getAppGui() {
		return appGui;
	}

	/**
	 * Create mine field
	 */
	public Demineur() {
		super("Demineur");
		champ.newGame();
		appGui = new DemineurGUI(this);
		setContentPane(appGui);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Demineur();
	}
	
	public void quit() {
		System.out.println("Exiting app...");
		System.exit(0);
	}
	
	public Champ getChamp() {
		return (champ);
	}
	
	public Level getLevel() {
		return(difficulty);
	}
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void reset() {
        champ.newGame();
        started = false;
        lost = false;
        score = 0;
        remainingSquares = champ.GetDim(difficulty)*champ.GetDim(difficulty) - champ.getInitialMineNumber(difficulty);
	}
	
	public void newDifficulty(Level difficulty) {
        this.difficulty = difficulty;
        this.champ = new Champ(difficulty);
		reset();
	}

    public boolean isLost() {
        return lost;
    }

    public void setLost(boolean lost) {
        this.lost = lost;
    }

    public int getRemainingSquares() {
        return remainingSquares;
    }

    public void setRemainingSquares(int remainingMines) {
        this.remainingSquares = remainingMines;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public void WriteScore(){
        try{
            FileOutputStream file = new FileOutputStream("scores.dat");
            BufferedOutputStream buffer = new BufferedOutputStream(file);
            DataOutputStream os = new DataOutputStream(buffer);

            String dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
            String output = dateTime + " - CLEAR TIME: " + appGui.getCompteur().getTime() + "\n";
            os.writeBytes(output);
            os.close();
        }

        catch(IOException e){
            e.printStackTrace();
        }
    }
}
