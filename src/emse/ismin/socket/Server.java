package emse.ismin.socket;

import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import javax.swing.JFrame;

import emse.ismin.Demineur;
import emse.ismin.Level;
import emse.ismin.Champ;

//Collection 1
//Bonus 5
//Deconnexion (bonus)
//IHM (bonus)
//Score (bonus)
//Chat (bonus)
//Couleurs 2
//Solo 4
//Niveau Solo 2
//Fichier 1
//Réseau: nombre de clients infini 1, position 2, fin de partie 1, affichage couleur 2   
//Commentaires 1 Javadoc 1 

public class Server extends JFrame implements Runnable {

    /**
     *
     */
    private static final long serialVersionUID = -5822900338130207614L;
    private ServerGUI gui;
    private int playerCount;
    private Map<String, Integer> playerScore = new HashMap<String, Integer>();
    private Map<String, DataInputStream> inputStreamMap = new HashMap<String, DataInputStream>();
    private Map<String, DataOutputStream> outputStreamMap = new HashMap<String, DataOutputStream>();
    private ServerSocket socketManager;
    private Level serverDifficulty = Level.MEDIUM;
    private Champ champ = new Champ(serverDifficulty);
    private int remainingSquares = champ.GetDim(serverDifficulty)*champ.GetDim(serverDifficulty) - champ.getInitialMineNumber(serverDifficulty);

    private int deathCount = 0;

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

    private void createNewSocket(ServerSocket socketManager){
        try {
            Socket socket = socketManager.accept(); //new client
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            String playerNick;
            playerNick = input.readUTF() ;
            gui.addMsg(playerNick + " connected");
            playerCount++;


            inputStreamMap.put(playerNick, input);
            outputStreamMap.put(playerNick, output);
            playerScore.put(playerNick, 0);

            sendMsgToAll(playerNick + " has joined the game!");
            sendMsgToAll("Current number of players: " + Integer.toString(playerCount));

            gui.addMsg("Attributing player nb to: " + playerNick + " = " + playerCount);
            output.writeInt(Demineur.PLAYERNB);
            output.writeInt(playerCount);

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



    public void listen(String playerNick){
        Thread listener = new Thread(new Runnable(){
            public void run(){
                try{
                    int cmd = inputStreamMap.get(playerNick).readInt();
                    System.out.println(cmd);
                    if(cmd == Demineur.MSG){
                        String inMsg = inputStreamMap.get(playerNick).readUTF();
                        System.out.println("Got msg from " + playerNick + ": " + inMsg);
                        sendMsgToAll(playerNick + ": " + inMsg);
                    }

                    else if(cmd == Demineur.POS){
                        int x = inputStreamMap.get(playerNick).readInt();
                        int y = inputStreamMap.get(playerNick).readInt();
                        int nb = inputStreamMap.get(playerNick).readInt();
                        System.out.println("Got message from player nb." + nb + ": " + x + "/" + y);
                        boolean updated = champ.updateClickState(x,y,nb);
                        if(updated){
                            remainingSquares--;
                            sendPosition(x, y, nb);
                            sendScore(playerNick, x, y);
                            if(remainingSquares == 0){
                                sendEnd();
                            }
                        }
                    }

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
        listener.start();
    }

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

    private void resetScore(){
        for(Map.Entry<String, Integer> entry : playerScore.entrySet()){
            entry.setValue(0);
        }
    }

    private void startNewGame(){
        champ = new Champ(serverDifficulty);
        champ.newGame();
    }

    private void resetMineNumber(){
        remainingSquares = champ.GetDim(serverDifficulty)*champ.GetDim(serverDifficulty) - champ.getInitialMineNumber(serverDifficulty);
    }

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

    private void sendEnd(){
        String endString = "";
        if(deathCount == playerCount){
            endString += "Vous êtes NULS techniquement tactiquement \n";
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
            }
        }

        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void sendPosition(int x, int y, int playerNb){
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
    }

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

    public void sendStart(){
        try{
            gui.addMsg("Sending start to all");
            System.out.println("Sending start to all");
            resetMineNumber();
            resetScore();
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

    public void run(){
        createNewSocket(socketManager);
        new Thread(this).start(); //start waiting for new client
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        new Server();
    }

    
}