package Entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import Audio.JukeBox;
import GameState.GameStateManager;
import Handlers.Content;
import Main.GamePanel;
import Utilities.DynamicText;

public class Player extends Object {
	
	
	
	//PLAYER ATTRIBUTES
	public double speed;
	public double acceleration;
	public int pushPower;
	public double recoilResistance;
	
	public double mainSpeed = 4;
	public double mainAcceleration = 0.1;
	public int mainPushPower = 75;
	public double mainRecoilResistance = 1;
	
	private double enrageSpeedBonus = 1;
	private double enrageAccelerationBonus = 1;
	private int enragePushBonus = 1;
	private double enrageResistanceBonus = 1;
	
	public double powerUpSpeedBonus = 1;
	public double powerUpAccelerationBonus = 1;
	public int powerUpPushBonus = 1;
	public double powerUpResistanceBonus = 1;
	//PLAYER ATTRIBUTES
	
	
	private int playerNumber;
	private int otherPlayerNumber;
	
	private int prevScore;
	private int scoreTextSize = 12;
	protected ArrayList<DynamicText> addScoreText = new ArrayList<DynamicText>();
	
	private ArrayList<Player> players;
	
	private boolean shocked = false;
	private long shockedTimer;
	private int shockedTimerLength;
	
	private double shockedShakeIntensity = 0;
	protected double chargeShakeIntensity = 0;
	private double generalShakeIntensity = 0;
	
	private int CTT = 0;	//Consecutive Thing Touches; increases everytime the thing is touched, decays by 1 every second
	private long CTTTimer;
	private int CTTTimerLength = 1500;
	private Color angerColor;
	
	private boolean enraged = false;
	private long enragedTimer;
	private int enragedTimerLength = 4000;
	private RageEmission rageEmission;
	
	protected boolean[] powerUpCurrentlyActive = new boolean[5];
	protected int currentStarColor = 0;
	private int starColorWaitTime = 0;
	private final int starColorWaitTimeLength = 5;
	protected Color[] starColors = {
			new Color(255, 0, 0, 150),
			new Color(255, 128, 0, 150),
			new Color(255, 255, 0, 150),
			new Color(0, 204, 0, 150),
			new Color(0, 128, 255, 150),
			new Color(128, 0, 255, 150)
		};
	
	
	
	public Player(double startX, int playerNum, ArrayList<Player> players) {
		
		super(Content.Hero1Idle, 0.4);
		
		this.players = players;
		
		if (playerNum == 1) {
			playerNumber = 1;
			otherPlayerNumber = 0;
			changeImage(Content.Hero2Idle);
		}
		else {
			playerNumber = 0;
			otherPlayerNumber = 1;
		}
		
		x = startX;
		y = GamePanel.HEIGHT / 2;
		
		targetX = x;
		targetY = y;
		
		dx = 0;
		dy = 0;
		
	}
	
	//Setters and getters
	
	public void shockPlayer(int duration) {
		
		double rand = Math.ceil(Math.random()*4);
		
		switch ((int) rand) {
		case 1:	JukeBox.play("ThingTouch1");
				break;
		case 2: JukeBox.play("ThingTouch2");
				break;
		case 3: JukeBox.play("ThingTouch3");
				break;
		case 4: JukeBox.play("ThingTouch4");
				break;
		}
		
		shocked = true;
		shockedTimer = System.nanoTime();
		shockedTimerLength = duration;
		
		if (playerNumber == 0)
			changeImage(Content.Hero1Shocked);
		else
			changeImage(Content.Hero2Shocked);
		
	}
	
	public double getSpeed() { return speed; }
	public double getAcceleration() { return acceleration; }
	public int getPushPower() { return pushPower; }
	public double getRecoilResistance() { return recoilResistance; }
	
	public void incrementCTT(int power) { CTT += power; }
	public void resetCTTTimer() { CTTTimer = System.nanoTime(); }
	
	public boolean getEnraged() { return enraged; }
	public void setEnraged(boolean uMad) { enraged = uMad; }
	
	
	
	public void enrage() {
		
		enraged = true;
		generalShakeIntensity += 3;
		
		enrageSpeedBonus = 2;
		enrageAccelerationBonus = 2;
		enragePushBonus = 2;
		enrageResistanceBonus = 0.2;
		
		enragedTimer = System.nanoTime();
		
	}
	
