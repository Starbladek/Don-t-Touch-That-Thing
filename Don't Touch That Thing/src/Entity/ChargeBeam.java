package Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import Handlers.Content;
import Handlers.Keys;

public class ChargeBeam extends Object {
	
	
	
	private int playerNumber;
	private int otherPlayerNumber;
	protected double chargeLevel = 25;	//Accessed by the powerup class
	private final double chargeBuildSpeed = 4;
	
	private double r;			//The actual radius
	private double tempR;		//The drawn Radius
	private double secondaryR;	//White circle drawn at whatever the radius is minus 40
	
	private double sinOffset;
	private double sizeDisplace;
	
	protected boolean firingTheBeam = false;
	private int beamWidth;
	private int beamHeight = 100;
	protected boolean chargeReadyToBeDeleted = false;
	
	private boolean rightHasBeenTapped = false;
	private boolean leftHasBeenTapped = true;
	
	private boolean DhasBeenTapped = false;
	private boolean AhasBeenTapped = true;
	
	private AffineTransform transform;
	protected double rotation;
	private Rectangle rectangle;
	private Shape rotatedRectangle;
	
	private ArrayList<Player> players;
	
	
	
	public ChargeBeam(int playerNumber, int otherPlayerNumber, ArrayList<Player> players) {
		
		super(Content.PowerUpBasic, 1);
		
		this.players = players;
		
		this.playerNumber = playerNumber;
		this.otherPlayerNumber = otherPlayerNumber;
		
	}
	
	
	
	public void update() {
		
		x = players.get(playerNumber).x;
		y = players.get(playerNumber).y;
		r = chargeLevel;
		
		chargeLevel -= 0.5;
		if (chargeLevel < 0)
			chargeLevel = 0;
		
		players.get(playerNumber).chargeShakeIntensity = Math.floor(r/20);
		
		if (playerNumber == 0 && firingTheBeam == false) {
			
			if (Keys.keyState[Keys.RIGHT] == true && Keys.keyState[Keys.LEFT] == false && leftHasBeenTapped == true) {
				chargeLevel += chargeBuildSpeed;
				rightHasBeenTapped = true;
				leftHasBeenTapped = false;
			}
			
			if (Keys.keyState[Keys.LEFT] == true && Keys.keyState[Keys.RIGHT] == false && rightHasBeenTapped == true) {
				chargeLevel += chargeBuildSpeed;
				leftHasBeenTapped = true;
				rightHasBeenTapped = false;
			}
			
		}
		else if (playerNumber == 1 && firingTheBeam == false) {
			
			if (Keys.keyState[Keys.D] == true && Keys.keyState[Keys.A] == false && AhasBeenTapped == true) {
				chargeLevel += chargeBuildSpeed;
				DhasBeenTapped = true;
				AhasBeenTapped = false;
			}
			
			if (Keys.keyState[Keys.A] == true && Keys.keyState[Keys.D] == false && DhasBeenTapped == true) {
				chargeLevel += chargeBuildSpeed;
				AhasBeenTapped = true;
				DhasBeenTapped = false;
			}
			
		}
		
	}
	
	public void draw(Graphics2D g) {
		
		
		
		//Pulsate the main turquois charge oval
		if (firingTheBeam == false) {
			sinOffset += 20;
			if (sinOffset > 360)
				sinOffset = 0;
			sizeDisplace = 0.05*Math.sin(Math.toRadians(sinOffset)) + 1;
			tempR = r*sizeDisplace;
		}
		else {
			tempR = r;
		}
		
		g.setColor(new Color(0, 255, 255, 128));
		g.fillOval((int) (x-tempR), (int) (y-tempR), (int) (2*tempR), (int) (2*tempR));
		
		
		
		//Create the secondary white circle
		secondaryR = -40 + r;
		if (secondaryR < 0)
			secondaryR = 0;
		
		g.setColor(new Color(255, 255, 255, 128));
		g.fillOval((int) (x-secondaryR), (int) (y-secondaryR), (int) (2*secondaryR), (int) (2*secondaryR));
		
		
		
		//Draw the beam when the timer runs out
		if (firingTheBeam == true) {
			
			beamWidth = (int) players.get(playerNumber).getDistanceTo(players.get(otherPlayerNumber));
			
			rotation = players.get(playerNumber).getAngleTo(players.get(otherPlayerNumber));
			transform = new AffineTransform();
			transform = AffineTransform.getRotateInstance(Math.toRadians(-rotation + 180), x, y);
			rectangle = new Rectangle((int) x, (int) (y - (beamHeight/2)), beamWidth, beamHeight);
			rotatedRectangle = transform.createTransformedShape(rectangle);
			
			g.fill(rotatedRectangle);
			
			beamHeight -= 2;
			if (beamHeight < 0) {
				beamHeight = 0;
				chargeReadyToBeDeleted = true;
			}
			
		}
		
		
		
	}
	
}