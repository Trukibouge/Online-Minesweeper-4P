package emse.ismin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;
/**
 * 
 * @author Truki
 * GUI of the Demineur class
 */
public class DemineurGUI extends JPanel implements ActionListener {

	/**
     *
     */
    private static final long serialVersionUID = -5812139900233662210L;
    private Demineur app;
    JLabel scoreLabel;
    JLabel remainingMinesLabel;
	private JButton quitButton = new JButton("Quit");
	private JButton resetButton = new JButton("Reset");
	private JButton revealButton = new JButton("Cheat");
    
    private JPanel demineurPanel;
	
	private Case[][] demineurPanelCases;
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menuGame = new JMenu("Game");
	private JMenu menuAbout = new JMenu("About");
	
	private JMenuItem menuNewGame = new JMenu("New Game");
	private JMenuItem menuEasy = new JMenuItem("Easy", KeyEvent.VK_E);
	private JMenuItem menuMedium = new JMenuItem("Medium", KeyEvent.VK_M);
	private JMenuItem menuHard = new JMenuItem("Hard", KeyEvent.VK_H);
	private JMenuItem menuImpossible = new JMenuItem("Impossible", KeyEvent.VK_I);
	private JMenuItem menuCustom  = new JMenuItem("Custom", KeyEvent.VK_C);
	
	private JMenuItem menuQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
	private JMenuItem menuHelp= new JMenuItem("Help", KeyEvent.VK_H);
    private JMenuItem menuReset = new JMenuItem("Reset", KeyEvent.VK_R);

    private JTextField hostTextField = new JTextField(Demineur.HOSTNAME, 10);
    private JTextField portTextField = new JTextField(String.valueOf(Demineur.PORT), 5);
    private JTextField nickTextField = new JTextField("Nickname", 10);
    private JButton connectButton = new JButton("Connect");

    private JTextArea msgArea = new JTextArea(5,50);
    private JScrollPane msgScrollPane = new JScrollPane(msgArea);

    private JTextField chatBox = new JTextField(">", 50);
    private JButton sendButton = new JButton("Send");

    private Compteur compteur;

    /**
     * Constructor
     * @param app
     */
	public DemineurGUI(Demineur app) {
		//ImageIcon quitIcon = new ImageIcon("sortieCLR.gif");
		this.app = app;
		
		this.setLayout(new BorderLayout());
        
        initializeMinefieldDisplay();
        initializeGUI();
	
    }

