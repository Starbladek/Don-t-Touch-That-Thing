package GameState;

import java.awt.image.BufferedImage;

import Audio.JukeBox;
import Handlers.Content;

public class GameStateManager {
	
	private GameState[] gameStates;
	public static int currentState;
	private int nextState;
	
	//List of the states the game can be in
	public static final int NUMOFGAMESTATES = 8;
	public static final int MENUSTATE = 0;
	public static final int LEVEL1STATE = 1;
	public static final int LEVEL2STATE = 2;
	public static final int LEVEL3STATE = 3;
	public static final int LEVEL4STATE = 4;
	public static final int LEVEL5STATE = 5;
	public static final int LEVEL6STATE = 6;
	public static final int RESULTSTATE = 7;
	
	//Transition things
	private BufferedImage[][] loadingImages = new BufferedImage[32][18];
	
	private boolean lockTransition;
	
	private final int WIPE_SPEED = 22;
	public static boolean currentlyWiping;
	public static boolean currentlyWipeReturning;
	
	private int smallPause;
	
	//Clock stuff
	private long startGameTime;
	private long currentGameTime;
	private long elapsedGameTime;
	public static int mainTimeLimit = 360;
	public static int timeLeft;
	
	public static int player1Score;
	public static int player2Score;
	
	public static int thingPhase;
	
	public GameStateManager() {
		
		JukeBox.init();
		
		gameStates = new GameState[NUMOFGAMESTATES];
		
		loadState(currentState);
		
	}
	
	private void loadState(int state) {
		
		if(state == MENUSTATE) {
			gameStates[state] = new MenuState(this);
			currentState = state;
			startGameTime = System.nanoTime();
			if (JukeBox.getRunning("GameMusic") == true)
				JukeBox.stop("GameMusic");
		}
		
		else if(state == LEVEL1STATE) {
			player1Score = 0;
			player2Score = 0;
			gameStates[state] = new Level1State(this);
			currentState = state;
			startGameTime = System.nanoTime();
			timeLeft = mainTimeLimit;
			thingPhase = 1;
			if (JukeBox.getRunning("MenuMusic") == true)
				JukeBox.stop("MenuMusic");
			JukeBox.loop("GameMusic");
		}
		
		else if (state == LEVEL2STATE) {
			gameStates[state] = new Level2State(this);
			currentState = state;
		}
		
		else if (state == LEVEL3STATE) {
			gameStates[state] = new Level3State(this);
			currentState = state;
		}
		
		else if (state == LEVEL4STATE) {
			gameStates[state] = new Level4State(this);
			currentState = state;
		}
		
		else if (state == LEVEL5STATE) {
			gameStates[state] = new Level5State(this);
			currentState = state;
		}
		
		else if (state == LEVEL6STATE) {
			if (JukeBox.getRunning("GameMusic") == true)
				JukeBox.stop("GameMusic");
			JukeBox.play("HURRYUP");
			gameStates[state] = new Level6State(this);
			currentState = state;
		}
		
		else if (state == RESULTSTATE) {
			if (JukeBox.getRunning("HURRYUP") == true)
				JukeBox.stop("HURRYUP");
			gameStates[state] = new ResultState(this);
			currentState = state;
		}
		
	}
	
	private void unloadState(int state) {
		gameStates[currentState] = null;
	}
	
	
	
	public void basicTransitionState(int state) {
		
		if (lockTransition == false) {
			lockTransition = true;
			unloadState(currentState);
			loadState(currentState);
			lockTransition = false;
		}
		
	}
	
	public void wipeTransitionState(int state) {
		
		if (lockTransition == false) {
			lockTransition = true;
			nextState = state;
			currentlyWiping = true;
		}
		
	}
	
	
	
	public void update() {
		
		//run the update() method in the current gamestate
		gameStates[currentState].update();
		
		//update the time
		currentGameTime = System.nanoTime();
		elapsedGameTime = (currentGameTime - startGameTime) / 1000000000;	//Divide by that large number to get it in seconds
		timeLeft = (int) (mainTimeLimit - elapsedGameTime);
		
		//wipe transition part 1
		if (currentlyWiping) {
			
			//Insert 20 boxes at a time
			for (int i = 0; i < WIPE_SPEED; i++) {
				
				int row = 0;
				int col = 0;
				
				//This section will find the first empty index of the double array and put a box in it. Once it reaches the end of the
				//array, it will exit and run the code in the conditional statement seen a few lines down.
				
				//While the current index in the double array has something in it, move on to the next index
				//If there is nothing in the current index AND the final box has not been added yet, exit the loop and add the box at the
				//current index.
				
				while (loadingImages[row][col] != null && loadingImages[31][17] == null) {
					
					row++;
					if (row > 31) {
						row = 0;
						col++;
					}
					
				}
				
				//If the final box has been added, break the loop and move on
				if (loadingImages[31][17] != null)
					break;
				
				double rand = Math.ceil(Math.random()*750);
				if (rand == 1) {
					
					double rand2 = Math.ceil(Math.random()*3);
					switch ((int) rand2) {
						case 1:	loadingImages[row][col] = Content.RareLoadingIcon1;
								break;
						case 2:	loadingImages[row][col] = Content.RareLoadingIcon2;
								break;
						case 3:	loadingImages[row][col] = Content.RareLoadingIcon3;
								break;
					}
					
				}
				else {
					
					double rand2 = Math.ceil(Math.random()*5);
					
					switch ((int) rand2) {
						case 1:	loadingImages[row][col] = Content.LoadingIcon1;
								break;
						case 2:	loadingImages[row][col] = Content.LoadingIcon2;
								break;
						case 3:	loadingImages[row][col] = Content.LoadingIcon3;
								break;
						case 4:	loadingImages[row][col] = Content.LoadingIcon4;
								break;
						case 5:	loadingImages[row][col] = Content.LoadingIcon5;
								break;
					}
					
				}
				
			}
			
			//If the final box was added, run this code
			if (loadingImages[31][17] != null) {
				
				smallPause++;
				
				if (smallPause > 1) {
					
					currentlyWiping = false;
					currentlyWipeReturning = true;
					
					unloadState(currentState);
					currentState = nextState;
					loadState(currentState);
					
				}
				
			}
			
		}
		
		
		
		//wipe transition part 2
		if (currentlyWipeReturning) {
			
			for (int i = 0; i < WIPE_SPEED; i++) {
				
				int row = 0;
				int col = 0;
				
				//This section will find the first filled index of the array and remove the box in it. Once it reaches the end of the
				//array, it will exit and run the code in the conditional statement seen a few lines down.
				
				//While the current index in the double array does NOT have something in it, move on to the next index
				//If there is something in the current index AND the final box still exists, exit the loop to remove the box at the current
				//index.
				
				while (loadingImages[row][col] == null && loadingImages[31][17] != null) {
					
					row++;
					if (row > 31) {
						row = 0;
						col++;
					}
					
				}
				
				if (loadingImages[31][17] == null)
					break;
				
				loadingImages[row][col] = null;
				
			}
			
			//If the final box was removed, run this code
			if (loadingImages[31][17] == null) {
				currentlyWipeReturning = false;
				lockTransition = false;
			}
			
		}
		
		
		
	}
	
	public void draw(java.awt.Graphics2D g) {
		
		//run the draw() method in the current gamestate
		gameStates[currentState].draw(g);
		
		// draw transition boxes
		for(int i = 0; i < 32; i++) {
			for (int j = 0; j < 18; j++) {
				if (loadingImages[i][j] != null) {
					g.drawImage(loadingImages[i][j], i*20, j*20, null);
				}
			}
		}
		
	}
	
}