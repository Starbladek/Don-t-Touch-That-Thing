package Entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import Handlers.Content;
import Main.GamePanel;

public class FlungStuff extends Object {
	
	private boolean readyToBeDeleted = false;
	private int targetPlayer;
	private double angleToTarget;
	private int direction;
	private int pushPower;
	
	private int rotation = 0;
	private int rotationSpeed = (int) Math.ceil(Math.random()*40) + 20;
	
	private AffineTransform transform;
	
	private boolean hasStruckPlayer = false;
	
	private ArrayList<Player> players;
	
	
	
	public FlungStuff(int thingToFling, int targetPlayer, ArrayList<Player> players, int speed, double size, int pushPower) {
		
		super(Content.ShibaInu1, size);
		
		this.players = players;
		
		if (thingToFling == 1)
			changeImage(Content.ShibaInu1);
		else if (thingToFling == 2)
			changeImage(Content.ShibaInu2);
		
		this.targetPlayer = targetPlayer;
		
		this.pushPower = pushPower;
		
		direction = (int) Math.ceil(Math.random()*4);	//This determines which side of the screen the thing will spawn on
		
		//Spawn above screen
		if (direction == 1) {
			x = Math.random()*GamePanel.WIDTH;
			//y = -(height/2);
			y = 0;
		}
		//Spawn below screen
		else if (direction == 2) {
			x = Math.random()*GamePanel.WIDTH;
			//y = GamePanel.HEIGHT + (height/2);
			y = GamePanel.HEIGHT;
		}
		//Spawn to the left of the screen
		else if (direction == 3) {
			x = 0;
			//x = -(width/2);
			y = Math.random()*GamePanel.HEIGHT;
		}
		//Spawn to the right of the screen
		else if (direction == 4) {
			x = GamePanel.WIDTH;
			//x = GamePanel.WIDTH + (width/2);
			y = Math.random()*GamePanel.HEIGHT;
		}
		
		angleToTarget = Math.toRadians(getAngleTo(players.get(targetPlayer)));
		
		dx = Math.cos(angleToTarget) * speed;
		dy = Math.sin(angleToTarget) * speed;
		
	}
	
	
	
	public boolean getReadyToBeDeleted() { return readyToBeDeleted; }
	
	public void update() {
		
		x -= dx;	//Not sure why this needs to be -= while y needs to be +=
		y += dy;	//but hey, it works
		
		if (checkIfCollidingWith(players.get(targetPlayer)) && hasStruckPlayer == false) {
			
			hasStruckPlayer = true;
			
			if (direction == 1 || direction == 2)
				dy = -dy;
			else if (direction == 3 || direction == 4)
				dx = -dx;
			
			players.get(targetPlayer).addForce(pushPower, Math.toRadians( getAngleTo(players.get(targetPlayer)) + 180 ) );
			
		}
		
		rotation += rotationSpeed;
		if (rotation >= 360)
			rotation = 0;
		
		if (x > GamePanel.WIDTH || x < 0 || y > GamePanel.HEIGHT || y < 0)
			readyToBeDeleted = true;
		
	}
	
	public void draw(Graphics2D g) {
		
		//1: INITIATE THE TRANSFORMATION
		transform = new AffineTransform();
		
		//- - - - STEPS SHOULD BE IN REVERSE ORDER - - - -
		
		//3: TRANSLATE THE IMAGE TO WHERE IT SHOULD BE
		transform.translate(x, y);
		
		//3: ROTATE THE IMAGE ON ITSELF
		transform.rotate(Math.toRadians(rotation));
		
		//2: CENTER THE IMAGE ON ITSELF
		transform.translate(-image.getWidth()/2, -image.getHeight()/2);
		
		//- - - - STEPS SHOULD BE IN REVERSE ORDER - - - -
		
		g.drawImage(image, transform, null);
		
	}
	
}