	public void pacify() {
		
		enraged = false;
		generalShakeIntensity -= 3;
		
		enrageSpeedBonus = 1;
		enrageAccelerationBonus = 1;
		enragePushBonus = 1;
		enrageResistanceBonus = 1;
		
		enragedTimer = 0;
		
	}
	
	
	
	public void update() {
		
		//attribute updates
		if (enraged) {
			speed = mainSpeed * enrageSpeedBonus;
			acceleration = mainAcceleration * enrageAccelerationBonus;
			pushPower = mainPushPower * enragePushBonus;
			recoilResistance = mainRecoilResistance * enrageResistanceBonus;
		}
		else {
			speed = mainSpeed * powerUpSpeedBonus;
			acceleration = mainAcceleration * powerUpAccelerationBonus;
			pushPower = mainPushPower * powerUpPushBonus;
			recoilResistance = mainRecoilResistance * powerUpResistanceBonus;
		}
		
		if (!shocked) {
			
			if (left)
				targetX -= speed;
			if (right)
				targetX += speed;
			if (up)
				targetY -= speed;
			if (down)
				targetY += speed;
			
			//Unfortunately, due to how the keys are handled, this is the only way to change the image with key presses. I
			//COULD check pixel by pixel every frame to see if it is the same image as the one it's currently showing, and
			//stop it from running the changeImage method if it is the same image, but that's obviously resource intensive
			//to count every pixel on an image every frame.
			
			//What I should have done is used basic key listeners for every class rather than set up a universal class to
			//handle everything. The universal class runs every frame, and returns whether or not a certain key is being
			//held, while basic key listeners would only fire the first time they're pressed. I think.
			
			//Up
			if (up == true && down == false && left == false && right == false) {
				if (playerNumber == 0)
					changeImage(Content.Hero1U);
				else
					changeImage(Content.Hero2U);
			}
			
			//Down
			else if (up == false && down == true && left == false && right == false) {
				if (playerNumber == 0)
					changeImage(Content.Hero1D);
				else
					changeImage(Content.Hero2D);
			}
			
			//Left
			else if (up == false && down == false && left == true && right == false) {
				if (playerNumber == 0)
					changeImage(Content.Hero1L);
				else
					changeImage(Content.Hero2L);
			}
			
			//Right
			else if (up == false && down == false && left == false && right == true) {
				if (playerNumber == 0)
					changeImage(Content.Hero1R);
				else
					changeImage(Content.Hero2R);
			}
			
			//Up-Left
			else if (up == true && down == false && left == true && right == false) {
				if (playerNumber == 0)
					changeImage(Content.Hero1UL);
				else
					changeImage(Content.Hero2UL);
			}
			
			//Up-Right
			else if (up == true && down == false && left == false && right == true) {
				if (playerNumber == 0)
					changeImage(Content.Hero1UR);
				else
					changeImage(Content.Hero2UR);
			}
			
			//Down-Left
			else if (up == false && down == true && left == true && right == false) {
				if (playerNumber == 0)
					changeImage(Content.Hero1DL);
				else
					changeImage(Content.Hero2DL);
			}
			
			//Down-Right
			else if (up == false && down == true && left == false && right == true) {
				if (playerNumber == 0)
					changeImage(Content.Hero1DR);
				else
					changeImage(Content.Hero2DR);
			}
			
			//Idle
			else if (up == false && down == false && left == false && right == false) {
				if (playerNumber == 0)
					changeImage(Content.Hero1Idle);
				else
					changeImage(Content.Hero2Idle);
			}
			
		}
		
		if (targetX > GamePanel.WIDTH - (width/2))
			targetX = GamePanel.WIDTH - (width/2);
		if (targetX < (width/2))
			targetX = (width/2);
		if (targetY > GamePanel.HEIGHT - (height/2))
			targetY = GamePanel.HEIGHT - (height/2);
		if (targetY < (height/2))
			targetY = (height/2);
		
		dx = targetX - x;
		dy = targetY - y;
		
		x += dx * acceleration;
		y += dy * acceleration;
		
		dx = 0;
		dy = 0;
		
		//Timers
		
		//Shocked timer
		if (shocked) {
			
			long elapsed = (System.nanoTime() - shockedTimer) / 1000000;
			
			if (playerNumber == 0)
				changeImage(Content.Hero1Shocked);
			else
				changeImage(Content.Hero2Shocked);
			
			shockedShakeIntensity = (shockedTimerLength - elapsed) / 125; //Decreases linearly as elapsed increases
			//System.out.println(shockedShakeIntensity);
			
			if (elapsed > shockedTimerLength) {
				
				shocked = false;
				shockedShakeIntensity = 0;
				shockedTimer = 0;
				
				if (playerNumber == 0)
					changeImage(Content.Hero1Idle);
				else
					changeImage(Content.Hero2Idle);
				
			}
			
		}
		
		
		
		//player velocity
		calculateVelocity();
		if (velTimer == 0) {
			x1 = x;
			y1 = y;
			velTimer = System.nanoTime();
		}
		
		
		
		//player enrage
		if (CTT >= 3) {
			
			CTT = 0;
			enrage();
			
			double rad = Math.toRadians(getAngleTo(players.get(otherPlayerNumber)) + 180);
			players.get(otherPlayerNumber).addForce(1000, rad);
			if (rageEmission == null)
				rageEmission = new RageEmission(playerNumber, players);
			
		}
		
		//rage emission update
		if (rageEmission != null) {
			
			boolean remove = rageEmission.update();
			if (remove)
				rageEmission = null;
			
		}
		
		
		
		//CTT decay timer
		if (CTT > 0) {
			
			long elapsed = (System.nanoTime() - CTTTimer) / 1000000;
			
			if (elapsed > CTTTimerLength) {
				
				if (CTT > 0)
					CTT--;
				
				if (CTT == 0)		//If current ConsecutiveThingTouch is 0, it's over and we can stop the timer
					CTTTimer = 0;
				else				//Else, it isn't over and we start again
					CTTTimer = System.nanoTime();
				
			}
			
		}
		
		
		
		//Enraged timer
		if (enraged == true) {
			
			long elapsed = (System.nanoTime() - enragedTimer) / 1000000;
			
			if (playerNumber == 0)
				changeImage(Content.Hero1Enraged);
			else
				changeImage(Content.Hero2Enraged);
			
			if (elapsed > enragedTimerLength) {
				
				pacify();
				
			}
			
		}
		
		
		
		//If players are touching, perform the action
		if (this.checkIfCollidingWith(players.get(otherPlayerNumber))) {
			
			double rad;
			double forceToAdd;
			boolean playSound = false;
			
			//If this player is moving faster than the other player, recoil this player back and thrust the other player forward
			if (this.getVelocity() > players.get(otherPlayerNumber).getVelocity()) {
				
				//Recoil this player back
				rad = Math.toRadians(this.getAngleTo(players.get(otherPlayerNumber)));
				forceToAdd = (this.pushPower / 2) * this.recoilResistance;
				addForce(forceToAdd, rad);
						
				//Thrust other player forward
				rad = Math.toRadians(this.getAngleTo(players.get(otherPlayerNumber)) + 180);
				forceToAdd = this.pushPower * players.get(otherPlayerNumber).getRecoilResistance();
				players.get(otherPlayerNumber).addForce(forceToAdd, rad);
				
				playSound = true;
				
			}
					
			//If this player is moving slower than the other player, do nothing
			else if (this.getVelocity() < players.get(otherPlayerNumber).getVelocity()) {
				
				//Do nothing!
				
			}
			
			//If this player is moving at the same speed as the other player, recoil this player back
			else {
				
				//Recoil this player back
				rad = Math.toRadians(this.getAngleTo(players.get(otherPlayerNumber)) + 180);
				forceToAdd = (this.pushPower * this.recoilResistance) + 25;
				this.addForce(forceToAdd, rad);
				
				playSound = true;
						
			}
			
			if (playSound == true) {
				
				double rand = Math.ceil(Math.random()*3);
				
				switch ((int) rand) {
				case 1:	JukeBox.play("Hit1");
						break;
				case 2:	JukeBox.play("Hit2");
						break;
				case 3:	JukeBox.play("Hit3");
						break;
				}
				
			}
			
		}
		
		//check if addScoreText is ready to be deleted
		for (int i = 0; i < addScoreText.size(); i++)
			if (addScoreText.get(i).readyToBeDeleted == true)
				addScoreText.remove(i);
		
		

	}
	
