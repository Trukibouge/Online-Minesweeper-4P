package emse.ismin.socket;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class ServerGUI extends JPanel{

    private JButton startButton = new JButton("Start");

    public ServerGUI(){
        setLayout(new BorderLayout());
        add(new JLabel("Minesweeper Server"), BorderLayout.NORTH);
        add(startButton);
    }

}