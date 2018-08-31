package Entity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import Audio.JukeBox;
import GameState.GameStateManager;
import Handlers.Content;
import Main.GamePanel;
import Utilities.*;

public class ScoreBall extends Object {
	
	
	
	private int r;
	private int colorNumber;
	private int sinValue;
	private int cosValue;
	
	private boolean wasCollected = false;
	private int currentTweenTime;
	private int tweenDuration = 10;
	private int startR;
	private boolean readyToBeDeleted = false;
	
	private long invincibilityTimer;
	private int invincibilityTimerLength = 1000;
	
	private ArrayList<Player> players;
	
	private Color[] scoreColors = {
			new Color(0, 255, 255, 255),
			new Color(0, 255, 0, 255),
			new Color(255, 102, 255, 255),
			new Color(255, 255, 0, 255),
			new Color(255, 255, 255, 255)
		};
	
	
	
	public ScoreBall(ArrayList<Player> players, Thing thing, boolean startWithInvincibility, int initR, double initX, double initY, double forcePunch, double forceAngle) {
		
		super(Content.PowerUpBasic, 1);
		
		this.players = players;
		
		r = (int) (Math.ceil(Math.random()*4) * 4);
		colorNumber = (int) Math.floor(Math.random()*4);
		
		if (Math.ceil(Math.random()*30) == 1) {
			r = 40;
			colorNumber = 4;
		}
		
		if (initR != -1)
			r = initR;
		
		width = r*2;
		height = r*2;
		
		if (initX == -1 && initY == -1) {
			
			do {
				
				x = (Math.random()*(GamePanel.WIDTH - width*2)) + width;
				y = (Math.random()*(GamePanel.HEIGHT - height*2)) + height;
				
				targetX = x;
				targetY = y;
				
			} while (getDistanceTo(players.get(0)) < 100 || getDistanceTo(players.get(1)) < 100 || getDistanceTo(thing) < 100);
			
		}
		else {
			x = initX;
			y = initY;
			targetX = x;
			targetY = y;
		}
		
		if (forcePunch != -1 && forceAngle != -1) {
			addForce(forcePunch, forceAngle);
		}
		
		if (startWithInvincibility == true)
			invincibilityTimer = System.nanoTime();
		else
			invincibilityTimer = 0;
		
	}
	
	
	
	public boolean getWasCollected() { return wasCollected; }
	public boolean getReadyToBeDeleted() { return readyToBeDeleted; }
	
	public void shrinkOut() {
		startR = r;
		wasCollected = true;
	}
	
	public void update() {
		
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
		
		x += dx * 0.1;
		y += dy * 0.1;
		
		dx = 0;
		dy = 0;
		
		sinValue += 5;
		if (sinValue >= 360)
			sinValue = 0;
		
		double yOffset = 0.5 * ( Math.sin(Math.toRadians(sinValue)) );
		y += yOffset;
		
		cosValue += 5;
		if (cosValue >= 360)
			cosValue = 0;
		
		double xOffset = 0.5 * ( Math.cos(Math.toRadians(cosValue)*2 + 180) );
		x += xOffset;
		
		//player-thing collision
		for (int i = 0; i < players.size(); i++) {
			
			if (players.get(i).checkIfCollidingWith(this) && invincibilityTimer == 0 && wasCollected == false) {
				
				JukeBox.play("Score1");
				if (i == 0)
					players.get(i).addScoreText.add( new DynamicText(85, 53, 1, 0, "+" + String.valueOf(r/4), 3, 35, 50, Color.WHITE, false) );
				else
					players.get(i).addScoreText.add( new DynamicText(85, 68, 1, 0, "+" + String.valueOf(r/4), 3, 35, 50, Color.WHITE, false) );
				if (i == 0)
					GameStateManager.player1Score += (r/4);
				else
					GameStateManager.player2Score += (r/4);
				
				shrinkOut();
				
			}
			
		}
		
		if (wasCollected == true) {
			currentTweenTime++;
			r = (int) Math.ceil(Tweens.linearEase(currentTweenTime, startR, -startR, tweenDuration));
			if (currentTweenTime == tweenDuration)
				readyToBeDeleted = true;
		}
		
		if (invincibilityTimer != 0) {
			long elapsed = (System.nanoTime() - invincibilityTimer) / 1000000;
			if (elapsed > invincibilityTimerLength)
				invincibilityTimer = 0;
		}
		
	}
	
	public void draw(Graphics2D g) {
		
		g.setColor(scoreColors[colorNumber]);
		g.fillOval((int) (x-r), (int) (y-r), (int) (2*r), (int) (2*r));
		g.setStroke(new BasicStroke(3));
		g.setColor(scoreColors[colorNumber].darker());
		g.setColor(scoreColors[colorNumber].darker());
		g.drawOval((int) (x-r), (int) (y-r), (int) (2*r), (int) (2*r));
		g.setStroke(new BasicStroke(1));
		
	}
	
}