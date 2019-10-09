package emse.ismin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Case extends JPanel implements MouseListener {
	
	/**
     *
     */
    private static final long serialVersionUID = 6544968331371366303L;
    private String caseContent = "?";
	private final static int CASESIZE = 50;
	
	private Demineur app;
	private int x;
	private int y;
	
	private boolean clicked = false;
    private boolean god = false;
    private boolean mine = false;
    private int player = 0;
    private Color cellColor;
    private Color whiteColor = new Color(255,255,255);
	
	public Case(Demineur app, int x, int y) {
		this.app = app;
		this.x = x;
		this.y = y;
		clicked = false;
		
		setPreferredSize(new Dimension(CASESIZE,CASESIZE));
		addMouseListener(this);
	}
	
	public void setText(String text) {
		this.caseContent = text;
	}
	
	public void resetCase() {
		caseContent = "?";
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
                // if(!app.isLost()){
                if(1==1){
                    if(mine) {
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
                        g.setColor(cellColor);
                        g.fillRect(1, 1, getWidth(), getHeight());
                        g.setColor(whiteColor);
                        g.drawString(caseContent,getWidth()/2,getHeight()/2);
					}
                }
                			
			}
		}
        
        //god mode
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
                g.drawString(caseContent,getWidth()/2,getHeight()/2);
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
		
	

    
    public void cellClicked(){
        clicked = true;
        setCellColor();
        repaint();
    }

    public void setCellColor(){
        switch(player){
            case 0: //impossible case
                cellColor = new Color(0,0,0);
                break;
            case 1:
                cellColor = new Color(244, 67, 54);
                break;
            case 2:
                cellColor = new Color(33, 150, 243);
                break;
            case 3:
                cellColor = new Color(76, 175, 80);
                break;
            case 4:
                cellColor = new Color(255, 235, 59);
                break;
        }
        if(player>4){
            Random rdmColor = new Random();
            cellColor = new Color(rdmColor.nextInt(256), rdmColor.nextInt(256), rdmColor.nextInt(256));
        }
    }
	
	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
        if(app.isNetPlay()){
            if(app.isConnected()){
                if(app.isStarted()){
                    if(!app.isLost()){
                        app.sendPos(x, y);
                    }
                }
                else{
                    app.getAppGui().showPopUpMessage("The game has not started!!!! >:(");
                }
            }

            else{
                app.getAppGui().showPopUpMessage("Please connect to a server.");
            }
        }

        else{
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
        
        
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

    public String getCaseContent() {
        return caseContent;
    }

    public void setCaseContent(String caseContent) {
        this.caseContent = caseContent;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public void setPlayer(int player) {
        this.player = player;
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

}
