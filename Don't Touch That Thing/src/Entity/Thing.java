package Entity;

import GameState.GameStateManager;
import Handlers.Content;
import Main.GamePanel;

import java.awt.*;
import java.util.ArrayList;

public class Thing extends Object {
	
	
	
	private int CTTPower = 1;	//Consecutive Thing Touch Power (how angry it makes the shibes when they touch it)
	private int sinValue;
	private ArrayList<ScoreBall> scoreBalls;
	private ArrayList<Player> players;
	
	private boolean recentlyHitPlayer;
	private long recentlyHitPlayerTimer;
	private int recentlyHitPlayerTimerLength = 1000;
	
	private double phaseShakeIntensity;
	private double hitShakeIntensity;
	
	private long phaseTimer;
	private int phaseTimerLength = 8000;
	
	private Jumpscare scaryFace;
	
	
	
	public Thing(ArrayList<Player> players, ArrayList<ScoreBall> scoreBalls, double size) {
		
		super(Content.Thing1, size);
		
		this.players = players;
		this.scoreBalls = scoreBalls;
		
		x = GamePanel.WIDTH/2;
		y = GamePanel.HEIGHT/2;
		
		phaseTimer = System.nanoTime();
		
	}
	
	
	
	public void update() {
		
		if (recentlyHitPlayer == true) {
			
			if (GameStateManager.thingPhase == 1)
				changeImage(Content.ThingAttack);
			
		}
		else {
			
			switch (GameStateManager.thingPhase) {
			case 1:
				changeImage(Content.Thing1);
				break;
			case 2:
				changeImage(Content.Thing2);
				break;
			case 3:
				changeImage(Content.Thing3);
				break;
			case 4:
				changeImage(Content.Thing4);
				break;
			case 5:
				changeImage(Content.Thing5);
				break;
			case 6:
				changeImage(Content.Thing6);
				break;
			}
			
		}
		
		sinValue += 3;
		if (sinValue > 360)
			sinValue = 0;
		
		double yOffset = 0.2 * ( Math.sin(Math.toRadians(sinValue)) );
		y += yOffset;
		
		if (GameStateManager.thingPhase > 4)
			phaseShakeIntensity = 1;
		else
			phaseShakeIntensity = 0;
		
		//player-thing collision
		for (int i = 0; i < players.size(); i++) {
			
			if (players.get(i).checkIfCollidingWith(this)) {
				
				if (players.get(i).getEnraged() == true)
					players.get(i).pacify();
				
				double rad = Math.toRadians(this.getAngleTo(players.get(i)) + 180);
				double forceToAdd = ((players.get(i).getVelocity()/100) * 15) + 25;
				
				players.get(i).shockPlayer(1000);	//pump 1000 volts into the shibe
				players.get(i).addForce(forceToAdd, rad);
				
				players.get(i).incrementCTT(CTTPower);
				players.get(i).resetCTTTimer();
				
				recentlyHitPlayerTimer = System.nanoTime();
				recentlyHitPlayer = true;
				
				GameStateManager.thingPhase -= 3;
				if (GameStateManager.thingPhase < 1)
					GameStateManager.thingPhase = 1;
				
				//sput out scoreBalls
				int amountOfPointsLost = 0;
				
				if (i == 0) {
					amountOfPointsLost = (int) Math.floor(GameStateManager.player1Score*0.1);
					GameStateManager.player1Score -= amountOfPointsLost;
				}
				else {
					amountOfPointsLost = (int) Math.floor(GameStateManager.player2Score*0.1);
					GameStateManager.player2Score -= amountOfPointsLost;
				}
				
				int r2SpawnBalls = (int) Math.floor(amountOfPointsLost/2);
				
				for (int j = 0; j < r2SpawnBalls; j++) {
					double randRad = Math.toRadians(this.getAngleTo(players.get(i)) + 100 + (Math.random()*160));
					scoreBalls.add(new ScoreBall(players, this, true, 8, players.get(i).x, players.get(i).y, 200, randRad));
				}
				
			}
			
		}
		
		if (recentlyHitPlayer == true) {
			
			long elapsed = (System.nanoTime() - recentlyHitPlayerTimer) / 1000000;
			hitShakeIntensity = (1000 - elapsed) / 200;
			
			if (elapsed > recentlyHitPlayerTimerLength) {
				recentlyHitPlayer = false;
			}
			
		}
		
		//This if statement is completely unnecessary, but I'm using it to keep things organized and
		//retain the ability to create local variables like 'long elapsed' within
		if (GameStateManager.thingPhase > 0) {
			
			long elapsed = (System.nanoTime() - phaseTimer) / 1000000;
			
			if (elapsed > phaseTimerLength) {
				
				phaseTimer = System.nanoTime();
				
				GameStateManager.thingPhase++;
				if (GameStateManager.thingPhase == 7) {
					
					if (scaryFace == null)
						scaryFace = new Jumpscare(Content.ThingScary, 2.5, 4, 30, "Scare");
					
					GameStateManager.thingPhase = 4;
					
					for (int i = 0; i < players.size(); i++)
						players.get(i).shockPlayer(4000);
					
				}
				
			}
			
		}
		
		if (scaryFace != null) {
			
			scaryFace.update();
			
			if (scaryFace.readyToBeDeleted == true)
				scaryFace = null;
			
		}
		
	}
	
	public void draw(Graphics2D g) {
		
		double totalIntensity = phaseShakeIntensity + hitShakeIntensity;
		
		double xOffset = (Math.random()*(totalIntensity*2)) - totalIntensity;
		double yOffset = (Math.random()*(totalIntensity*2)) - totalIntensity;
		
		g.drawImage(image, (int) (x - (width / 2) + xOffset), (int) (y - (height / 2) + yOffset), null);
		
		if (scaryFace != null)
			scaryFace.draw(g);
		
	}
	
}
