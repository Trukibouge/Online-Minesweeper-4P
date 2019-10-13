package emse.ismin;

import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import javax.swing.JFrame;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import emse.ismin.Demineur;
import emse.ismin.Level;
import emse.ismin.Champ;

                //Collection 1 ok
//Bonus 5
                //Deconnexion (bonus) ok
//IHM (bonus)
                //Score (bonus) ok
                //Chat (bonus) ok
                //Couleurs 2 ok
                //Solo 4 ok
                //Niveau Solo 2 ok
//Fichier 1
                //Réseau: nombre de clients infini 1, position 2, fin de partie 1, affichage couleur 2 ok
                //Commentaires 1 Javadoc 1


/**
 * @author Truki
 * Server of Demineur.
 */
public class Server extends JFrame implements Runnable {

    private static final long serialVersionUID = -5822900338130207614L;
    private static String SERVERFILEDATA = "serverHighScore.dat";

    private ServerGUI gui;
    private int playerCount;
    private int givenColorCount;

    private Map<String, Integer> playerScore = new HashMap<String, Integer>();
    private Map<String, DataInputStream> inputStreamMap = new HashMap<String, DataInputStream>();
    private Map<String, DataOutputStream> outputStreamMap = new HashMap<String, DataOutputStream>();
    private Map<String, Timer> timerMap = new HashMap<String, Timer>();

    private ServerSocket socketManager;
    private Level serverDifficulty = Level.MEDIUM;
    private Champ champ = new Champ(serverDifficulty);
    private int remainingSquares = champ.GetDim(serverDifficulty)*champ.GetDim(serverDifficulty) - champ.getInitialMineNumber(serverDifficulty); //number of cell that are not mines

    private int deathCount = 0; //number of dead player

    /**
     * Constructor
     */
    public Server(){
        System.out.println("Starting server");
        champ.newGame();
        gui = new ServerGUI(this);
        setContentPane(gui);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        startServer();
    }

    /**
     * Start server and initialize a server socket
     */
    protected void startServer(){
        gui.addMsg("Waiting for clients...\n");     
        try {
            socketManager = new ServerSocket(Demineur.PORT);
            new Thread(this).start();
        } 
        
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new socket on a connection, initialize a player's instance
     * @param socketManager Server socket to be used by the server
     */
    private void createNewSocket(ServerSocket socketManager){
        try {
            Socket socket = socketManager.accept(); //new client
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            String playerNick;
            playerNick = input.readUTF() ;
            gui.addMsg(playerNick + " connected");
            playerCount++;
            givenColorCount++;


            inputStreamMap.put(playerNick, input);
            outputStreamMap.put(playerNick, output);
            playerScore.put(playerNick, 0);

            sendMsgToAll(playerNick + " has joined the game!");
            sendMsgToAll("Current number of players: " + Integer.toString(playerCount));

            gui.addMsg("Attributing player nb to: " + playerNick + " = " + givenColorCount);
            output.writeInt(Demineur.PLAYERNB);
            output.writeInt(givenColorCount);

            heartbeat(playerNick);

            int diffIndex = 1;
            switch(serverDifficulty){
                case EASY:
                diffIndex = 0;
                    break;
                    
                case MEDIUM:
                diffIndex = 1;
                    break;
    
                case HARD:
                diffIndex = 2;
                    break;
    
                case IMPOSSIBLE:
                diffIndex = 3;
                    break;
    
                case CUSTOM:
                diffIndex = 4;
                    break;
            }
            changeDiff(diffIndex);

            listen(playerNick);
        } 
        
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Check if a player is connected periodically
     * @param playerNick
     */
    private void heartbeat(String playerNick){
        int delay = 5000;
        ActionListener heartbeatTask = new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                checkConnection(playerNick);
            }
        };
        Timer heartbeatTimer = new Timer(delay, heartbeatTask);
        timerMap.put(playerNick, heartbeatTimer);
        heartbeatTimer.start();
    }

    private void checkConnection(String playerNick){
        try{
            System.out.println("Sent heartbeat to " + playerNick);
            outputStreamMap.get(playerNick).writeUTF("Heyman");
        }
        catch(IOException e){
            String msg = playerNick + " has disconnected :(";
            System.out.println(msg);
            gui.addMsg(msg);
            sendMsgToAll(msg);
            removePlayer(playerNick);
            System.out.println("Removed " + playerNick);
        }
    }

    /**
     * Remove a player in case of disconnection
     * @param playerNick
     */
    private void removePlayer(String playerNick){
        playerCount--;
        inputStreamMap.remove(playerNick);
        outputStreamMap.remove(playerNick);
        playerScore.remove(playerNick);
        timerMap.get(playerNick).stop();
        timerMap.remove(playerNick);
    }

