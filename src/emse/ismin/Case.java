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
	private final static int CASESIZE = 50;
	
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
		setPreferredSize(new Dimension(CASESIZE,CASESIZE));
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
                if(!app.isLost()){
                    if(app.getChamp().isMine(x,y)) {
						try {
							BufferedImage image;
							image = ImageIO.read(new File("img/death.png"));
							g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
						}
						catch(IOException ex){
							ex.printStackTrace();
						}	
					}
						
					else {
						g.setColor(new Color (0,0,0,100));
						g.drawString(text,getWidth()/2,getHeight()/2);
						g.fillRect(1, 1, getWidth(), getHeight());
					}
                }
                			
			}
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
            }
                
            else {
                g.setColor(new Color (0,0,0,100));
                g.drawString(text,getWidth()/2,getHeight()/2);
                g.fillRect(1, 1, getWidth(), getHeight());
            }
		}
	}
    
    // private void spread(){
    //     int xsup = x ==  minefieldState.length - 1 ? minefieldState.length - 1 : x + 1;
	// 	int xinf = x == 0 ? 0 : x - 1;
	// 	int ysup = y == minefieldState[0].length - 1 ? minefieldState[0].length - 1 : y + 1;
	// 	int yinf = y == 0 ? 0 : y - 1;
		
	// 	int closeMinesCount = 0;
		
	// 	for(int i = xinf; i <= xsup; i++) {
	// 		for(int j = yinf; j <= ysup; j++) {
	// 			if( !(i==x && j==y) && minefieldState[i][j]) {
	// 				closeMinesCount++;
	// 			}
	// 		}
	// 	}
    // }
    
	public void godMode() {
		god = true;
		repaint();
	}
		
	
	private void updateLabels(int x, int y) {
		if(!app.getChamp().getScoreCalculatedPositions()[x][y]) {
			app.getChamp().getScoreCalculatedPositions()[x][y] = true;
            app.setScore(app.getScore() + Integer.parseInt(app.getChamp().getCloseMines(x,y))*10);
            app.setRemainingSquares(app.getRemainingSquares() - 1);
            app.getAppGui().updateScoreLabel();
            app.getAppGui().updateRemainingMinesLabel();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
        clicked = true;
        repaint();

        if(!app.isStarted()){
             app.setStarted(true);
             app.getAppGui().getCompteur().startTimer();
        }

        if(!app.isLost() || !app.isWon()){
            if(app.getChamp().isMine(x,y)){
                app.getAppGui().onDeath();
            }
            else{
                updateLabels(x,y);
                if(app.getRemainingSquares() == 0){
                    app.getAppGui().onWin();
                }
            }
        }

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
