package emse.ismin.socket;

import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import javax.swing.JFrame;

import emse.ismin.Demineur;

public class Server extends JFrame implements Runnable {

    private ServerGUI gui;
    private int playerNb;
    private List<String> playerList;
    private Map<String, DataInputStream> inputStreamMap = new HashMap<String, DataInputStream>();
    private Map<String, DataOutputStream> outputStreamMap = new HashMap<String, DataOutputStream>();
    private ServerSocket socketManager;

    public Server(){
        System.out.println("Starting server");

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

            sendMsgToAll(playerNick + " has joined the game!");
            sendMsgToAll("Current number of players: " + Integer.toString(playerNb));

            listen(playerNick);
        } 
        
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendMsgToAll(String msg){
        gui.addMsg("Sending to all: " + msg);
        try{
            for(Map.Entry<String, DataOutputStream> entry : outputStreamMap.entrySet()){
                entry.getValue().writeInt(0);
                entry.getValue().writeUTF(msg);
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

    public void listen(String playerNick){
        Thread listener = new Thread(new Runnable(){
            public void run(){
                try{
                    int cmd = inputStreamMap.get(playerNick).readInt();
                    System.out.println(cmd);
                    if(cmd == Demineur.MSG){
                        sendMsgToAll(playerNick + ": " + inputStreamMap.get(playerNick).readUTF());
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