    /**
     * Listen to a socket
     * @param playerNick player nick associated to a socket
     */
    public void listen(String playerNick){
        Thread listener = new Thread(new Runnable(){
            public void run(){
                try{
                    int cmd = inputStreamMap.get(playerNick).readInt();
                    System.out.println(cmd);

                    //Message received
                    if(cmd == Demineur.MSG){
                        String inMsg = inputStreamMap.get(playerNick).readUTF();
                        System.out.println("Got msg from " + playerNick + ": " + inMsg);
                        sendMsgToAll(playerNick + ": " + inMsg);
                    }

                    //Position received
                    else if(cmd == Demineur.POS){
                        int x = inputStreamMap.get(playerNick).readInt();
                        int y = inputStreamMap.get(playerNick).readInt();
                        int nb = inputStreamMap.get(playerNick).readInt();
                        System.out.println("Got message from player nb." + nb + ": " + x + "/" + y);
                        positionClicked(x, y, nb, playerNick);
                    }

                    //Player death received
                    else if(cmd == Demineur.DEATH){
                        System.out.println("Sending death");
                        gui.addMsg(playerNick + " is dead. Il est NUL techniquement tactiquement.");
                        sendMsgToAll(playerNick + " is dead. Il est NUL techniquement tactiquement.");
                        deathCount++;
                        System.out.println("Death count: " + deathCount);
                        if(deathCount == playerCount){
                            System.out.println("Sending end");
                            gui.addMsg("Vous êtes NULS tactiquement techniquement");
                            sendMsgToAll("Vous êtes NULS tactiquement techniquement");
                            sendEnd();
                        }
                    }
                    
                    new Thread(this).start();
                }

                catch(IOException e){

                }
            }
        });

        //Start listening thread again
        listener.start();
    }

