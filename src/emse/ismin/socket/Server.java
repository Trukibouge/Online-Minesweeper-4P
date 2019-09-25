package emse.ismin.socket;

import java.net.*;
import java.io.*;
import javax.swing.JFrame;

public class Server extends JFrame implements Runnable {

    private ServerGUI gui;

    public Server(){
        System.out.println("Starting server");

        gui = new ServerGUI();
        setContentPane(gui);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        try {
            ServerSocket socketManager = new ServerSocket(10000);
            Socket socket = socketManager.accept();
            
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());


            




            output.close();
            socket.close();
            socketManager.close();
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