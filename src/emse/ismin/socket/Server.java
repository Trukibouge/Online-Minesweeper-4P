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

//1 go
//2 clic

public class Server extends JFrame implements Runnable {

    /**
     *
     */
    private static final long serialVersionUID = -5822900338130207614L;
    private ServerGUI gui;
    private int playerNb;
    private List<String> playerList;
    private Map<String, DataInputStream> inputStreamMap = new HashMap<String, DataInputStream>();
    private Map<String, DataOutputStream> outputStreamMap = new HashMap<String, DataOutputStream>();
    private ServerSocket socketManager;
    private Level serverDifficulty = Level.MEDIUM;
    private Champ champ = new Champ(serverDifficulty);

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
            playerNb++;


            inputStreamMap.put(playerNick, input);
            outputStreamMap.put(playerNick, output);

            sendMsgToAll(Demineur.MSG, playerNick + " has joined the game!");
            sendMsgToAll(Demineur.MSG, "Current number of players: " + Integer.toString(playerNb));

            gui.addMsg("Attributing player nb to: " + playerNick + " = " + playerNb);
            output.writeInt(Demineur.PLAYERNB);
            output.writeInt(playerNb);

            listen(playerNick);
        } 
        
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendMsgToAll(int nature, String msg){
        try{
            if(nature == Demineur.MSG){
                gui.addMsg("Sending message to all: " + msg);
                System.out.println("Sending message to all: " + msg);
                for(Map.Entry<String, DataOutputStream> entry : outputStreamMap.entrySet()){
                    entry.getValue().writeInt(Demineur.MSG);
                    entry.getValue().writeUTF(msg);
                }
            }

            else if(nature == Demineur.START){
                gui.addMsg("Sending start to all");
                System.out.println("Sending start to all");
                for(Map.Entry<String, DataOutputStream> entry : outputStreamMap.entrySet()){
                    entry.getValue().writeInt(Demineur.START);
                }
            }

        }

        catch(IOException e){
            e.printStackTrace();
        }

    }

    public void sendStart(){
        sendMsgToAll(Demineur.START, "");
    }

    public void run(){
        createNewSocket(socketManager);
        new Thread(this).start(); //start waiting for new client
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
                        sendMsgToAll(Demineur.MSG, playerNick + ": " + inMsg);
                    }
                    else if(cmd == Demineur.POS){
                        int x = inputStreamMap.get(playerNick).readInt();
                        int y = inputStreamMap.get(playerNick).readInt();
                        int nb = inputStreamMap.get(playerNick).readInt();
                        System.out.println("Got message from player nb." + nb + ": " + x + "/" + y);
                    }
                    
                    new Thread(this).start();
                }

                catch(IOException e){

                }
            }
        });
        listener.start();
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        new Server();
    }

    
}