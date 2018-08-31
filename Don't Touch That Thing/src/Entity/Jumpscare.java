package Entity;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import Audio.JukeBox;
import Main.GamePanel;
import Utilities.Tweens;

public class Jumpscare extends Object {
	
	private BufferedImage jumpscareImage;
	
	private int currentTweenTime;
	private double startScale;
	private double changeInScale;
	private int tweenDuration;
	private double alpha;
	
	private double traceAlpha;
	private double traceScale;
	private int traceTweenDuration;
	
	protected boolean readyToBeDeleted;
	
	public Jumpscare(BufferedImage jumpscareImage, double startScale, double changeInScale, int tweenDuration, String jumpscareNoise) {
		
		super(jumpscareImage, startScale);
		
		this.jumpscareImage = jumpscareImage;
		
		this.startScale = startScale;
		this.changeInScale = changeInScale;
		this.tweenDuration = tweenDuration;
		
		this.traceTweenDuration = tweenDuration/3;
		
		JukeBox.play(jumpscareNoise);
		
		x = GamePanel.WIDTH/2;
		y = GamePanel.HEIGHT/2;
		
	}
	
	public void update() {
		
		currentTweenTime++;
		scale = Tweens.quadEaseOut(currentTweenTime, startScale, changeInScale, tweenDuration);
		alpha = Tweens.quadEaseIn(currentTweenTime, 1, -1, tweenDuration);
		
		changeImage(jumpscareImage);
		
		if (currentTweenTime < traceTweenDuration) {
			traceScale = Tweens.linearEase(currentTweenTime, startScale, changeInScale*3, traceTweenDuration);
			traceAlpha = Tweens.linearEase(currentTweenTime, 0.75, -0.75, traceTweenDuration);
			if (currentTweenTime == (traceTweenDuration-1))
				traceAlpha = 0;
		}
		
		if (currentTweenTime == tweenDuration)
			readyToBeDeleted = true;
		
	}
	
	public void draw(Graphics2D g) {
		
		
		
		//Main spook
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
		g.drawImage(image, (int) (x - (width / 2)), (int) (y - (height / 2)), null);
		
		
		
		//Secondary tracer spook
		if (traceAlpha > 0) {
			
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (traceAlpha)));
			
			BufferedImage before = jumpscareImage;
			
			int w = before.getWidth();
			int h = before.getHeight();
			
			BufferedImage after = new BufferedImage((int) (w*traceScale), (int) (h*traceScale), BufferedImage.TYPE_INT_ARGB);
			
			AffineTransform at = new AffineTransform();
			at.scale(traceScale, traceScale);
			
			AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			after = scaleOp.filter(before, after);
	    	
			g.drawImage(after, (int) (x - (after.getWidth() / 2)), (int) (y - (after.getHeight() / 2)), null);
			
		}
		
		
		
		//Reset alpha back to 1
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		
		
		
	}
	
}
