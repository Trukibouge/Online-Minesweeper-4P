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
public class DemineurGUILegacy extends JPanel implements ActionListener {

	private Demineur app;
	JLabel scoreLabel;
	private JButton quitButton;
	private JButton resetButton;
	private JButton revealButton;
	private JPanel demineurPanel;
	private JButton[][] demineurPanelButtonsLegacy;
	
	private Case[][] demineurPanelButtons;
	
	private JMenuBar menuBar;
	private JMenu menuGame;
	private JMenu menuAbout;
	private JMenuItem menuQuit;
	private JMenuItem menuHelp;
	private JMenuItem menuReset;
	
	public DemineurGUILegacy(Demineur app) {
		//ImageIcon quitIcon = new ImageIcon("sortieCLR.gif");
		this.app = app;
		
		this.setLayout(new BorderLayout());
		
		generateMinefieldDisplayLegacy();
		initializeButtonsLegacy();
		
		scoreLabel = new JLabel("Score: " + app.getScore());

		menuBar = new JMenuBar();
		
		//Game
		menuGame = new JMenu("Game");
		menuBar.add(menuGame);
		
		menuReset = new JMenuItem("Reset", KeyEvent.VK_R);
		menuReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		menuReset.setToolTipText("Start a new game.");
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
		
		
		JPanel ButtonPanel = new JPanel();
		ButtonPanel.add(resetButton);
		ButtonPanel.add(revealButton);
		ButtonPanel.add(quitButton);
		
		add(scoreLabel, BorderLayout.NORTH);
		add(demineurPanel, BorderLayout.CENTER);
		add(ButtonPanel, BorderLayout.SOUTH);

		
	}
	
	private void generateMinefieldDisplayLegacy() {
		int currentDim = app.getChamp().GetDim(app.getLevel());
		demineurPanel = new JPanel();
		demineurPanelButtonsLegacy = new JButton[currentDim][currentDim];
		demineurPanel.setLayout(new GridLayout(currentDim,currentDim));
		
		for(int i = 0; i < demineurPanelButtonsLegacy.length; i++) {
			for(int j = 0; j < demineurPanelButtonsLegacy[0].length; j++) {
				demineurPanelButtonsLegacy[i][j] = new JButton();	
				demineurPanelButtonsLegacy[i][j].addActionListener(this);
				demineurPanel.add(demineurPanelButtonsLegacy[i][j]);
			}
		}
	}
	
	private void initializeButtonsLegacy() {
		for(int i = 0; i < demineurPanelButtonsLegacy.length; i++) {
			for(int j = 0; j < demineurPanelButtonsLegacy[0].length; j++) {
				demineurPanelButtonsLegacy[i][j].setText("?");
			}
		}
	}
	
	private void updateButton(int i, int j) {
		if(!app.getChamp().isMine(i,j)) {
			demineurPanelButtonsLegacy[i][j].setText(app.getChamp().getCloseMines(i,j));
		}

		else {
			demineurPanelButtonsLegacy[i][j].setText("x");
		}

	}
	
	private void updateOnClick(int i, int j) {
		if(!app.getChamp().isMine(i, j)) {
			if(!app.getChamp().getScoreCalculatedPositions()[i][j]) {
				app.setScore(app.getScore() + Integer.parseInt(app.getChamp().getCloseMines(i,j))*10);
				app.getChamp().getScoreCalculatedPositions()[i][j] = true;
			}

		}
		else {
			demineurPanelButtonsLegacy[i][j].setText("☠");
			final ImageIcon deathIcon = new ImageIcon("img/death.png");
			JOptionPane.showMessageDialog(null, "YOU ARE DEAD ☠\n Score: " + String.valueOf(app.getScore()), "Dead", JOptionPane.INFORMATION_MESSAGE, deathIcon);
		}
			
		
		updateScore();
	}
	
	private void updateScore() {
		scoreLabel.setText("Score: " + String.valueOf(app.getScore()));
	}
	
	private void updatePanelGodMode() {
		int currentDim =  app.getChamp().GetDim(app.getLevel());
		for(int i = 0; i < demineurPanelButtonsLegacy.length; i++) {
			for(int j = 0; j < demineurPanelButtonsLegacy[0].length; j++) {
				updateButton(i,j);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==quitButton || e.getSource()==menuQuit) {
			int rep = JOptionPane.showConfirmDialog(null, "Are you sure?", "Qutting App", JOptionPane.YES_NO_OPTION);
			if(rep == JOptionPane.YES_OPTION)
				app.quit();
		}
		
		if(e.getSource()==resetButton || e.getSource()==menuReset) {
			app.reset();
			updateScore();
			initializeButtonsLegacy();
		}
		
		if(e.getSource()==revealButton) {
			updatePanelGodMode();
		}
		
		if (e.getSource() == menuHelp) {
			JOptionPane.showMessageDialog(null, "HELPPPP");
		}
		
		for(int i = 0; i < demineurPanelButtonsLegacy.length; i++){
			for(int j = 0; j < demineurPanelButtonsLegacy[0].length; j++){
				if(e.getSource()== demineurPanelButtonsLegacy[i][j]) {
					updateOnClick(i,j);
					updateButton(i,j);
				}
			}
		}
		
	}
}