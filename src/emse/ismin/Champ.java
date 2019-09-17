/**
 * 
 */

package emse.ismin;
import java.util.*;

import javax.swing.Spring;

/**
 * @author Truki
 * Mine field class
 *
 */
public class Champ {
	
	private boolean[][] minefieldState;
	private boolean[][] scoreCalculatedPositions;
	
	private int mineNumber;
    
    private Level difficulty;
    
	final private static int NBMINESEASY = 2;
	final private static int NBMINESMEDIUM = 20;
	final private static int NBMINESHARD = 80;
	final private static int NBMINESIMPOSSIBLE = 80;
	
	final private static int DIMEASY = 3;
	final private static int DIMMEDIUM = 10;
	final private static int DIMHARD = 20;
	final private static int DIMIMPOSSIBLE = 10;
	
	public int GetDim(Level difficulty) {
		int dim = 0;
		switch(difficulty) {
		case EASY:
			dim = DIMEASY;
			break;
			
		case MEDIUM:
			dim = DIMMEDIUM;
			break;
		
		case HARD:
			dim = DIMHARD;
			break;
			
		case IMPOSSIBLE:
			dim = DIMIMPOSSIBLE;
			break;
			
		case CUSTOM:
			dim = 0;
			break;
		}
		
		return dim;
		
    }
    
    public int getInitialMineNumber(Level difficulty){
		int mine = 0;
		switch(difficulty) {
		case EASY:
            mine = NBMINESEASY;
            break;
			
		case MEDIUM:
            mine = NBMINESMEDIUM;
			break;
		
		case HARD:
            mine = NBMINESHARD;
			break;
			
		case IMPOSSIBLE:
            mine = NBMINESIMPOSSIBLE;
			break;
			
		case CUSTOM:
			mine = 0;
			break;
		}
		
		return mine;
    }
	
	public Champ(Level difficulty) {
        this.difficulty = difficulty;
		switch(difficulty) {
		case EASY:
			initChamp(DIMEASY,DIMEASY);
			mineNumber = NBMINESEASY;
			break;
			
		case MEDIUM:
			initChamp(DIMMEDIUM,DIMMEDIUM);
			mineNumber = NBMINESMEDIUM;
			break;
			
		case HARD: 
			initChamp(DIMHARD,DIMHARD);
			mineNumber = NBMINESHARD;
			break;
			
		case IMPOSSIBLE:
			initChamp(DIMIMPOSSIBLE,DIMIMPOSSIBLE);
			mineNumber = NBMINESIMPOSSIBLE;
			break;
			
		case CUSTOM:
			break;
		}
	}
	
	private void initChamp(int x, int y) {
		minefieldState = new boolean[x][y]; 
		scoreCalculatedPositions = new boolean[x][y];
	}
	
	public Champ(int dimx, int dimy) {
		initChamp(dimx,dimy);
	}
	
	public Champ(int dimx, int dimy, int nbMines) {
		this(dimx, dimy);
		mineNumber = nbMines;
	}
	
	/**
	 * Return the mine field state as string
	 */
	public String toString() {
		StringBuilder fieldDisplay = new StringBuilder();
		for(int i = 0; i < minefieldState.length; i++) {
			for(int j = 0; j < minefieldState[0].length; j++) {
				if(minefieldState[i][j] == false) {
					fieldDisplay.append(getCloseMines(i,j) + " ");
				}
				else {
					fieldDisplay.append("x ");
				}
			}
			fieldDisplay.append("\n");
		}
		return(fieldDisplay.toString());
	}
	
	public String toString(boolean debug) {
		if(debug == false) {
			return(this.toString());
		}
		
		else {
			StringBuilder fieldDisplay = new StringBuilder();
			for(int i = 0; i < minefieldState.length; i++) {
				for(int j = 0; j < minefieldState[0].length; j++) {
					if(minefieldState[i][j] == false) {
						fieldDisplay.append(". ");
					}
					else {
						fieldDisplay.append("x ");
					}
				}
				fieldDisplay.append("\n");
			}
			return(fieldDisplay.toString());
		}

	}
	
	/**
	 * Randomly place mines in the whole field.
	 * 
	 */
	public void placeMines() {
		Random rdmNumber = new Random();
		//Reset
		for(int i = 0; i < minefieldState.length; i++) {
			for(int j = 0; j < minefieldState[0].length; j++) {
					minefieldState[i][j] = false;
					scoreCalculatedPositions[i][j] = false;
			}
		}
		
		//Set
		for (int remainingMines = mineNumber; remainingMines > 0;) {
			int x = rdmNumber.nextInt(minefieldState.length);
			int y = rdmNumber.nextInt(minefieldState[0].length);
			
			if(!minefieldState[x][y]) {
				minefieldState[x][y] = true;
				remainingMines--;
			}
		}
	}
	
	/**
	 * Display mine field as text.
	 */
	public void display() {		
		System.out.println(this.toString());
	}
	
	public void displayDebug() {
        System.out.println(this.toString(true));
        System.out.println("Difficulty: " + this.difficulty.toString());
	}
	
	public String getCloseMines(int x, int y) {
		int xsup = x == minefieldState.length - 1 ? minefieldState.length - 1 : x + 1;
		int xinf = x == 0 ? 0 : x - 1;
		int ysup = y == minefieldState[0].length - 1 ? minefieldState[0].length - 1 : y + 1;
		int yinf = y == 0 ? 0 : y - 1;
		
		int closeMinesCount = 0;
		
		for(int i = xinf; i <= xsup; i++) {
			for(int j = yinf; j <= ysup; j++) {
				if( !(i==x && j==y) && minefieldState[i][j]) {
					closeMinesCount++;
				}
			}
		}
		
		return String.valueOf(closeMinesCount);
	}
	
	public boolean[][] getMinefieldState() {
		return(minefieldState);
	}
	
	public boolean[][] getScoreCalculatedPositions() {
		return scoreCalculatedPositions;
	}

	public boolean isMine(int x, int y) {
		return(minefieldState[x][y]);
	}

    public void newGame(){
        placeMines();
		display();
		displayDebug();
    }
}

