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

public class PowerUp extends Object {
	
	
	
	private int powerUpType;
	private int theStupidValue;
	private int playerNumber;
	private int otherPlayerNumber;
	
	private boolean readyToBeDeleted = false;
	private boolean wasCollectedGeneral = false;
	private boolean was0Collected = false;
	private boolean was1Collected = false;
	private boolean was2Collected = false;
	private boolean was3Collected = false;
	private boolean was4Collected = false;
	
	private ArrayList<DynamicText> texts = new ArrayList<DynamicText>();
	private ArrayList<Player> players;
	
	//Power Type 0
	private int type0Timer = 0;
	private int type0TimerLength = 380;
	
	//Power Type 1
	private long type1Timer = 0;
	private long type1Elapsed;
	private int type1TimerLength = 7000;
	private int swarmTimer;
	private int swarmCooldown = 12;
	private ArrayList<FlungStuff> stuffFlung = new ArrayList<FlungStuff>();
	
	//Power Type 2
	private double chargePeriod = 0;
	private double chargePeriodLength = 255;
	private ChargeBeam[] chargeBeam = new ChargeBeam[2];
	
	//Power Type 3
	private int type3Timer = 0;
	private int type3TimerLength = 420;
	private double scaleFactor;
	private int currentTweenTime;
	private double growthStartScale;
	private double shrinkStartScale;
	private final int scaleDuration = 30;
	private boolean currentlyGrowing = false;
	
	//Power Type 4
	private long type4Timer;
	private int type4Elapsed;
	private int type4TimerLength = 8000;
	private int onslaughtTimer = 0;
	private int onslaughtCooldown = 7;
	private int timerColor;
	
	//I probably could have just had one array of colors and changed the alphas around some other way
	//but this will work just fine
	private Color[] powerUpColors = {
			new Color(255, 0, 0, 100),
			new Color(255, 128, 0, 100),
			new Color(255, 255, 0, 100),
			new Color(0, 204, 0, 100),
			new Color(0, 128, 255, 100),
			new Color(128, 0, 255, 100)
		};
	
	private Color[] rainbowColors = {
			new Color(255, 0, 0, 255),
			new Color(255, 128, 0, 255),
			new Color(255, 255, 0, 255),
			new Color(0, 204, 0, 255),
			new Color(0, 128, 255, 255),
			new Color(128, 0, 255, 255)
		};
	
	private Color[] overlayColors = {
			new Color(255, 0, 0, 40),
			new Color(255, 128, 0, 40),
			new Color(255, 255, 0, 40),
			new Color(0, 204, 0, 40),
			new Color(0, 128, 255, 40),
			new Color(128, 0, 255, 40)
		};
	
	
	
	public PowerUp(int typeValue, ArrayList<Player> players) {
		
		super(Content.PowerUpBasic, 1);
		
		this.players = players;
		
		powerUpType = typeValue;
		//powerUpType = 2;
		
		if (Math.ceil(Math.random()*10) == 1) {
			changeImage(Content.PowerUpSpecial);
			powerUpType = 4;
		}
		
		//Find a new location for the powerup to spawn if it's within 150 units from either player
		do {
			
			x = (Math.random()*(GamePanel.WIDTH - width*2)) + width;
			y = (Math.random()*(GamePanel.HEIGHT - height*2)) + height;
			
		} while (getDistanceTo(players.get(0)) < 150 || getDistanceTo(players.get(1)) < 150);
		
		//colorNumber = (int) Math.floor(Math.random()*6);
		
		if (powerUpType != 4)
			colorImage(image, powerUpColors[powerUpType]);
		
		this.players = players;
		
	}
	
	public boolean getReadyToBeDeleted() { return readyToBeDeleted; }
	
	
	