	public void draw(Graphics2D g) {
		
		
		
		//TINTING THE IMAGE
		//Coloring the player during the star powerup takes precedence over coloring the player when they're mad
		//If the player under the effects of neither, don't color them at all
		
		if (powerUpCurrentlyActive[0] == true || powerUpCurrentlyActive[4] == true) {
			
			starColorWaitTime++;
			if (starColorWaitTime >= starColorWaitTimeLength) {
				currentStarColor++;
				starColorWaitTime = 0;
			}
			
			if (currentStarColor > 5)
				currentStarColor = 0;
			
			colorImage(image, starColors[currentStarColor]);
			
		}
		else if (CTT > 0) {
			
			int tintAlpha = CTT * 50;
			
			if (tintAlpha > 255)
				tintAlpha = 255;
			
			angerColor = new Color(127, 0, 0, tintAlpha);
			colorImage(image, angerColor);
			
		}
		
		
		
		//draw player score
		
		if (GameStateManager.currentState != 7) {
		
			g.setFont(new Font("Century Gothic", Font.PLAIN, scoreTextSize));
			scoreTextSize--;
			if (scoreTextSize < 12)
				scoreTextSize = 12;
			
			if (playerNumber == 0) {
				
				if (prevScore != GameStateManager.player1Score) {
					scoreTextSize = 16;
					g.setColor(Color.BLACK);
					g.drawString("Score: " + GameStateManager.player1Score, 26, 60);
					g.drawString("Score: " + GameStateManager.player1Score, 25, 61);
					g.drawString("Score: " + GameStateManager.player1Score, 24, 60);
					g.drawString("Score: " + GameStateManager.player1Score, 25, 59);
					g.setColor(Color.ORANGE);
					g.drawString("Score: " + GameStateManager.player1Score, 25, 60);
					prevScore = GameStateManager.player1Score;
				}
				else {
					g.setColor(Color.ORANGE);
					g.drawString("Score: " + GameStateManager.player1Score, 25, 60);
				}
				
			}
			else {
				
				if (prevScore != GameStateManager.player2Score) {
					scoreTextSize = 16;
					g.setColor(Color.BLACK);
					g.drawString("Score: " + GameStateManager.player2Score, 26, 75);
					g.drawString("Score: " + GameStateManager.player2Score, 25, 76);
					g.drawString("Score: " + GameStateManager.player2Score, 24, 75);
					g.drawString("Score: " + GameStateManager.player2Score, 25, 74);
					g.setColor(Color.MAGENTA);
					g.drawString("Score: " + GameStateManager.player2Score, 25, 75);
					prevScore = GameStateManager.player2Score;
				}
				else {
					g.setColor(Color.MAGENTA);
					g.drawString("Score: " + GameStateManager.player2Score, 25, 75);
				}
				
			}
		
		}
		
		
		
		//draw the image
		double xDisplace;
		double yDisplace;
		
		//Shake them in general
		xDisplace = Math.random()*(generalShakeIntensity*2) - generalShakeIntensity;
		yDisplace = Math.random()*(generalShakeIntensity*2) - generalShakeIntensity;
		
		//If shocked, give them the ol' shock-jock
		if (shocked) {
			xDisplace += Math.random()*(shockedShakeIntensity*2) - shockedShakeIntensity;
			yDisplace += Math.random()*(shockedShakeIntensity*2) - shockedShakeIntensity;
		}
		
		//If the beam powerup is currently active, shake them as they charge the beam
		if (powerUpCurrentlyActive[2] == true) {
			xDisplace += Math.random()*(chargeShakeIntensity*2) - chargeShakeIntensity;
			yDisplace += Math.random()*(chargeShakeIntensity*2) - chargeShakeIntensity;
		}
		
		g.drawImage(image, (int) (x - (width / 2) + xDisplace), (int) (y - (height / 2) + yDisplace), null);
		
		//draw rage emissions
		if (rageEmission != null)
			rageEmission.draw(g);
		
		//draw addScoreText
		for (int i = 0; i < addScoreText.size(); i++)
			addScoreText.get(i).draw(g);
		
		
		
	}
	
}
