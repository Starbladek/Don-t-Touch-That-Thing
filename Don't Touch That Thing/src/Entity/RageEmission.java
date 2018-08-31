package Entity;

import java.awt.*;
import java.util.ArrayList;

import Handlers.Content;

public class RageEmission extends Object {
	
	
	
	private double x;
	private double y;
	private double r;
	private int playerNum;
	
	private double radiusModifier = 0;
	
	private ArrayList<Player> players;
	
	
	
	public RageEmission(int pNum, ArrayList<Player> players) {
		
		super(Content.PowerUpBasic, 1);	//The image it sets here does not matter, as it isn't drawn down below anyway
		this.players = players;
		playerNum = pNum;
		
	}
	
	
	
	public boolean update() {
		
		x = players.get(playerNum).x;
		y = players.get(playerNum).y;
		
		radiusModifier += 20;
		
		if (radiusModifier >= 360)
			radiusModifier = 0;
		
		r = 0.2 * (Math.sin(Math.toRadians(radiusModifier))*100) + 120;
		
		boolean shouldWeRemove;
		
		if (players.get(playerNum).getEnraged() == false)
			shouldWeRemove = true;
		else
			shouldWeRemove = false;
			
		return shouldWeRemove;
		
	}
	
	public void draw(Graphics2D g) {
		g.setColor(new Color(255, 0, 0, 128));
		g.fillOval((int) (x-r), (int) (y-r), (int) (2*r), (int) (2*r));
	}
	
	
	
}
