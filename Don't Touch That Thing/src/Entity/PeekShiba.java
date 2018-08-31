package Entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import Handlers.Content;
import Main.GamePanel;
import Utilities.Tweens;

public class PeekShiba extends Object {
	
	
	
	private long peekTimer;
	private long peekTimerLength = 2000;
	
	private boolean entering;
	private boolean leaving;
	public boolean readyToBeDeleted;
	
	private int currentTweenTime;
	private int tweenDuration = 20;
	
	private double startX;
	
	private Font font = new Font("Comic Sans MS", Font.PLAIN, 12);
	
	private int phraseNumber;
	private String[] phrases = {
			"u can do it",
			"pls help",
			"where am i",
			"u smell nice",
			"ur great",
			"hey",
			":)",
			"where my memes at",
			"im so glad ur here"
	};
	
	
	
	public PeekShiba() {
		
		super(Content.PeekingShibaInu, 0.5);
		
		peekTimer = System.nanoTime();
		entering = true;
		
		x = -width;
		y = (Math.random()*(GamePanel.HEIGHT - height));
		startX = x;
		
		//Length of phrases is 9, so we math.floor this to get values 0-8
		phraseNumber = (int) Math.floor(Math.random()*phrases.length);
		
	}
	
	public void update() {
		
		if (entering) {
			currentTweenTime++;
			x = Tweens.expoEaseOut(currentTweenTime, startX, width, tweenDuration);
			if (currentTweenTime == tweenDuration) {
				entering = false;
				currentTweenTime = 0;
				startX = x;
			}
		}
		
		if (leaving) {
			currentTweenTime++;
			x = Tweens.expoEaseIn(currentTweenTime, startX, -width, tweenDuration);
			if (currentTweenTime == tweenDuration) {
				readyToBeDeleted = true;
			}
		}
		
		
		
		long elapsed = (System.nanoTime() - peekTimer) / 1000000;
		if (elapsed > peekTimerLength) {
			leaving = true;
		}
		
	}
	
	public void draw(Graphics2D g) {
		
		g.drawImage(image, (int) x, (int) y, null);
		
		if (entering == false && leaving == false) {
			
			g.setFont(font);
			g.setColor(Color.BLUE);
			
			String s = phrases[phraseNumber];
			g.drawString(s, (int) width, (int) ( y + (height/2) + 3 ));
			
		}
		
	}

}