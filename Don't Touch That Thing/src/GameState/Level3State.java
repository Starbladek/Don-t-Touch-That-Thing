package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import Audio.JukeBox;
import Entity.Player;
import Entity.PowerUp;
import Entity.ScoreBall;
import Entity.Thing;
import Handlers.Content;
import Handlers.Keys;
import Main.GamePanel;
import TileMap.Background;

public class Level3State extends GameState {
	
	private ArrayList<Player> players;
	private ArrayList<PowerUp> powerUps;
	private ArrayList<ScoreBall> scoreBalls;
	private Thing thing;
	
	private long powerUpSpawnTimer;
	private long powerUpElapsed;
	private int powerUpSpawnTimerLength = 15000;
	
	private long scoreBallSpawnTimer;
	private long scoreBallElapsed;
	private int scoreBallSpawnTimerLength = 800;
	
	private int timeToLeaveAt = 180;
	
	private Background bg;
	
	
	
	public Level3State(GameStateManager gsm) {
		
		this.gsm = gsm;
		init();
		
		try {
			
			bg = new Background(Content.Level3BG);
			bg.setVelocity(0, 0);
			bg.setPosition(0, 0);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public void init() {
		
		players = new ArrayList<Player>();
		players.add(new Player(GamePanel.WIDTH * 0.3, 0, players));
		players.add(new Player(GamePanel.WIDTH * 0.7, 1, players));
		
		powerUps = new ArrayList<PowerUp>();
		
		scoreBalls = new ArrayList<ScoreBall>();
		scoreBallSpawnTimer = System.nanoTime();
		
		thing = new Thing(players, scoreBalls, 0.8);
		
		powerUpSpawnTimer = System.nanoTime();
		
		if (JukeBox.getRunning("StarPower") == true)
			JukeBox.stop("StarPower");
		if (JukeBox.getRunning("PowerMusic") == true)
			JukeBox.stop("PowerMusic");
		JukeBox.setVolume("GameMusic", -10);
		
	}
	
	public void update() {
		
		//check for key inputs
		handleInput();
		
		//check if level is over
		if (GameStateManager.timeLeft < timeToLeaveAt)
			gsm.wipeTransitionState(GameStateManager.LEVEL4STATE);
		
		//background update
		bg.update();
		bg.sway();
		
		//player update
		for (int i = 0; i < players.size(); i++) {
			players.get(i).update();
		}
		
		//thing update
		thing.update();
		
		//powerup update
		for (int i = 0; i < powerUps.size(); i++) {
			powerUps.get(i).update();
		}
		
		//check for powerup spawn
		powerUpElapsed = (System.nanoTime() - powerUpSpawnTimer) / 1000000;
		if (powerUpElapsed > powerUpSpawnTimerLength) {
			powerUps.add(new PowerUp( (int) Math.floor(Math.random()*4), players ));
			powerUpSpawnTimer = System.nanoTime();
		}
		
		//check if powerup is ready to be deleted
		for (int i = 0; i < powerUps.size(); i++)
			if (powerUps.get(i).getReadyToBeDeleted() == true)
				powerUps.remove(i);
		
		//check for scoreBall spawn
		scoreBallElapsed = (System.nanoTime() - scoreBallSpawnTimer) / 1000000;
		if (scoreBallElapsed > scoreBallSpawnTimerLength) {
			
			scoreBalls.add(new ScoreBall(players, thing, false, -1, -1, -1, -1, -1));
			scoreBallSpawnTimer = System.nanoTime();
			
			if (scoreBalls.size() > 10) {
				
				int ballNumber = 0;
				
				while (scoreBalls.get(ballNumber).getWasCollected() == true)
					ballNumber++;
				
				scoreBalls.get(ballNumber).shrinkOut();
				
			}
			
		}
		
		//update scoreBalls
		for (int i = 0; i < scoreBalls.size(); i++)
			scoreBalls.get(i).update();
		
		//check if scoreBall is ready to be deleted
		for (int i = 0; i < scoreBalls.size(); i++)
			if (scoreBalls.get(i).getReadyToBeDeleted() == true)
				scoreBalls.remove(i);
	
	}
	
	public void draw(Graphics2D g) {
		
		//draw background
		bg.draw(g);
		
		//draw players
		for (int i = 0; i < players.size(); i++) {
			players.get(i).draw(g);
		}
		
		//draw powerups
		for (int i = 0; i < powerUps.size(); i++) {
			powerUps.get(i).draw(g);
		}
		
		for (int i = 0; i < scoreBalls.size(); i++)
			scoreBalls.get(i).draw(g);
		
		//draw thing
		thing.draw(g);
		
		//draw level time left
		g.setFont(new Font("Century Gothic", Font.BOLD, 18));
		String s = String.valueOf(GameStateManager.timeLeft);
		g.setColor(Color.BLACK);
		g.drawString(s, 26, 40);
		g.drawString(s, 24, 40);
		g.drawString(s, 25, 41);
		g.drawString(s, 25, 39);
		g.setColor(Color.WHITE);
		g.drawString(s, 25, 40);
		
	}
	
	

	public void handleInput() {
		
		players.get(0).setUp((Keys.keyState[Keys.UP]));
		players.get(0).setDown((Keys.keyState[Keys.DOWN]));
		players.get(0).setLeft((Keys.keyState[Keys.LEFT]));
		players.get(0).setRight((Keys.keyState[Keys.RIGHT]));
		
		players.get(1).setUp((Keys.keyState[Keys.W]));
		players.get(1).setDown((Keys.keyState[Keys.S]));
		players.get(1).setLeft((Keys.keyState[Keys.A]));
		players.get(1).setRight((Keys.keyState[Keys.D]));
		
		if (Keys.isPressed(Keys.SPACE) == true) {
			int rand = (int) Math.ceil(Math.random()*2);
			if (rand == 1)
				JukeBox.play("Bark1");
			else
				JukeBox.play("Bark2");
		}
		
		if (Keys.keyState[Keys.ESCAPE] == true)
			gsm.wipeTransitionState(GameStateManager.MENUSTATE);
		
	}
	
}