	public void update() {
		
		if (wasCollectedGeneral == false) {
			theStupidValue += 10;
			if (theStupidValue >= 360)
				theStupidValue = 0;
			double yOffset = 0.5 * ( Math.sin(Math.toRadians(theStupidValue)) );
			y += yOffset;
		}
		
		//player-powerup collision
		for (int i = 0; i < players.size(); i++) {
			
			if (players.get(i).checkIfCollidingWith(this) && wasCollectedGeneral == false) {
				
				JukeBox.play("PowerUp1");
				playerNumber = i;
				
				if (playerNumber == 0)
					otherPlayerNumber = 1;
				else
					otherPlayerNumber = 0;
				
				if (players.get(playerNumber).powerUpCurrentlyActive[powerUpType] == false) {
					
					//INITIALIZE POWER EFFECTS
					
					//Power type 0: Give players increased stats for 7 seconds
					if (powerUpType == 0) {
						texts.add(new DynamicText(players.get(playerNumber).x, players.get(playerNumber).y, 0, 0, "SUPER!!", 1, 35, 4, Color.CYAN, false));
						if (JukeBox.getRunning("StarPower") == false && players.get(playerNumber).powerUpCurrentlyActive[4] != true) {
							JukeBox.loop("StarPower");
							JukeBox.setVolume("GameMusic", -20);
						}
						players.get(playerNumber).powerUpSpeedBonus = 2;
						players.get(playerNumber).powerUpAccelerationBonus = 1.5;
						players.get(playerNumber).powerUpPushBonus = 2;
						players.get(playerNumber).powerUpResistanceBonus = 0.5;
						was0Collected = true;
					}
					
					//Power type 1: Fling objects at the other player
					else if (powerUpType == 1) {
						texts.add(new DynamicText(players.get(playerNumber).x, players.get(playerNumber).y, 0, 0, "SWARM!!", 1, 35, 4, Color.CYAN, false));
						type1Timer = System.nanoTime();
						was1Collected = true;
					}
					
					//Enable both players to charge a beam and whoever has the biggest charge by the end shocks the other player
					else if (powerUpType == 2) {
						texts.add(new DynamicText(players.get(playerNumber).x, players.get(playerNumber).y, 0, 0, "CHARGE!!", 1, 35, 4, Color.CYAN, false));
						chargeBeam[playerNumber] = new ChargeBeam(playerNumber, otherPlayerNumber, players);
						chargeBeam[otherPlayerNumber] = new ChargeBeam(otherPlayerNumber, playerNumber, players);
						players.get(otherPlayerNumber).powerUpCurrentlyActive[powerUpType] = true;	//Because both players get this powerup
						was2Collected = true;
					}
					
					//Power type 3: Increase player size and push power
					else if (powerUpType == 3) {
						texts.add(new DynamicText(players.get(playerNumber).x, players.get(playerNumber).y, 0, 0, "SUPERSIZE!!", 1, 35, 4, Color.CYAN, false));
						growthStartScale = players.get(playerNumber).scale;
						currentTweenTime = 0;
						currentlyGrowing = true;
						was3Collected = true;
					}
					
					else if (powerUpType == 4) {
						
						texts.add(new DynamicText(players.get(playerNumber).x, players.get(playerNumber).y, 0, 0, "AAAAAAH", 1, 35, 4, Color.CYAN, true));
						
						if (JukeBox.getRunning("StarPower") == true)
							JukeBox.stop("StarPower");
						
						JukeBox.loop("PowerMusic");
						JukeBox.setVolume("GameMusic", -20);
						
						type4Timer = System.nanoTime();
						players.get(playerNumber).powerUpSpeedBonus = 2;
						players.get(playerNumber).powerUpAccelerationBonus = 1.5;
						players.get(playerNumber).powerUpPushBonus = 2;
						players.get(playerNumber).powerUpResistanceBonus = 0.5;
						was4Collected = true;
						
					}
					
					//INITIALIZE POWER EFFECTS
					
					
					players.get(playerNumber).powerUpCurrentlyActive[powerUpType] = true;
					wasCollectedGeneral = true;
					
				}
				else {
					readyToBeDeleted = true;
				}
				
			}
			
		}
		
		
		
		//THE TIMERS FOR EVERY POWERUP
		
		
		
		//Power Type 0 Timer
		if (players.get(playerNumber).powerUpCurrentlyActive[0] == true && was0Collected == true) {
			
			type0Timer++;
			
			if (type0Timer > type0TimerLength) {
				
				if (JukeBox.getRunning("StarPower") == true && players.get(otherPlayerNumber).powerUpCurrentlyActive[0] != true) {
					JukeBox.stop("StarPower");
					JukeBox.setVolume("GameMusic", -10);
				}
				
				if (players.get(playerNumber).powerUpCurrentlyActive[4] != true) {
					players.get(playerNumber).powerUpSpeedBonus = 1;
					players.get(playerNumber).powerUpAccelerationBonus = 1;
					players.get(playerNumber).powerUpPushBonus = 1;
					players.get(playerNumber).powerUpResistanceBonus = 1;
				}
				
				players.get(playerNumber).powerUpCurrentlyActive[0] = false;
				readyToBeDeleted = true;
				
			}
			
		}
		
		
		
		//Power Type 1 Timer
		if (players.get(playerNumber).powerUpCurrentlyActive[1] == true && was1Collected == true) {
			
			type1Elapsed = (int) ((System.nanoTime() - type1Timer) / 1000000);
			
			swarmTimer++;
			if (swarmTimer > swarmCooldown) {
				swarmTimer = 0;
				stuffFlung.add(new FlungStuff(1, otherPlayerNumber, players, 20, 0.25, 100));
			}
			
			if (type1Elapsed > type1TimerLength) {
				players.get(playerNumber).powerUpCurrentlyActive[1] = false;
				readyToBeDeleted = true;
			}
			
		}
		
		//Update all flung stuff
		for (int i = 0; i < stuffFlung.size(); i++)
			stuffFlung.get(i).update();
		
		//Check if the stuff flung is ready to be deleted
		for (int i = 0; i < stuffFlung.size(); i++)
			if (stuffFlung.get(i).getReadyToBeDeleted() == true)
				stuffFlung.remove(i);
		
		
		
		//Power Type 2 Timer
		if (players.get(playerNumber).powerUpCurrentlyActive[2] == true && was2Collected == true) {
			
			chargePeriod++;
			
			//When the charging period is over, shock the player with the lower charge value
			if (chargePeriod > chargePeriodLength && chargeBeam[0].firingTheBeam == false && chargeBeam[1].firingTheBeam == false) {
				
				int reduceAmount;
				JukeBox.play("ChargeShot");
				
				if (chargeBeam[playerNumber].chargeLevel > chargeBeam[otherPlayerNumber].chargeLevel) {
					players.get(otherPlayerNumber).shockPlayer(3000);
					players.get(otherPlayerNumber).addForce(1000, Math.toRadians( players.get(playerNumber).getAngleTo(players.get(otherPlayerNumber)) + 180 ));
					chargeBeam[playerNumber].firingTheBeam = true;
					if (otherPlayerNumber == 0) {
						reduceAmount = (int) (GameStateManager.player1Score*0.6);
						GameStateManager.player1Score -= reduceAmount;
						players.get(playerNumber).addScoreText.add( new DynamicText(85, 52, 1, 0, "-" + String.valueOf(reduceAmount), 3, 35, 50, Color.RED, false) );
					}
					else {
						reduceAmount = (int) (GameStateManager.player2Score*0.6);
						GameStateManager.player2Score -= reduceAmount;
						players.get(otherPlayerNumber).addScoreText.add( new DynamicText(85, 68, 1, 0, "-" + String.valueOf(reduceAmount), 3, 35, 50, Color.RED, false) );
					}
				}
				else {
					players.get(playerNumber).shockPlayer(3000);
					players.get(playerNumber).addForce(1000, Math.toRadians( players.get(otherPlayerNumber).getAngleTo(players.get(playerNumber)) + 180 ));
					chargeBeam[otherPlayerNumber].firingTheBeam = true;
					if (playerNumber == 0) {
						reduceAmount = (int) (GameStateManager.player1Score*0.6);
						GameStateManager.player1Score -= reduceAmount;
						players.get(playerNumber).addScoreText.add( new DynamicText(85, 52, 1, 0, "-" + String.valueOf(reduceAmount), 3, 35, 50, Color.RED, false) );
					}
					else {
						reduceAmount = (int) (GameStateManager.player2Score*0.6);
						GameStateManager.player2Score -= reduceAmount;
						players.get(otherPlayerNumber).addScoreText.add( new DynamicText(85, 68, 1, 0, "-" + String.valueOf(reduceAmount), 3, 35, 50, Color.RED, false) );
					}
					
				}
				
			}
			
			//End the powerup after the beam is done firing
			for (int i = 0; i < chargeBeam.length; i++) {
				
				if (chargeBeam[i].chargeReadyToBeDeleted == true) {
					chargePeriod = 0;
					players.get(playerNumber).powerUpCurrentlyActive[2] = false;
					players.get(otherPlayerNumber).powerUpCurrentlyActive[2] = false;
					readyToBeDeleted = true;
				}
				
			}
			
		}
		
		//Update all charge beams
		for (int i = 0; i < chargeBeam.length; i++)
			if (chargeBeam[i] != null)
				chargeBeam[i].update();
		
		
		
		//Power Type 3 Timer
		if (players.get(playerNumber).powerUpCurrentlyActive[3] == true && was3Collected == true) {
			
			type3Timer++;
			
			//I'm so sorry if you're trying to parse my code, this is just so messy :(
			if (currentlyGrowing == true && currentTweenTime < scaleDuration) {
				currentTweenTime++;
				scaleFactor = Utilities.Tweens.linearEase(currentTweenTime, growthStartScale, 0.2, scaleDuration);
				players.get(playerNumber).scale = scaleFactor;
				if (currentTweenTime == scaleDuration) {
					players.get(playerNumber).mainPushPower += 250;
					players.get(playerNumber).mainRecoilResistance -= 0.5;
					shrinkStartScale = players.get(playerNumber).scale;
					currentlyGrowing = false;
					currentTweenTime = 0;
				}
			}
			
			if (type3Timer > type3TimerLength) {
				currentTweenTime++;
				scaleFactor = Utilities.Tweens.linearEase(currentTweenTime, shrinkStartScale, -0.2, scaleDuration);
				players.get(playerNumber).scale = scaleFactor;
				if (currentTweenTime == scaleDuration) {
					players.get(playerNumber).mainPushPower -= 250;
					players.get(playerNumber).mainRecoilResistance += 0.5;
					players.get(playerNumber).powerUpCurrentlyActive[3] = false;
					readyToBeDeleted = true;
				}
			}
			
		}
		
		
		
		if (players.get(playerNumber).powerUpCurrentlyActive[4] == true && was4Collected == true) {
			
			type4Elapsed = (int) ((System.nanoTime() - type4Timer) / 1000000);
			
			onslaughtTimer++;
			if (onslaughtTimer > onslaughtCooldown) {
				onslaughtTimer = 0;
				stuffFlung.add(new FlungStuff(1, otherPlayerNumber, players, 35, 0.25, 100));
			}
			
			if (type4Elapsed > type4TimerLength) {
				if (JukeBox.getRunning("PowerMusic") == true) {
					JukeBox.stop("PowerMusic");
					JukeBox.setVolume("GameMusic", -10);
				}
				players.get(playerNumber).powerUpSpeedBonus = 1;
				players.get(playerNumber).powerUpAccelerationBonus = 1;
				players.get(playerNumber).powerUpPushBonus = 1;
				players.get(playerNumber).powerUpResistanceBonus = 1;
				players.get(playerNumber).powerUpCurrentlyActive[4] = false;
				readyToBeDeleted = true;
			}
			
		}
		
		
		
		//check if texts are ready to be deleted
		for (int i = 0; i < texts.size(); i++)
			if (texts.get(i).readyToBeDeleted == true)
				texts.remove(i);
		
		
		
	}
	
