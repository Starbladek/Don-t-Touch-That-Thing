package Handlers;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import Handlers.Content;

public class Content {
	
	//Player 1
	public static BufferedImage Hero1UL = load("PlayerSprites/Hero1UL");
	public static BufferedImage Hero1U = load("PlayerSprites/Hero1U");
	public static BufferedImage Hero1UR = load("PlayerSprites/Hero1UR");
	
	public static BufferedImage Hero1L = load("PlayerSprites/Hero1L");
	public static BufferedImage Hero1Idle = load("PlayerSprites/Hero1Idle");
	public static BufferedImage Hero1R = load("PlayerSprites/Hero1R");
	
	public static BufferedImage Hero1DL = load("PlayerSprites/Hero1DL");
	public static BufferedImage Hero1D = load("PlayerSprites/Hero1D");
	public static BufferedImage Hero1DR = load("PlayerSprites/Hero1DR");
	
	public static BufferedImage Hero1Shocked = load("PlayerSprites/Hero1Shocked");
	public static BufferedImage Hero1Enraged = load("PlayerSprites/Hero1Enraged");
	
	
	
	//Player 2
	public static BufferedImage Hero2UL = load("PlayerSprites/Hero2UL");
	public static BufferedImage Hero2U = load("PlayerSprites/Hero2U");
	public static BufferedImage Hero2UR = load("PlayerSprites/Hero2UR");
	
	public static BufferedImage Hero2L = load("PlayerSprites/Hero2L");
	public static BufferedImage Hero2Idle = load("PlayerSprites/Hero2Idle");
	public static BufferedImage Hero2R = load("PlayerSprites/Hero2R");
	
	public static BufferedImage Hero2DL = load("PlayerSprites/Hero2DL");
	public static BufferedImage Hero2D = load("PlayerSprites/Hero2D");
	public static BufferedImage Hero2DR = load("PlayerSprites/Hero2DR");
	
	public static BufferedImage Hero2Shocked = load("PlayerSprites/Hero2Shocked");
	public static BufferedImage Hero2Enraged = load("PlayerSprites/Hero2Enraged");
	
	
	
	//Loading Icons
	public static BufferedImage LoadingIcon1 = loadScaledImage("LoadingIcons/LoadingIcon1", 0.25, 0.25);
	public static BufferedImage LoadingIcon2 = loadScaledImage("LoadingIcons/LoadingIcon2", 0.25, 0.25);
	public static BufferedImage LoadingIcon3 = loadScaledImage("LoadingIcons/LoadingIcon3", 0.25, 0.25);
	public static BufferedImage LoadingIcon4 = loadScaledImage("LoadingIcons/LoadingIcon4", 0.25, 0.25);
	public static BufferedImage LoadingIcon5 = loadScaledImage("LoadingIcons/LoadingIcon5", 0.25, 0.25);
	public static BufferedImage RareLoadingIcon1 = loadScaledImage("LoadingIcons/RareLoadingIcon1", 0.25, 0.25);
	public static BufferedImage RareLoadingIcon2 = loadScaledImage("LoadingIcons/RareLoadingIcon2", 0.25, 0.25);
	public static BufferedImage RareLoadingIcon3 = loadScaledImage("LoadingIcons/RareLoadingIcon3", 0.25, 0.25);
	
	
	
	//Thing
	public static BufferedImage Thing1 = load("ThingSprites/Thing1");
	public static BufferedImage Thing2 = load("ThingSprites/Thing2");
	public static BufferedImage Thing3 = load("ThingSprites/Thing3");
	public static BufferedImage Thing4 = load("ThingSprites/Thing4");
	public static BufferedImage Thing5 = load("ThingSprites/Thing5");
	public static BufferedImage Thing6 = load("ThingSprites/Thing6");
	public static BufferedImage ThingScary = load("ThingSprites/ThingScary");
	public static BufferedImage ThingAttack = load("ThingSprites/ThingAttack");
	
	
	
	//Backgrounds
	public static BufferedImage MenuBG = load("Backgrounds/MenuBG");
	public static BufferedImage Level1BG = load("Backgrounds/Level1BG");
	public static BufferedImage Level2BG = load("Backgrounds/Level2BG");
	public static BufferedImage Level3BG = load("Backgrounds/Level3BG");
	public static BufferedImage Level4BG = load("Backgrounds/Level4BG");
	public static BufferedImage Level5BG = load("Backgrounds/Level5BG");
	public static BufferedImage Level6BG = load("Backgrounds/Level6BG");
	public static BufferedImage ResultsBG = load("Backgrounds/ResultsBG");
	
	
	
	//Power Ups
	public static BufferedImage PowerUpBasic = load("Misc/PowerUpBasic");
	public static BufferedImage PowerUpSpecial = load("Misc/PowerUpSpecial");
	
	
	
	//Miscellaneous
	public static BufferedImage ShibaInu1 = load("Misc/ShibaInu1");
	public static BufferedImage ShibaInu2 = load("Misc/ShibaInu2");
	public static BufferedImage RoseShibaInu = load("Misc/RoseShibaInu");
	public static BufferedImage PeekingShibaInu = load ("Misc/PeekingShibaInu");
	
	
	
	public static BufferedImage load(String s) {
		
		BufferedImage image;
		
		try {
			
			image = ImageIO.read(Content.class.getResourceAsStream("/" + s + ".png"));
			return image;
			
		}
		catch(Exception e) {
			
			e.printStackTrace();
			System.out.println("Error loading graphics.");
			System.exit(0);
			
		}
		
		return null;
		
	}
	
	public static BufferedImage loadScaledImage(String s, double scaleFactorWidth, double scaleFactorHeight) {
		
		BufferedImage imageToResize = load(s);
		
		BufferedImage scaledImage = null;
		
		int newImageWidth = (int) ((imageToResize.getWidth())*scaleFactorWidth);
    	int newImageHeight = (int) ((imageToResize.getHeight())*scaleFactorHeight);
    	
    	scaledImage = new BufferedImage(newImageWidth, newImageHeight, imageToResize.getType());
    	Graphics2D g = scaledImage.createGraphics();
    	
    	AffineTransform at = AffineTransform.getScaleInstance(scaleFactorWidth, scaleFactorHeight);
    	g.drawRenderedImage(imageToResize, at);
    	
    	return scaledImage;
    	
	}
	
}