package Entity;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;



//abstract means it cannot be called directly, it's only meant to be extended from in other classes
public abstract class Object {
	
	//basic stuff
	protected double x;
	protected double y;
	
	protected double dx;
	protected double dy;
	
	protected double targetX;	//to be used when the desired X position changes dynamically
	protected double targetY;	//to be used when the desired Y position changes dynamically
	
	protected double width;
	protected double height;
	
	//movement stuff
	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;
	
	//image stuff
	protected BufferedImage image;
	protected double scale;
	
	//velocity stuff
	protected double velocity;
	protected double angle;
	
	protected long velTimer;
	protected long velTimerDiff;
	protected int velLength = 25;
	
	protected double x1;
	protected double y1;
	protected double x2;
	protected double y2;
	
	
	
	public Object(BufferedImage imageName, double scale) {
		
		image = imageName;
		this.scale = scale;
		init();
		
	}
	
	public void init() {
		
		width = image.getWidth();
		height = image.getHeight();
		
		if (scale != 1)
			scaleImage(image, scale, scale);
		
	}
	
	
	
	
	
	//To be used when checking if two circular objects are colliding
	public double getDistanceTo(Object obj) {
		
		//Distance formula!
		double dist;
		
		double e1x = x;
		double e1y = y;
		
		double e2x = obj.x;
		double e2y = obj.y;
		
		double dx = e1x - e2x;
		double dy = e1y - e2y;
		
		dist = Math.sqrt(dx*dx + dy*dy);
		
		return dist;
		
	}
	
	public boolean checkIfCollidingWith(Object obj) {
		
		boolean b;
		
		double dist = getDistanceTo(obj);
		
		double e1r = width/2;
		double e2r = obj.width/2;
		
		if (dist < e1r + e2r) {
			b = true;
		}
		else {
			b = false;
		}
		
		return b;
		
	}
	
	public double getAngleTo(Object obj) {
		
		double dx = obj.x - x;
		double dy = obj.y - y;
		
		double newAngle = Math.toDegrees(Math.atan2(dx, dy)) + 90;
		
		if (newAngle < 0) {
			newAngle += 360;
		}
		
		return newAngle;
		
	}
	
	
	
	public void calculateVelocity() {
		
		double dist;
		
		if (velTimer != 0) {
			
			velTimerDiff = (System.nanoTime() - velTimer) / 1000000;
			
			if (velTimerDiff > velLength) {
				
				velTimer = 0;
				x2 = x;
				y2 = y;
				
				double dx = x1 - x2;
				double dy = y1 - y2;
				dist = Math.sqrt(dx*dx + dy*dy);
				
				velocity = dist * (1000 / velLength);	//Velocity = distance traveled * (one second / amount of checks per second)
				angle = Math.toDegrees(Math.atan2(dx, dy)) + 90;
				
				if (angle < 0) {
					angle += 360;
				}
				
			}
			
		}
		
	}
	public double getVelocity() { return velocity; }
	
	public void setCurrentPos() {
		x1 = x;
		y1 = y;
	}
	
	public double getAngle() { return angle; }
	public void setAngle(double newAngle) { angle = newAngle;  }
	
	
	
	public void addForce(double force, double angle) {
		targetX = x + (force*Math.cos(angle));
		targetY = y - (force*Math.sin(angle));
	}
	
	
	
	
	
	public void changeImage(BufferedImage newImage) {
		
		width = newImage.getWidth();
		height = newImage.getHeight();
		
		image = newImage;
		
		scaleImage(image, scale, scale);
		
	}
	
	public void scaleImage(BufferedImage imageToResize, double scaleFactorWidth, double scaleFactorHeight) {
		
		BufferedImage scaledImage = null;
	    
		int newImageWidth = (int) (width*scaleFactorWidth);		//Scaling the image doesn't change its actual width and height, so we
    	int newImageHeight = (int) (height*scaleFactorHeight);	//have to specify what the new image width and height is
    	
    	scaledImage = new BufferedImage(newImageWidth, newImageHeight, imageToResize.getType());
    	Graphics2D g = scaledImage.createGraphics();
    	
    	AffineTransform at = AffineTransform.getScaleInstance(scaleFactorWidth, scaleFactorHeight);
    	g.drawRenderedImage(imageToResize, at);
    	
    	width = newImageWidth;
    	height = newImageHeight;
    	
    	image = scaledImage;
    	
	}
	
	public void colorImage(BufferedImage imageToRecolor, Color color) {
		
        int w = image.getWidth();
        int h = image.getHeight();
        
        BufferedImage coloredImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = coloredImage.createGraphics();
        
        g.drawImage(image, 0, 0, null);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(color);
        g.fillRect(0, 0, w, h);
        g.dispose();
        
        image = coloredImage;
        
	}

	
	
	public void setLeft(boolean b) { left = b; }
	public void setRight(boolean b) { right = b; }
	public void setUp(boolean b) { up = b; }
	public void setDown(boolean b) { down = b; }
	
	
	
}