	public void draw(Graphics2D g) {
		
		
		
		//draw the power up, if it hasn't been collected yet
		if (wasCollectedGeneral == false)
			g.drawImage(image, (int) (x - (width / 2)), (int) (y - (height / 2)), null);
		
		
		
		//Power Type 0 meter
		if (was0Collected == true) {

			if (players.get(playerNumber).powerUpCurrentlyActive[0] == true) {
				
				g.setColor(Color.BLACK);
				
				g.fillRect((int) (players.get(playerNumber).x - (players.get(playerNumber).width/2) - 1),
						(int) (players.get(playerNumber).y - (players.get(playerNumber).height/2) - 1),
						(int) (players.get(playerNumber).width - players.get(playerNumber).width * type0Timer / type0TimerLength) + 2,
						4);
				
				g.setColor(players.get(playerNumber).starColors[players.get(playerNumber).currentStarColor]);
				
				g.fillRect((int) (players.get(playerNumber).x - (players.get(playerNumber).width/2)),
						(int) (players.get(playerNumber).y - (players.get(playerNumber).height/2)),
						(int) (players.get(playerNumber).width - players.get(playerNumber).width * type0Timer / type0TimerLength),
						2);
				
			}
			
		}
		
		//Power Type 1 meter
		if (was1Collected == true) {
			
			g.setColor(Color.BLACK);
			g.fillRect((int) (players.get(playerNumber).x - (players.get(playerNumber).width/2) + 1),
					(int) (players.get(playerNumber).y - (players.get(playerNumber).height/2) + 1),
					(int) (players.get(playerNumber).width - players.get(playerNumber).width * type1Elapsed / type1TimerLength),
					3);
			
			g.setColor(Color.WHITE);
			g.fillRect((int) (players.get(playerNumber).x - (players.get(playerNumber).width/2)),
					(int) (players.get(playerNumber).y - (players.get(playerNumber).height/2)),
					(int) (players.get(playerNumber).width - players.get(playerNumber).width * type1Elapsed / type1TimerLength),
					3);
			
		}
		
		//Power Type 2 meter
		if (was2Collected == true) {
			
			double xDisplace;
			double yDisplace;
			double chargeRatio = chargePeriod / chargePeriodLength;
			
			
			
			//Exponentially increase xDisplace relative to the chargeRatio
			xDisplace = Math.random()*(chargeRatio*8) - (chargeRatio*4);
			if (xDisplace < 0)
				xDisplace = -(xDisplace*xDisplace);
			else
				xDisplace = xDisplace*xDisplace;
			if (xDisplace < 1)
				xDisplace = 0;
			
			//Exponentially increase yDisplace relative to the chargeRatio
			yDisplace = Math.random()*(chargeRatio*8) - (chargeRatio*4);
			if (yDisplace < 0)
				yDisplace = -(yDisplace*yDisplace);
			else
				yDisplace = yDisplace*yDisplace;
			if (yDisplace < 1)
				yDisplace = 0;
			
			
			
			g.setColor(Color.BLACK);
			g.fillRect((int) ((GamePanel.WIDTH * 0.2) - 3 + xDisplace),
					(int) (60 + yDisplace),
					(int) (((GamePanel.WIDTH*0.6) - (GamePanel.WIDTH*0.6) * chargeRatio) + 6),
					16);
			
			int tempValue =  (int) chargePeriodLength - (int) chargePeriod;
			
			if (tempValue < 0)
				tempValue = 0;
			
			Color tempColor = new Color(255, tempValue, tempValue, 255);
			g.setColor(tempColor);
			g.fillRect((int) (GamePanel.WIDTH * 0.2 + xDisplace),
					(int) (63 + yDisplace),
					(int) ((GamePanel.WIDTH*0.6) - (GamePanel.WIDTH*0.6) * chargeRatio),
					10);
			
			g.setFont(new Font("Century Gothic", Font.PLAIN, (int) 18));
			String text = "MASH LEFT AND RIGHT!!";
			
			int length = (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
			int height = (int) g.getFontMetrics().getStringBounds(text, g).getHeight();
			
			double xTextDisplace = Math.random()*4 - 2;
			double yTextDisplace = Math.random()*4 - 2;
			
			g.setColor(Color.BLACK);
			g.drawString(text, (int) ((GamePanel.WIDTH/2) - (length / 2) + 1 + xTextDisplace), (int) (85 + (height / 2) + 1 + yTextDisplace));
			g.drawString(text, (int) ((GamePanel.WIDTH/2) - (length / 2) - 1 + xTextDisplace), (int) (85 + (height / 2) - 1 + yTextDisplace));
			g.drawString(text, (int) ((GamePanel.WIDTH/2) - (length / 2) + 1 + xTextDisplace), (int) (85 + (height / 2) - 1 + yTextDisplace));
			g.drawString(text, (int) ((GamePanel.WIDTH/2) - (length / 2) - 1 + xTextDisplace), (int) (85 + (height / 2) + 1 + yTextDisplace));
			g.setColor(Color.RED);
			g.drawString(text, (int) ((GamePanel.WIDTH/2) - (length / 2) + xTextDisplace), (int) (85 + (height / 2) + yTextDisplace));
			
		}
		
		//Power Type 3 meter
		if (was3Collected == true) {
			
			g.setColor(Color.WHITE);
			g.fillRect((int) (players.get(playerNumber).x - (players.get(playerNumber).width/2)),
					(int) (players.get(playerNumber).y - (players.get(playerNumber).height/2) - 7),
					(int) (players.get(playerNumber).width - players.get(playerNumber).width * type3Timer / type3TimerLength),
					6);
			
		}
		
		//Power Type 4 meter
		if (was4Collected == true) {
			
			//Give the meter a border
			g.setColor(Color.BLACK);
			g.fillRect((int) ((GamePanel.WIDTH * 0.2) - 3),
					47,
					(int) ((GamePanel.WIDTH*0.6) - (GamePanel.WIDTH*0.6) * chargePeriod / chargePeriodLength) + 6,
					16);
			
			timerColor++;
			if (timerColor > 5)
				timerColor = 0;
			
			//Color the meter
			g.setColor(rainbowColors[timerColor]);
			g.fillRect((int) (GamePanel.WIDTH * 0.2),
					50,
					(int) ((GamePanel.WIDTH*0.6) - (GamePanel.WIDTH*0.6) * type4Elapsed / type4TimerLength),
					10);
			
			//Overlay the screen
			g.setColor(overlayColors[players.get(playerNumber).currentStarColor]);
			g.fillRect(0,
					0,
					GamePanel.WIDTH,
					GamePanel.HEIGHT);
			
		}
		
		
		
		//draw powerup text
		for (int i = 0; i < texts.size(); i++)
			texts.get(i).draw(g);
		
		
		
		//draw stuff flung
		for (int i = 0; i < stuffFlung.size(); i++)
			stuffFlung.get(i).draw(g);
		
		
		
		//draw charge beam
		for (int i = 0; i < chargeBeam.length; i++)
			if (chargeBeam[i] != null)
				chargeBeam[i].draw(g);
		
		
		
	}
	
}