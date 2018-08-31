package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import TileMap.Background;
import Audio.JukeBox;
import Entity.*;
import Handlers.Content;
import Handlers.Keys;
import Main.GamePanel;

public class ResultState extends GameState {
	
	private ArrayList<Player> players;
	private Background bg;
	
	private int[] eventTimes = { 2000, 4000, 6000, 10000 };
	private boolean[] eventTriggered = { false, false, false, false };
	
	private int theWinner;
	
	private long startTime;
	
	public ResultState(GameStateManager gsm) {
		
		this.gsm = gsm;
		init();
		
		try {
			
			bg = new Background(Content.ResultsBG);
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
		
		startTime = System.nanoTime();
		
		if (JukeBox.getRunning("StarPower") == true)
			JukeBox.stop("StarPower");
		if (JukeBox.getRunning("PowerMusic") == true)
			JukeBox.stop("PowerMusic");
		JukeBox.setVolume("GameMusic", -10);
		
	}
	
	public void update() {
		
		//check for key inputs
		handleInput();
		
		//background update
		bg.update();
		
		//player update
		for (int i = 0; i < players.size(); i++)
			players.get(i).update();
		
		long elapsed;
		if (eventTriggered[3] != true)
			elapsed = (System.nanoTime() - startTime) / 1000000;
		else
			elapsed = 0;
		
		if (elapsed > eventTimes[0] && eventTriggered[0] == false) {
			eventTriggered[0] = true;
		}
		if (elapsed > eventTimes[1] && eventTriggered[1] == false) {
			eventTriggered[1] = true;
		}
		if (elapsed > eventTimes[2] && eventTriggered[2] == false) {
			eventTriggered[2] = true;
			if (GameStateManager.player1Score > GameStateManager.player2Score) { 
				theWinner = 0;
			}
			else if (GameStateManager.player1Score < GameStateManager.player2Score) {
				theWinner = 1;
			}
			else {
				theWinner = 2;
			}
		}
		if (elapsed > eventTimes[3] && eventTriggered[3] == false) {
			eventTriggered[3] = true;
		}
	
	}
	
	public void draw(Graphics2D g) {
		
		//draw background
		bg.draw(g);
		
		//draw players
		for (int i = 0; i < players.size(); i++)
			players.get(i).draw(g);
		
		//draw texts
		if (eventTriggered[0] == true) {
			
			g.setFont(new Font("Century Gothic", Font.BOLD, 30));
			String s = String.valueOf(GameStateManager.player1Score);
			
			int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
			int height = (int) g.getFontMetrics().getStringBounds(s, g).getHeight();
			
			g.setColor(Color.YELLOW);
			g.drawString(s, (int) (GamePanel.WIDTH*0.3 - length/2), 100 - height/2);
			
		}
		
		if (eventTriggered[1] == true) {
			
			g.setFont(new Font("Century Gothic", Font.BOLD, 30));
			String s = String.valueOf(GameStateManager.player2Score);
			
			int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
			int height = (int) g.getFontMetrics().getStringBounds(s, g).getHeight();
			
			g.setColor(Color.MAGENTA);
			g.drawString(s, (int) (GamePanel.WIDTH*0.7 - length/2), 100 - height/2);
			
		}
		
		if (eventTriggered[2] == true) {
			
			g.setFont(new Font("Century Gothic", Font.BOLD, 50));
			
			String s;
			if (theWinner != 2)
				s = "Player " + (theWinner + 1) + " wins!";
			else
				s = "You both lost >:(";
			
			int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
			int height = (int) g.getFontMetrics().getStringBounds(s, g).getHeight();
			
			if (theWinner == 0)
				g.setColor(Color.YELLOW);
			else if (theWinner == 1)
				g.setColor(Color.MAGENTA);
			else
				g.setColor(Color.RED);
			
			g.drawString(s, GamePanel.WIDTH/2 - length/2, GamePanel.HEIGHT/2 - height/2 + 40);
			
		}
		
		if (eventTriggered[3] == true) {
			
			g.setFont(new Font("Century Gothic", Font.BOLD, 15));
			String s = "(press ESC to return to the menu)";
			
			int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
			int height = (int) g.getFontMetrics().getStringBounds(s, g).getHeight();
			
			g.setColor(Color.WHITE);
			g.drawString(s, GamePanel.WIDTH/2 - length/2, (int) (GamePanel.HEIGHT*0.8 - height/2));
			
		}
		
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