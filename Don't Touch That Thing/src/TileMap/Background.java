package TileMap;

import java.awt.*;
import java.awt.image.*;

public class Background {
	
	private BufferedImage image;

	private double x;
	private double y;
	private double dx;
	private double dy;
	
	private int width;
	private int height;
	
	private boolean currentlyMoving = false;
	private int swayStrength;
	private double destinedX;
	private double destinedY;
	
	private double currentTweenTime;
	private int tweenDuration;
	private double tweenSpeed;	//0.01 is normal, higher is faster, lower is slower
	
	
	
	//ALL VARIATIONS OF THE CONSTRUCTOR
	//
	//
	public Background(BufferedImage BGImage) {
		
		image = BGImage;
		width = image.getWidth();
		height = image.getHeight();
		
		this.swayStrength = 50;
		this.tweenDuration = 500;
		this.tweenSpeed = 0.01;
		
	}
	
	public Background(BufferedImage BGImage, int swayStrength) {
		
		image = BGImage;
		width = image.getWidth();
		height = image.getHeight();
		
		this.swayStrength = swayStrength;
		this.tweenDuration = 500;
		this.tweenSpeed = 0.01;
		
	}
	
	public Background(BufferedImage BGImage, int swayStrength, int tweenDuration) {
		
		image = BGImage;
		width = image.getWidth();
		height = image.getHeight();
		
		this.swayStrength = swayStrength;
		this.tweenDuration = tweenDuration;
		this.tweenSpeed = 0.01;
		
	}
	
	public Background(BufferedImage BGImage, int swayStrength, int tweenDuration, double tweenSpeed) {
		
		image = BGImage;
		width = image.getWidth();
		height = image.getHeight();
		
		this.swayStrength = swayStrength;
		this.tweenDuration = tweenDuration;
		this.tweenSpeed = tweenSpeed;
		
	}
	//
	//
	//ALL VARIATNS OF THE CONSTRUCTOR
	
	
	
	public void setPosition(double newX, double newY) {
		x = newX;
		y = newY;
	}
	
	public void setVelocity(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public void sway() {
		
		if (currentlyMoving == false) {
			
			destinedX = Math.random()*(-swayStrength);
			destinedY = Math.random()*(-swayStrength);
			currentlyMoving = true;
			
		}
		else {
			
			double bx = destinedX - x;
			double by = destinedY - y;
			
			x += bx*tweenSpeed;
			y += by*tweenSpeed;
			
			//x = linearTween(currentTweenTime, startX, destinedX, tweenDuration);
			//y = linearTween(currentTweenTime, startY, destinedY, tweenDuration);
			
			currentTweenTime += 5;
			
			if (currentTweenTime >= tweenDuration) {
				currentTweenTime = 0;
				currentlyMoving = false;
			}
			
		}
		
	}
	
	/*public double linearTween(double currentTime, double startValue, double changeInValue, double duration) {
		
		return changeInValue * (currentTime/duration) + startValue;
		
	}*/
	
	
	
	public void update() {
		
		x += dx;
		while(x <= -width) x += width;
		while(x >= width) x -= width;
		
		y += dy;
		while(y <= -height) y += height;
		while(y >= height) y -= height;
		
	}
	
	public void draw(Graphics2D g) {
		
		g.drawImage(image, (int)x, (int)y, null);
		
		double xBoundary = 0;
		double yBoundary = 0;
		
		if(x < xBoundary) {
			g.drawImage(image, (int)x + width, (int)y, null);
		}
		if(x > xBoundary) {
			g.drawImage(image, (int)x - width, (int)y, null);
		}
		if(y < yBoundary) {
			g.drawImage(image, (int)x, (int)y + height, null);
		}
		if(y > yBoundary) {
			g.drawImage(image, (int)x, (int)y - height, null);
		}
		
	}
	
}
