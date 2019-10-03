package emse.ismin;

import java.net.*;
import java.io.*;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JFrame;

/**
 * @author Truki
 * @version 0.1, 2019/9/3
 */
public class Demineur extends JFrame implements Runnable {
    
    /**
     *
     */
    private static final long serialVersionUID = 3899778202618430188L;
    public static int PORT = 10000;
    public static String HOSTNAME = "localhost";
    public static int MSG = 0;
    public static int POS = 1;
    public static int START = 2;
    public static int END = 3;
    public static int PLAYERNB = 4;
    public static int DEATH = 5;

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private Thread process;


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

    private boolean connected = false;
    
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
		//champ.newGame();
		appGui = new DemineurGUI(this);
		setContentPane(appGui);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
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
            socket = new Socket(hostName, port);
            outputStream =new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream.writeUTF(nickName);
            appGui.addMsg("Connected to " + hostName + ":" + port + " as " + nickName);
            connected = true;

            process = new Thread(this);
        }

        catch(UnknownHostException e){
            e.printStackTrace();
            appGui.addMsg("Cannot connect to "+ hostName + ":" + port);
        }

        catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Start thread");
        process.start();
    }

    private void listen(){
        try{
            int cmd = inputStream.readInt();
            System.out.println(cmd);
            if(cmd == Demineur.MSG){
                appGui.addMsg(inputStream.readUTF());
            }

            else if(cmd == Demineur.START){
                appGui.addMsg("Gogogo");
                started = true;
                appGui.getCompteur().startTimer();
            }

            else if(cmd == Demineur.PLAYERNB){
                playerNb = inputStream.readInt();
                appGui.addMsg("Your number: " + playerNb);
            }

            else if(cmd == Demineur.POS){
                int x = inputStream.readInt();
                int y = inputStream.readInt();
                int playerNb = inputStream.readInt();
                String value = inputStream.readUTF();
                boolean isMine = inputStream.readBoolean();
                remainingSquares = inputStream.readInt();
                appGui.updateRemainingMinesLabel();
                appGui.getDemineurPanelCases()[x][y].setMine(isMine);
                appGui.getDemineurPanelCases()[x][y].setCaseContent(value);
                appGui.getDemineurPanelCases()[x][y].setPlayer(playerNb);
                appGui.getDemineurPanelCases()[x][y].cellClicked();
                if(isMine && playerNb == this.playerNb){
                    lost = true;
                    sendDeath();
                    appGui.onDeath();
                }
            }

            else if(cmd == Demineur.END){
                appGui.addMsg("Received end game");
            }
        }

        catch(IOException e){
            e.printStackTrace();
        }

    }

    protected void sendPos(int x, int y){
        try{
            System.out.println("Sending position to server: " + x + "/" + y);
            outputStream.writeInt(Demineur.POS);
            outputStream.writeInt(x);
            outputStream.writeInt(y);
            outputStream.writeInt(playerNb);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    protected void sendMsg(String msg){
        try{
            System.out.println("Sending message to server: " + msg);
            outputStream.writeInt(Demineur.MSG);
            outputStream.writeUTF(msg);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    protected void sendDeath(){
        try{
            System.out.println("Sending client death");
            outputStream.writeInt(Demineur.DEATH);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void run(){
        while(process != null){
            System.out.println("Starting to listen man");
            listen();
        }
    }

       

    /**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new Demineur();
	}

    public boolean isConnected() {
        return connected;
    }

}
