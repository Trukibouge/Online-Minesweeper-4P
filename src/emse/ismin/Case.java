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

/**
 * @author Truki
 * Case class. Used to display cells on the minefield.
 */
public class Case extends JPanel implements MouseListener {

    private static final long serialVersionUID = 6544968331371366303L;
    private String caseContent = "?";
	private final static int CASESIZE = 50;
	
	private Demineur app;
	private int x;
	private int y;
	
	private boolean clicked = false;
    private boolean god = false; //god mode (instantly display a cell)
    private boolean mine = false; //true if the cell contains a mine
    private int player = 0; //id of the player who clicked a cell
    private Color cellColor;
    private Color whiteColor = new Color(255,255,255);

    /**
     * Constructor
     * @param app
     * @param x
     * @param y
     */
	public Case(Demineur app, int x, int y) {
		this.app = app;
		this.x = x;
		this.y = y;
		clicked = false;
		
		setPreferredSize(new Dimension(CASESIZE,CASESIZE));
		addMouseListener(this);
	}

    /**
     * Set the content of a cell
     * @param text
     */
	public void setText(String text) {
		this.caseContent = text;
	}

    /**
     * Reset a cell state
     */
	public void resetCase() {
		//caseContent = "?";
		clicked = false;
		god = false;
		repaint();
	}

    /**
     * paintComponent override
     * @param g
     */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);		

		if(!god) {
            //cell not clicked
			if(!clicked) {
                paintMaterialGrey(g);
            }
            
            //cell clicked
			else {
                paintCell(g);
			}
        }
        
        //god mode (reveal)
		else {
            paintCell(g);
		}
    }

    /**
     * Paint a cell in grey
     * @param g
     */
    private void paintMaterialGrey(Graphics g){
        if((x+y)%2 == 1){
            g.setColor(new Color(178, 178, 178));
        }
        else{
            g.setColor(new Color(158, 158, 158));
        }




        g.fillRect(1, 1, getWidth(), getHeight());
    }

    /**
     * Paint a cell
     * @param g
     */
    private void paintCell(Graphics g){
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


    /**
     * Spread clicked cells in offline mode
     */
    private void spreadOffline(){
        if(Integer.parseInt(app.getChamp().getCloseMines(x, y)) == 0){

            int xsup = x ==  app.getChamp().getMinefieldState().length - 1 ? app.getChamp().getMinefieldState().length - 1 : x + 1;
            int xinf = x == 0 ? 0 : x - 1;
            int ysup = y == app.getChamp().getMinefieldState()[0].length - 1 ? app.getChamp().getMinefieldState()[0].length - 1 : y + 1;
            int yinf = y == 0 ? 0 : y - 1;
            
            for(int i = xinf; i <= xsup; i++) {
                for(int j = yinf; j <= ysup; j++) {
                    if( !(i==x && j==y) && (!app.getAppGui().getDemineurPanelCases()[i][j].clicked) ){
                            app.getAppGui().getDemineurPanelCases()[i][j].mouseClickedOffline();
                    }
                }
            }
        }
    }

    /**
     * Set god mode for a cell
     */
	public void godMode() {
		god = true;
		repaint();
	}

    /**
     * Process a cell position in netplay
     */
    protected void cellPositionReceivedFromServer(){
        clicked = true;
        setCellColor();
        repaint();
    }

    /**
     * Set the cell color for netplay
     */
    public void setCellColor(){
        switch(player){
            case 0: //solo case
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
            mouseClickedOnline();
        }

        else{
            mouseClickedOffline();
            
        }
    }

    /**
     * Process the click on a cell for netplay
     */
    private void mouseClickedOnline(){
        if(app.isConnected()){
            if(app.isStarted()){
                if(!app.isLost() && !app.isWon()){
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

    /**
     * Process the click on a cell in offline mode
     */
    private void mouseClickedOffline(){
        if(!app.isLost()){
            clicked = true;
                if(!app.isStarted()){
                    app.startGameAndTimer();
                }
    
                if(!app.isWon()){
                    if(app.getChamp().isMine(x,y)){
                        app.getAppGui().onDeath();
                    }
                    else{
                        processClickedCell(x,y);
                        if(app.getRemainingSquares() == 0){
                            String endString = "YOU WIN\nScore: " + String.valueOf(app.getScore()) + "\nTime: " + String.valueOf(app.getAppGui().getCompteur().getTime());
                            app.getAppGui().onWin(endString, true);
                        }
                    }
                }
                repaint();
                spreadOffline();
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

    /**
     * Get the string content of a cell
     * @return Content
     */
    public String getCaseContent() {
        return caseContent;
    }

    /**
     * Set the string content of a cell
     * @param caseContent Content
     */
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

    /**
     * Process score and labels for a clicked cell (offline mode only)
     * @param x
     * @param y
     */
    private void processClickedCell(int x, int y) {
		if(!app.getChamp().getScoreCalculatedPositions()[x][y]) {
			app.getChamp().getScoreCalculatedPositions()[x][y] = true;
            app.setScore(app.getScore() + Integer.parseInt(app.getChamp().getCloseMines(x,y))*10);
            app.setRemainingSquares(app.getRemainingSquares() - 1);
            app.getAppGui().updateLabels();
		}
    }

}
