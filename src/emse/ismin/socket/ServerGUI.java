package emse.ismin.socket;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGUI extends JPanel implements ActionListener{
    
    private Server server;

    private JButton startButton = new JButton("Start");
    private JTextArea msgArea = new JTextArea(20,20);

    public ServerGUI(Server server){
        this.server = server;
        setLayout(new BorderLayout());
        add(new JLabel("Minesweeper Server"), BorderLayout.NORTH);
        
        msgArea.setEditable(false);
        startButton.addActionListener(this);
        add(startButton, BorderLayout.SOUTH);
        add(msgArea, BorderLayout.CENTER);

    }

    /**
     * 
     * @param str
     */
    protected void addMsg(String str){
        msgArea.append(str + "\n");
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == startButton){
            addMsg("HEY MAN");
            
        }
    }

}