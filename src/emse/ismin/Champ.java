/**
 * 
 */

package emse.ismin;

import java.util.*;

/**
 * @author Truki
 * Mine field class
 *
 */
public class Champ {
	
	private boolean[][] minefieldState; //true if a cell is a mine
    private boolean[][] scoreCalculatedPositions; //score value of a cell
    private int[][] playerClickState; //id of the player who clicked on a cell
	
	private int mineNumber; //number of close cells
    
    private Level difficulty; //difficulty level

	//nb of mines for each difficulty
	final private static int NBMINESEASY = 2;
	final private static int NBMINESMEDIUM = 20;
	final private static int NBMINESHARD = 80;
	final private static int NBMINESIMPOSSIBLE = 80;

	//size of the field for each difficulty
	final private static int DIMEASY = 3;
	final private static int DIMMEDIUM = 10;
	final private static int DIMHARD = 20;
	final private static int DIMIMPOSSIBLE = 10;

	/**
	 * Get size depending on difficulty
	 * @param difficulty
	 * @return Dimensions of the field
	 */
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

	/**
	 * Get mine number depending on difficulty
	 * @param difficulty
	 * @return Number of mines
	 */
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

	/**
	 * Constructor
	 * @param difficulty
	 */
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

	/**
	 * Initialize a champ
	 * @param x
	 * @param y
	 */
	private void initChamp(int x, int y) {
		minefieldState = new boolean[x][y];
        scoreCalculatedPositions = new boolean[x][y];
        playerClickState = new int[x][y];
	}

	/**
	 * Initialize a champ
	 * @param dimx
	 * @param dimy
	 */
	public Champ(int dimx, int dimy) {
		initChamp(dimx,dimy);
	}

	/**
	 * Initialize a champ
	 * @param dimx
	 * @param dimy
	 * @param nbMines
	 */
	public Champ(int dimx, int dimy, int nbMines) {
		this(dimx, dimy);
		mineNumber = nbMines;
	}

	/**
	 * Return the mine field state as string
	 * @return A display of the minefield
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

	/**
	 *
	 * @param debug
	 * @return
	 */
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
	 */
	private void placeMines() {
		Random rdmNumber = new Random();
		//Reset
		for(int i = 0; i < minefieldState.length; i++) {
			for(int j = 0; j < minefieldState[0].length; j++) {
					minefieldState[i][j] = false;
                    scoreCalculatedPositions[i][j] = false;
                    playerClickState[i][j] = 0;
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

	/**
	 * Console display of various information about the champ
	 */
	public void displayDebug() {
        System.out.println(this.toString(true));
        System.out.println("Difficulty: " + this.difficulty.toString());
	}

	/**
	 * Get the number of adjacent mines to a cell
	 * @param x
	 * @param y
	 * @return Number of adjacent mines
	 */
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

	/**
	 * Start a new game
	 */
    public void newGame(){
        placeMines();
		display();
		displayDebug();
    }



    public int[][] getPlayerClickState() {
        return playerClickState;
    }

	/**
	 * Update a cell's player click state
	 * @param x
	 * @param y
	 * @param nb
	 * @return Boolean indicating if a click state has been update
	 */
    public boolean updateClickState(int x, int y, int nb){
        boolean updated = false;
        if(playerClickState[x][y] == 0){
            playerClickState[x][y] = nb;
            updated = true;
        }
        return updated;
    }

	/**
	 * Reset all player click states
	 */
	public void resetClickState(){
        for(int i = 0; i < playerClickState.length; i++){
            for(int j = 0; j < playerClickState[0].length; j++){
                playerClickState[i][j] = 0;
            }
        }
    }

	/**
	 * Get the score corrresponding to a cell
	 * @param x
	 * @param y
	 * @return Score value of a cell
	 */
    public int getCellScore(int x, int y){
        return Integer.parseInt(getCloseMines(x,y))*10;
    }

}

