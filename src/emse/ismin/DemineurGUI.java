package emse.ismin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;

/**
 * 
 * @author Truki
 *
 */
public class DemineurGUI extends JPanel implements ActionListener {

	private Demineur app;
	JLabel scoreLabel;
	private JButton quitButton;
	private JButton resetButton;
	private JButton revealButton;
	private JPanel demineurPanel;
	
	private Case[][] demineurPanelCases;
	
	private JMenuBar menuBar;
	private JMenu menuGame;
	private JMenu menuAbout;
	
	private JMenuItem menuNewGame;
	private JMenuItem menuEasy;
	private JMenuItem menuMedium;
	private JMenuItem menuHard;
	private JMenuItem menuImpossible;
	private JMenuItem menuCustom;
	
	private JMenuItem menuQuit;
	private JMenuItem menuHelp;
	private JMenuItem menuReset;
	
	public DemineurGUI(Demineur app) {
		//ImageIcon quitIcon = new ImageIcon("sortieCLR.gif");
		this.app = app;
		
		this.setLayout(new BorderLayout());
		
		generateMinefieldDisplay();

		scoreLabel = new JLabel("Score: " + app.getScore());
		
		menuBar = new JMenuBar();
		//Game
		menuGame = new JMenu("Game");
		menuBar.add(menuGame);
		
		menuNewGame = new JMenu("New Game");
		menuEasy = new JMenuItem("Easy", KeyEvent.VK_E);
		menuEasy.addActionListener(this);
		menuMedium = new JMenuItem("Medium", KeyEvent.VK_M);
		menuMedium.addActionListener(this);
		menuHard = new JMenuItem("Hard", KeyEvent.VK_H);
		menuHard.addActionListener(this);
		menuImpossible = new JMenuItem("Impossible", KeyEvent.VK_I);
		menuImpossible.addActionListener(this);
		menuCustom = new JMenuItem("Custom", KeyEvent.VK_C);
		menuCustom.addActionListener(this);
		
		menuNewGame.add(menuEasy);
		menuNewGame.add(menuMedium);
		menuNewGame.add(menuHard);
		menuNewGame.add(menuImpossible);
		menuNewGame.add(menuCustom);
		
		menuGame.add(menuNewGame);
		
		
		menuReset = new JMenuItem("Reset", KeyEvent.VK_R);
		menuReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		menuReset.setToolTipText("Reset a game with the current difficulty.");
		menuReset.addActionListener(this);
		menuGame.add(menuReset);
		
		menuQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
		menuQuit.setAccelerator((KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK)));
		menuQuit.addActionListener(this);
		menuGame.add(menuQuit);
		
		//About
		menuAbout = new JMenu("About");
		menuBar.add(Box.createGlue());
		menuBar.add(menuAbout);
		menuHelp = new JMenuItem("Help", KeyEvent.VK_H);
		menuAbout.add(menuHelp);
		menuHelp.addActionListener(this);
		
		menuAbout.add(new JMenuItem("Ver 0.0001 Alpha"));
		
		
		app.setJMenuBar(menuBar);
		
		quitButton = new JButton("Quit");
		quitButton.addActionListener(this);
		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		
		revealButton = new JButton("Cheat");
		revealButton.addActionListener(this);
		
		JPanel lowerButtonPanel = new JPanel();
		lowerButtonPanel.add(resetButton);
		lowerButtonPanel.add(revealButton);
		lowerButtonPanel.add(quitButton);
		
		add(scoreLabel, BorderLayout.NORTH);
		add(demineurPanel, BorderLayout.CENTER);
		add(lowerButtonPanel, BorderLayout.SOUTH);

		
	}
	
	private void generateMinefieldDisplay() {
		int currentDim = app.getChamp().GetDim(app.getLevel());
		demineurPanel = new JPanel();
		demineurPanelCases = new Case[currentDim][currentDim];
		demineurPanel.setLayout(new GridLayout(currentDim,currentDim));
		
		for(int i = 0; i < currentDim; i++) {
			for(int j = 0; j < currentDim; j++) {
				demineurPanelCases[i][j] = new Case(app, i, j);	
				demineurPanel.add(demineurPanelCases[i][j]);
			}
		}
	}
	
	private void resetMinefieldDisplay() {
		for(int i = 0; i < demineurPanelCases.length; i++) {
			for(int j = 0; j < demineurPanelCases[0].length; j++) {
				demineurPanelCases[i][j].resetCase();
			}
		}
	}
	
	protected void updateScoreLabel() {
		scoreLabel.setText("Score: " + String.valueOf(app.getScore()));
	}
	
	private void updatePanelGodMode() {
		for(int i = 0; i < demineurPanelCases.length; i++) {
			for(int j = 0; j < demineurPanelCases[0].length; j++) {
				demineurPanelCases[i][j].godMode();
			}
		}

	}
	
	protected void onDeath() {
		final ImageIcon deathIcon = new ImageIcon("img/death.png");
		JOptionPane.showMessageDialog(null, "YOU ARE DEAD ☠\n Score: " + String.valueOf(app.getScore()), "Dead", JOptionPane.INFORMATION_MESSAGE, deathIcon);
		updatePanelGodMode();
	}
	
	private void newGame(Level difficulty) {
		demineurPanel.removeAll();
		app.newGame(difficulty);
		generateMinefieldDisplay();
		app.pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == menuEasy) {
			newGame(Level.EASY);
		}
		
		if(e.getSource() == menuMedium){
			newGame(Level.MEDIUM);
		}
		
		if(e.getSource() == menuHard){
			newGame(Level.HARD);
		}
		
		if(e.getSource() == menuImpossible){
			newGame(Level.IMPOSSIBLE);
		}
		
		if(e.getSource() == menuCustom){
			newGame(Level.CUSTOM);
		}
		
		if(e.getSource()==quitButton || e.getSource()==menuQuit) {
			int rep = JOptionPane.showConfirmDialog(null, "Are you sure?", "Qutting App", JOptionPane.YES_NO_OPTION);
			if(rep == JOptionPane.YES_OPTION)
				app.quit();
		}
		
		if(e.getSource()==resetButton || e.getSource()==menuReset) {
			app.reset();
			resetMinefieldDisplay();
			updateScoreLabel();
		}
		
		if(e.getSource()==revealButton) {
			updatePanelGodMode();
		}
		
		if (e.getSource() == menuHelp) {
			JOptionPane.showMessageDialog(null, "HELPPPP");
		}
		
	}
}