    /**
     * Send current score to a player
     * @param playerNick
     * @param x
     * @param y
     */
    private void sendScore(String playerNick, int x, int y){
        if(!champ.isMine(x, y)){
            playerScore.put(playerNick, playerScore.get(playerNick) + champ.getCellScore(x, y));
        }   
        try{
            outputStreamMap.get(playerNick).writeInt(Demineur.SCOREUPDATE);
            outputStreamMap.get(playerNick).writeInt(playerScore.get(playerNick));
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Change game difficulty
     * @param newDiffIndex
     */
    protected void changeDiff(int newDiffIndex){
        switch(newDiffIndex){
            case 0:
            serverDifficulty = Level.EASY;
                break;
                
            case 1:
            serverDifficulty = Level.MEDIUM;
                break;

            case 2:
            serverDifficulty = Level.HARD;
                break;

            case 3:
            serverDifficulty = Level.IMPOSSIBLE;
                break;

            case 4:
            serverDifficulty = Level.CUSTOM;
                break;
        }
        startNewGame();
        resetMineNumber();
        sendDiff(newDiffIndex);
    }

    /**
     * Reset score for all players
     */
    private void resetScore(){
        for(Map.Entry<String, Integer> entry : playerScore.entrySet()){
            entry.setValue(0);
        }
    }

    private Map.Entry<String, Integer> getHighestScore(){
       Map.Entry<String, Integer> maxEntry = null;
        for (Map.Entry<String, Integer> entry : playerScore.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }
        return maxEntry;
    }

    private void writeServerHighScore(boolean success){
        String succ;
        
        if(success){
            succ = "CLEARED";
        }
        else{
            succ = "FAILED";
        }

        try{
            Path path = Paths.get(SERVERFILEDATA);
            if(!Files.exists(path)){
                Files.write(path, Arrays.asList(""), StandardCharsets.UTF_8);
            }
            String dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
            String output = dateTime + "\tDifficulty: " + serverDifficulty + "\tUsername: " + getHighestScore().getKey() +"\tScore: " + getHighestScore().getValue() + "\t" + succ + "\n";
            Files.write(path, output.getBytes(), StandardOpenOption.APPEND);
        }

        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Start new game
     */
    private void startNewGame(){
        champ = new Champ(serverDifficulty);
        champ.newGame();
    }

    /**
     * Reset the number of remaining cells to be clicked on
     */
    private void resetMineNumber(){
        remainingSquares = champ.GetDim(serverDifficulty)*champ.GetDim(serverDifficulty) - champ.getInitialMineNumber(serverDifficulty);
    }

    /**
     * Send difficulty change to all players
     * @param newDiffIndex
     */
    private void sendDiff(int newDiffIndex){
        try{
            gui.addMsg("Changing difficulty to " + newDiffIndex + ", number of mines: " + remainingSquares);
            System.out.println("Changing difficulty to " + newDiffIndex);
            for(Map.Entry<String, DataOutputStream> entry : outputStreamMap.entrySet()){
                entry.getValue().writeInt(Demineur.DIFF);
                entry.getValue().writeInt(newDiffIndex);
                entry.getValue().writeInt(remainingSquares);
            }
        }

        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Send end of game flag to all players. Determine good or bad ending.
     */
    private void sendEnd(){
        String endString = "End of the game. \n";
        boolean goodEnding = true;
        if(deathCount == playerCount){
            endString += "Vous êtes NULS techniquement tactiquement \n";
            goodEnding = false;
            
        }
        for(Map.Entry<String, Integer> entry : playerScore.entrySet()){
            String nick = entry.getKey();
            Integer score = entry.getValue();
            endString += nick + ": " + score + "\n";
        }

        try{
            gui.addMsg("Sending end to all");
            System.out.println("Sending end to all");
            for(Map.Entry<String, DataOutputStream> entry : outputStreamMap.entrySet()){
                entry.getValue().writeInt(Demineur.END);
                entry.getValue().writeUTF(endString);
                entry.getValue().writeBoolean(goodEnding);
            }
        }

        catch(IOException e){
            e.printStackTrace();
        }

        writeServerHighScore(goodEnding);
    }

    /**
     * Process a cell clicked command from a client
     * @param x
     * @param y
     * @param playerNb
     * @param playerNick
     */
    private void positionClicked(int x, int y, int playerNb, String playerNick){
        boolean updated = champ.updateClickState(x,y,playerNb);
        if(updated){
            remainingSquares--;
            try{
                gui.addMsg("Sending position to all: " + ": " + x + "/" + y + "(" + playerNb + ")" +"; isMine = " + champ.isMine(x,y));
                System.out.println("Sending position to all: " + ": " + x + "/" + y + "(" + playerNb + ")" +"; isMine = " + champ.isMine(x,y));
                for(Map.Entry<String, DataOutputStream> entry : outputStreamMap.entrySet()){
                    entry.getValue().writeInt(Demineur.POS);
                    entry.getValue().writeInt(x);
                    entry.getValue().writeInt(y);
                    entry.getValue().writeInt(playerNb);
                    entry.getValue().writeUTF(champ.getCloseMines(x, y));
                    entry.getValue().writeBoolean(champ.isMine(x, y));
                    entry.getValue().writeInt(remainingSquares);
                }
            }
    
            catch(IOException e){
                e.printStackTrace();
            }

            sendScore(playerNick, x, y);

            if(remainingSquares == 0){
                sendEnd();
            }
            spreadOnline(x, y, playerNb, playerNick);
        }

    }

    /**
     * Spread the clicked cell if the adjacent number of mines is 0
     * @param x
     * @param y
     * @param playerNb
     * @param playerNick
     */
    private void spreadOnline(int x, int y, int playerNb, String playerNick){
        if(Integer.parseInt(champ.getCloseMines(x, y)) == 0){
            int xsup = x ==  champ.getMinefieldState().length - 1 ? champ.getMinefieldState().length - 1 : x + 1;
            int xinf = x == 0 ? 0 : x - 1;
            int ysup = y == champ.getMinefieldState()[0].length - 1 ? champ.getMinefieldState()[0].length - 1 : y + 1;
            int yinf = y == 0 ? 0 : y - 1;
            
            for(int i = xinf; i <= xsup; i++) {
                for(int j = yinf; j <= ysup; j++) {
                    if( !(i==x && j==y) && (champ.getPlayerClickState()[i][j] == 0)){
                            positionClicked(i, j, playerNb, playerNick);
                    }
                }
            }
        }
    }

    /**
     * Send message to all players
     * @param msg
     */
    private void sendMsgToAll(String msg){
        try{
            gui.addMsg("Sending message to all: " + msg);
            System.out.println("Sending message to all: " + msg);
            for(Map.Entry<String, DataOutputStream> entry : outputStreamMap.entrySet()){
                entry.getValue().writeInt(Demineur.MSG);
                entry.getValue().writeUTF(msg);
            }
        }

        catch(IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Reset the number of death player
     */
    private void resetDeathCount(){
        deathCount = 0;
    }

    /**
     * Send a start of the game flag to all players
     */
    public void sendStart(){
        try{
            gui.addMsg("Sending start to all");
            System.out.println("Sending start to all");
            champ.displayDebug();
            resetMineNumber();
            resetScore();
            resetDeathCount();
            champ.resetClickState();
            for(Map.Entry<String, DataOutputStream> entry : outputStreamMap.entrySet()){
                entry.getValue().writeInt(Demineur.START);
                entry.getValue().writeInt(remainingSquares);
            }
        }

        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Run server listening thread
     */
    public void run(){
        createNewSocket(socketManager);
        new Thread(this).start(); //start waiting for new client
    }

    /**
     * Instantiate a server object
     * @param args
     */
    public static void main(String[] args) {
        new Server();
    }

    
}