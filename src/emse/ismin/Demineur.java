package emse.ismin;

import java.net.*;
import java.io.*;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Truki
 * @version 0.1, 2019/9/3
 */
public class Demineur extends JFrame implements Runnable {
    
    public static int PORT = 10000;
    public static String HOSTNAME = "localhost";
    public static int MSG = 0;
    public static int POS = 1;
    public static int START = 2;
    public static int END = 3;

    private Thread process;
    private DataOutputStream output;
    private DataInputStream input;

	private Level difficulty = Level.MEDIUM;
	private int score = 0;
	private Champ champ = new Champ(difficulty);
    private DemineurGUI appGui;
    private int remainingSquares = champ.GetDim(difficulty)*champ.GetDim(difficulty) - champ.getInitialMineNumber(difficulty);
    
    private boolean started = false;
    private boolean lost = false;
    private boolean won = false;

    private static String FILENAME = "scores.dat";

    private int playerNb;
    
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
            Path path = Paths.get(FILENAME);

            if(!Files.exists(path)){
                for(int i = 0; i<Level.values().length; i++){
                    //if(difficulty)
                }
            }

            FileOutputStream file = new FileOutputStream(FILENAME);
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


    protected void connect(String hostName, int port, String nickName){
        System.out.println("Connecting to " + hostName + ":" + port + " as " + nickName);

        try{
            Socket socket = new Socket(hostName, port);
            DataOutputStream output =new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
            output.writeUTF(nickName);
            System.out.println("Connected!");
            appGui.addMsg("Connected to " + hostName + ":" + port + " as " + nickName);

            String serverMessage = input.readUTF();
            appGui.addMsg(serverMessage);

            process = new Thread(this);
        }

        catch(UnknownHostException e){
            e.printStackTrace();
            appGui.addMsg("Cannot connect to "+ hostName + ":" + port);
        }

        catch(IOException e){
            e.printStackTrace();
        }
        
    }

    private void listen(){
        try {
            String serverMessage = input.readUTF();
            appGui.addMsg(serverMessage);
        } 
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run(){
        try{
            while(process != null){
                int cmd = input.readInt();
                if(cmd == Demineur.MSG){
                    String msg = input.readUTF();
                    appGui.addMsg(msg);
                }
    
                else if(cmd == Demineur.POS){
    
                } 
    
                else if(cmd == Demineur.START){
    
                }
    
                else if(cmd == Demineur.END){
    
                }
            }
        }

        catch(IOException e){
            e.printStackTrace();
        }


    // public void run(){
    //     listen();
    //     new Thread(this).start(); //start waiting for new client
    // }

    // public void run(){
    //     while(process != null){
    //         int cmd = input.readInt();
    //         if(cmd == Demineur.MSG){
    //             String msg = in.readInt();
    //             appGui.addMsg(msg);
    //         }
    //     }

    }
}
