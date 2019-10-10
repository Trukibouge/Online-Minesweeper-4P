package emse.ismin;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGUI extends JPanel implements ActionListener{
    
    /**
     *
     */
    private static final long serialVersionUID = -399773621255213729L;

    private Server server;

    private String[] difficultyChoices = {"EASY", "MEDIUM", "HARD", "IMPOSSIBLE", "CUSTOM"};
    private JPanel bottomPanel = new JPanel(new BorderLayout());
    private JButton startButton = new JButton("Start");
    private JComboBox difficultySelect = new JComboBox(difficultyChoices);
    private JTextArea msgArea = new JTextArea(20,20);

    public ServerGUI(Server server){
        this.server = server;
        setLayout(new BorderLayout());
        add(new JLabel("Minesweeper Server"), BorderLayout.NORTH);
        
        msgArea.setEditable(false);
        startButton.addActionListener(this);
        
        bottomPanel.add(startButton, BorderLayout.EAST);
        difficultySelect.setSelectedIndex(1);
        difficultySelect.addActionListener(this);
        bottomPanel.add(difficultySelect, BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);
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
            server.sendStart();
        }

        if(e.getSource() == difficultySelect){
            int selectedIndex = difficultySelect.getSelectedIndex();
            server.changeDiff(selectedIndex);
        }
    }

}