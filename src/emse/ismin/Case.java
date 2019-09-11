package emse.ismin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Case extends JPanel implements MouseListener {
	
	private String text = "?";
	private final static int caseSize = 50;
	
	private Demineur app;
	private int x;
	private int y;
	
	private boolean clicked = false;
	private boolean god = false;
	
	public Case(Demineur app, int x, int y) {
		this.app = app;
		this.x = x;
		this.y = y;
		clicked = false;
		
		initializeText();
		setPreferredSize(new Dimension(caseSize,caseSize));
		addMouseListener(this);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	private void initializeText() {
		if(!app.getChamp().isMine(x,y)) {
			text = app.getChamp().getCloseMines(x,y);
		}

		else {
			text = "x";
		}
	}
	
	public void resetCase() {
		initializeText();
		clicked = false;
		god = false;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);		
	
		if(!god) {
			if(!clicked) {
				g.setColor(new Color(158, 158, 158));
				g.fillRect(1, 1, getWidth(), getHeight());
			}
			
			else {
					if(app.getChamp().isMine(x,y)) {
						try {
							BufferedImage image;
							image = ImageIO.read(new File("img/death.png"));
							g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
						}
						catch(IOException ex){
							ex.printStackTrace();
						}	
						app.getAppGui().onDeath();
					}
						
					else {
						g.setColor(new Color (0,0,0,100));
						g.drawString(text,25,25);
						g.fillRect(1, 1, getWidth(), getHeight());
						updateScore(x,y);
					}
						
			}
		}
		
		else {
			g.setColor(new Color (0,0,0,100));
			g.drawString(text,25,25);
			g.fillRect(1, 1, getWidth(), getHeight());
		}
	}
	
	public void godMode() {
		god = true;
		repaint();
	}
		
	
	private void updateScore(int x, int y) {
		if(!app.getChamp().getScoreCalculatedPositions()[x][y]) {
			app.getChamp().getScoreCalculatedPositions()[x][y] = true;
			app.setScore(app.getScore() + Integer.parseInt(app.getChamp().getCloseMines(x,y))*10);
			app.getAppGui().updateScoreLabel();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		clicked = true;
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	


}