    /**
     * Initialize GUI Component display
     */
    private void initializeGUI(){
        menuBar.add(menuGame);
		
		menuEasy.addActionListener(this);
		menuMedium.addActionListener(this);
		menuHard.addActionListener(this);
		menuImpossible.addActionListener(this);
		menuCustom.addActionListener(this);
		
		menuNewGame.add(menuEasy);
		menuNewGame.add(menuMedium);
		menuNewGame.add(menuHard);
		menuNewGame.add(menuImpossible);
		menuNewGame.add(menuCustom);
		
		menuGame.add(menuNewGame);
		
		menuReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		menuReset.setToolTipText("Reset a game with the current difficulty.");
		menuReset.addActionListener(this);
		menuGame.add(menuReset);
		
		menuQuit.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK)));
		menuQuit.addActionListener(this);
		menuGame.add(menuQuit);
		
		//About
		menuBar.add(Box.createGlue());
		menuBar.add(menuAbout);
		menuAbout.add(menuHelp);
		menuHelp.addActionListener(this);
		
		menuAbout.add(new JMenuItem("Ver 0.0001 Alpha"));
		
		
		app.setJMenuBar(menuBar);
		
		quitButton.addActionListener(this);
		resetButton.addActionListener(this);
		revealButton.addActionListener(this);
		
        JPanel lowerButtonPanel = new JPanel();
        lowerButtonPanel.setLayout(new BorderLayout());
        JPanel lowerButtonPanelUpper = new JPanel();
        JPanel lowerButtonPanelLower = new JPanel();

        lowerButtonPanelUpper.add(resetButton);
		lowerButtonPanelUpper.add(revealButton);
        lowerButtonPanelUpper.add(quitButton);
        lowerButtonPanelLower.setLayout(new BorderLayout());

        //lowerButtonPanelLower.add(msgArea, BorderLayout.NORTH);
        lowerButtonPanelLower.add(msgScrollPane, BorderLayout.NORTH);
        JPanel lowerButtonPanelLowerLower = new JPanel();
        lowerButtonPanelLowerLower.add(chatBox);
        lowerButtonPanelLowerLower.add(sendButton);
        sendButton.addActionListener(this);
        lowerButtonPanelLower.add(lowerButtonPanelLowerLower, BorderLayout.SOUTH);

        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BorderLayout());

        JPanel upperPanelUpper = new JPanel();
        JPanel upperPanelLower = new JPanel();

        scoreLabel = new JLabel("Score: " + app.getScore());
        remainingMinesLabel = new JLabel("Remaining squares: " + app.getRemainingSquares());
        compteur = new Compteur();
        upperPanelUpper.add(scoreLabel);
        upperPanelUpper.add(remainingMinesLabel);
        upperPanelUpper.add(scoreLabel);
        upperPanelUpper.add(compteur);

        connectButton.addActionListener(this);
        upperPanelLower.add(new JLabel("Server: "));
        upperPanelLower.add(hostTextField);
        upperPanelLower.add(portTextField);
        upperPanelLower.add(nickTextField);
        upperPanelLower.add(connectButton);

        upperPanel.add(upperPanelUpper, BorderLayout.NORTH);

        if(app.isNetPlay()){
            lowerButtonPanel.add(lowerButtonPanelLower, BorderLayout.SOUTH);
            upperPanel.add(upperPanelLower, BorderLayout.SOUTH);
        }

        else{
            lowerButtonPanel.add(lowerButtonPanelUpper, BorderLayout.NORTH);
        }

		add(upperPanel, BorderLayout.NORTH);
		add(demineurPanel, BorderLayout.CENTER);
		add(lowerButtonPanel, BorderLayout.SOUTH);

		
    }

    /**
     * Initialize minefield GUI
     */
    private void initializeMinefieldDisplay(){
        demineurPanel = new JPanel();
        generateMinefieldDisplay();
    }

    /**
     * Display a minefield display
     */
	private void generateMinefieldDisplay() {
		int currentDim = app.getChamp().GetDim(app.getLevel());
		demineurPanelCases = new Case[currentDim][currentDim];
		demineurPanel.setLayout(new GridLayout(currentDim,currentDim));
		for(int i = 0; i < currentDim; i++) {
			for(int j = 0; j < currentDim; j++) {
				demineurPanelCases[i][j] = new Case(app, i, j);	
                demineurPanel.add(demineurPanelCases[i][j]);
                
                if(!app.isNetPlay()){
                    demineurPanelCases[i][j].setCaseContent(app.getChamp().getCloseMines(i, j));
                    demineurPanelCases[i][j].setMine(app.getChamp().isMine(i,j));
                }
			}
		}
	}

    /**
     * Reset the display of a minefield
     */
	private void resetMinefieldDisplay() {
		for(int i = 0; i < demineurPanelCases.length; i++) {
			for(int j = 0; j < demineurPanelCases[0].length; j++) {
                demineurPanelCases[i][j].setCaseContent(app.getChamp().getCloseMines(i, j));
                demineurPanelCases[i][j].setMine(app.getChamp().isMine(i,j));
				demineurPanelCases[i][j].resetCase();
			}
		}
	}

    /**
     * Update score and remaining cells label
     */
    protected void updateLabels(){
        updateScoreLabel();
        updateRemainingMinesLabel();
    }

    /**
     * Update score
     */
    protected void updateScoreLabel() {
		scoreLabel.setText("Score: " + app.getScore());
    }

    /**
     * Update remaining cells to click
     */
    protected void updateRemainingMinesLabel(){
        remainingMinesLabel.setText("Remaining squares: " + app.getRemainingSquares());
    }

    /**
     * Display all the minefield's content instantly
     */
	private void updatePanelGodMode() {
		for(int i = 0; i < demineurPanelCases.length; i++) {
			for(int j = 0; j < demineurPanelCases[0].length; j++) {
				demineurPanelCases[i][j].godMode();
			}
		}
	}

    /**
     * Display death message
     */
	protected void onDeath() {
        compteur.stopTimer();
        final ImageIcon deathIcon = new ImageIcon("img/death.png");
        JOptionPane.showMessageDialog(null, "YOU ARE DEAD ☠\n Score: " + String.valueOf(app.getScore()), "Dead", JOptionPane.INFORMATION_MESSAGE, deathIcon);
        app.setLost(true);
        if(!app.isNetPlay()){
            updatePanelGodMode();
            app.WriteLocalHighScore(false);
        }
        //app.WriteScore();
    }

    /**
     * Display a popup message
     * @param msg Message to be displayed
     */
    protected void showPopUpMessage(String msg){
        JOptionPane.showMessageDialog(null, msg);
    }

    /**
     * Show game ending message
     * @param msg
     * @param goodEnding //if the end is a happy end
     */
    protected void onWin(String msg, boolean goodEnding){
        compteur.stopTimer();
        final ImageIcon winIcon;
        if(goodEnding){
            winIcon = new ImageIcon("img/win.jpg");
        }
        else{
            winIcon = new ImageIcon("img/death.png");
        }
        JOptionPane.showMessageDialog(null, msg, "End", JOptionPane.INFORMATION_MESSAGE, winIcon);
        app.setWon(true);
        if(!app.isNetPlay()){
            updatePanelGodMode();
            app.WriteLocalHighScore(goodEnding);
        }
        //app.WriteScore();
    }

    /**
     * Start a new game of a set difficulty: reset panels and labels accordingly
     * @param difficulty
     */
	protected void newGame(Level difficulty) {
        if(!app.isNetPlay()){
            demineurPanel.removeAll();
            app.newDifficulty(difficulty);
            generateMinefieldDisplay();
            updateLabels();
            app.pack();
        }

        else{
            demineurPanel.removeAll();
            generateMinefieldDisplay();
            app.pack();
        }

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == menuEasy) {
            app.setNetPlay(false);
			newGame(Level.EASY);
		}
		
		if(e.getSource() == menuMedium){
            app.setNetPlay(false);
			newGame(Level.MEDIUM);
		}
		
		if(e.getSource() == menuHard){
            app.setNetPlay(false);
			newGame(Level.HARD);
		}
		
		if(e.getSource() == menuImpossible){
            app.setNetPlay(false);
			newGame(Level.IMPOSSIBLE);
		}
		
		if(e.getSource() == menuCustom){
            app.setNetPlay(false);
			newGame(Level.CUSTOM);
		}
		
		if(e.getSource()==quitButton || e.getSource()==menuQuit) {
			int rep = JOptionPane.showConfirmDialog(null, "Are you sure?", "Qutting App", JOptionPane.YES_NO_OPTION);
			if(rep == JOptionPane.YES_OPTION)
				app.quit();
		}
		
		if(e.getSource()==resetButton || e.getSource()==menuReset) {
			guiReset();
		}
		
		if(e.getSource()==revealButton) {
			updatePanelGodMode();
		}
		
		if (e.getSource() == menuHelp) {
			JOptionPane.showMessageDialog(null, "HELPPPP");
        }
        
        if(e.getSource() == connectButton){
            app.connect(hostTextField.getText(), Integer.valueOf(portTextField.getText()), nickTextField.getText());
        }

        if(e.getSource() == sendButton){
            app.sendMsg(chatBox.getText().replace(">", ""));
            chatBox.setText(">");
        }
		
	}

    /**
     * Reset labels and panesls
     */
    private void guiReset(){
        app.reset();
        resetMinefieldDisplay();
        updateScoreLabel();
        updateRemainingMinesLabel();
        compteur.resetTimer();
    }

    public Compteur getCompteur() {
        return compteur;
    }

    /**
     * Add a message to the messaging area
     * @param str Message
     */
    protected void addMsg(String str){
        msgArea.append(str + "\n");
    }

    public Case[][] getDemineurPanelCases() {
        return demineurPanelCases;
    }

}