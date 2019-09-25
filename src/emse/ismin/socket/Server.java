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
    private Thread processUsers;

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
            ServerSocket socketManager = new ServerSocket(Demineur.PORT);
            Socket socket = socketManager.accept();
            
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            String playerNick = input.readUTF() ;
            gui.addMsg(playerNick + " connected");
            playerNb++;
            output.writeInt(playerNb);

            // output.close();
            // socket.close();
            // socketManager.close();
        } 
        
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        new Server();
    }

    public void run(){

    }
}