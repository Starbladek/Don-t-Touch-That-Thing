package Utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class DynamicText {
	
	private double x;
	private double y;
	
	private double dx;
	private double dy;
	
	private String text;
	private int outType;
	private int duration;
	private Color color;
	
	private int textScale;
	
	private boolean shake;
	
	private int elapsed;
	public boolean readyToBeDeleted = false;
	
	//outType 1
	private int growthSinValue;
	private double textSize;
	private int rotation;
	
	//outType 2
	//lol none
	
	//outType 3
	private int textAlpha = 255;
	
	public DynamicText(double x, double y, double dx, double dy, String text, int outType, int textScale, int duration, Color color, boolean shake) {
		
		this.x = x;
		this.y = y;
		
		this.dx = dx;
		this.dy = dy;
		
		this.text = text;
		this.outType = outType;
		this.duration = duration;
		this.color = color;
		
		this.textScale = textScale;
		
		this.shake = shake;
		
	}
	
	public void update() {
		//Everything is done in draw() to make things simpler
	}
	
	public void draw(Graphics2D g) {
		
		elapsed++;
		
		double xDisplace;
		double yDisplace;
		
		if (shake == true) {
			xDisplace = Math.random()*16 - 8;
			yDisplace = Math.random()*16 - 8;
		}
		else {
			xDisplace = 0;
			yDisplace = 0;
		}
		
		//Expand, and then rotate 90 degrees while fading out and shrinking
		if (outType == 1) {
			
			growthSinValue += duration;
			textSize = Math.sin(Math.toRadians(growthSinValue))*textScale;
			g.setFont(new Font("Century Gothic", Font.PLAIN, (int) textSize));
			
			int length = (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
			int height = (int) g.getFontMetrics().getStringBounds(text, g).getHeight();
			
			if (growthSinValue > 90) {
				
				AffineTransform orig = g.getTransform();
				
				rotation += duration*4;
				g.rotate(Math.toRadians(rotation), x, y);
				
				//This gives the text an outline
				g.setColor(Color.BLACK);
				g.drawString(text, (int) (x - (length / 2) + 1 + xDisplace), (int) (y + (height / 2) + 1 + yDisplace));
				g.drawString(text, (int) (x - (length / 2) - 1 + xDisplace), (int) (y + (height / 2) - 1 + yDisplace));
				g.drawString(text, (int) (x - (length / 2) + 1 + xDisplace), (int) (y + (height / 2) - 1 + yDisplace));
				g.drawString(text, (int) (x - (length / 2) - 1 + xDisplace), (int) (y + (height / 2) + 1 + yDisplace));
				g.setColor(color);
				g.drawString(text, (int) (x - (length / 2) + xDisplace), (int) (y + (height / 2) + yDisplace));
				
				g.setTransform(orig);
				
			}
			
			else {
					
				g.setColor(Color.BLACK);
				g.drawString(text, (int) (x - (length / 2) + 1 + xDisplace), (int) (y + (height / 2) + 1 + yDisplace));
				g.drawString(text, (int) (x - (length / 2) - 1 + xDisplace), (int) (y + (height / 2) - 1 + yDisplace));
				g.drawString(text, (int) (x - (length / 2) + 1 + xDisplace), (int) (y + (height / 2) - 1 + yDisplace));
				g.drawString(text, (int) (x - (length / 2) - 1 + xDisplace), (int) (y + (height / 2) + 1 + yDisplace));
				g.setColor(color);
				g.drawString(text, (int) (x - (length / 2) + xDisplace), (int) (y + (height / 2) + yDisplace));
					
			}
			
			if (growthSinValue >= 180)
				readyToBeDeleted = true;
			
		}
		
		//Remove after a set amount of time
		else if (outType == 2) {
			
			if (elapsed > duration)
				readyToBeDeleted = true;
			
		}
		
		//Move in a certain direction and fade out
		else if (outType == 3) {
			
			x += dx;
			y += dy;
			
			g.setFont(new Font("Century Gothic", Font.PLAIN, 12));
			
			int length = (int) g.getFontMetrics().getStringBounds(text, g).getWidth();
			int height = (int) g.getFontMetrics().getStringBounds(text, g).getHeight();
			
			textAlpha -= 255/duration;
			if (textAlpha < 0)
				textAlpha = 0;
			
			g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), textAlpha));
			g.drawString(text, (int) x - (length / 2), (int) y + (height / 2));
			
			if (elapsed > duration)
				readyToBeDeleted = true;
			
		}
		
	}
	
}
