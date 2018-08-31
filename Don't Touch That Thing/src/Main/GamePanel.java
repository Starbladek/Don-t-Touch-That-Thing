//* * * * * Don't Touch That Thing * * * * *
//* * * * * Made by: Michael Perez  * * * * *

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Hello! Welcome to my first almost-real game. This is still a touch-and-go game that I've only made to prove to myself that I can
 * do something on a larger scale than a stupid idle game. Nearly nothing was planned out beforehand, and I just came up with stuff
 * as I went. If you're interested, you can look through it! It's not the neatest thing in the world, but I think I came up with a
 * decent structure. With a lot of help from ForeignGuyMike (https://www.youtube.com/user/ForeignGuyMike). He's pretty cool.
 * 
 * In a nutshell, the game has a lot of different "states" it can be in, such as on the main menu, in level 1, level 2, etc. The game
 * can switch between these states very easily, and each state is its own thing that can have a lot of stuff happen within it.
 * 
 * All entities that are spawned within a level are derived from the class Object, such as the player, the thing, the powerups, etc.
 * Each entity acts independently, for the most part, from the states. That means that they can easily be spawned almost anywhere,
 * with very little that the current state has to do in order to make the objects work properly.
 * 
 * All of the image assets are handled in the Content class. Everything there is public static, so any class can take images from it
 * and use them as they need. All images are loaded upon startup.
 * 
 * All of the sound assets are handled in the JukeBox class. Sounds are NOT loaded on startup, rather they are loaded as needed. Or,
 * rather, they can be. I still load a lot of the things in GameStateManager :P
 * 
 * There are definitely areas that could be improved. I don't know how double inheritence works, or if it's even possible, but the
 * level state classes are pretty much identical. I feel like I could have made a super LevelBase class or something, that all of
 * the levels could have inherited from. But levels already inherit from GameStateManager, so I'm not sure what I would have done.
 * It's fine as it is now, though. Just a lil sloppy.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package Main;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import Handlers.Keys;
import GameState.GameStateManager;

@SuppressWarnings({ "serial", "unused" })
public class GamePanel extends JPanel implements Runnable, KeyListener {
	
	//Dimensions
	public static final int WIDTH = 640;
	public static final int HEIGHT = 360;
	public static final int SCALE = 2;
	
	//Game thread
	private Thread thread;
	private boolean running;
	
	private int FPS = 60;
	private double averageFPS;
	private long targetTime = 1000 / FPS;
	
	//Image
	private BufferedImage image;
	private Graphics2D g;
	
	//Game State Manager
	private GameStateManager gsm;
	
	
	
	public GamePanel() {
		
		super();			//super() calls the constructor of the extended class, in this case JPanel. The constructor in JPanel
							//"Creates a new JPanel with a double buffer and a flow layout."
		
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		
		setFocusable(true);	//Set whether the GamePanel can actually be focused on
		
		requestFocus();		//Gets the focus
		
	}
	
	//You need addNotify in order to have run() execute automatically
	public void addNotify() {
		
		super.addNotify();		//A callback that runs when the JComponent is ready to be displayed
		
		if(thread == null) {	//If there is not a thread, create one
			
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
			
		}
		
	}
	
	private void init() {
		
		running = true;
		
		image = new BufferedImage (WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);	//This is our canvas to work with
		g = (Graphics2D) image.getGraphics();									//This is our "paint brush" for image
		
		//Anti-aliasing bullshit
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		gsm = new GameStateManager();
		
	}
	
	public void run() {
		
		init();
		
		long start;		//The time before the operations
		long elapsed;	//Amount of time spent updating, rendering, and drawing
		long wait;		//How long to wait if the operations finish early

		long totalTime = 0;
		int frameCount = 0;
		int maxFrameCount = FPS;
		
		//- - - - - THE GAME LOOP - - - - -
		//
		//
		while (running) {
			
			gameUpdate();	//Update game
			gameRender();	//Draw
			gameDraw();		//Draw to screen
			
			start = System.nanoTime(); //Get the current time in nano seconds
			
			elapsed = (System.nanoTime() - start) / 1000000;
			
			wait = targetTime - elapsed;
			
			if (wait < 0) wait = 5;
			
			try {
				Thread.sleep(wait);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			totalTime += System.nanoTime() - start;
			
			frameCount++;
			
			if (frameCount == maxFrameCount) {
				
				averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
				frameCount = 0;
				totalTime = 0;
				
			}
			
		}
		//
		//
		//- - - - - THE GAME LOOP - - - - -
		
	}
	
	
	
	private void gameUpdate() {
		
		//game state manager update
		gsm.update();
		
		//keys update
		Keys.update();
		
	}
	
	private void gameRender() {
		
		//draw game state manager
		gsm.draw(g);
		
		
		
		//- - - - - - - - DEBUG TEXT - - - - - - - -
		
		//draw FPS
		//g.setColor(Color.white);
		//g.setFont(new Font("Century Gothic", Font.PLAIN, 12));
		//String s = "FPS: " + averageFPS;
		//g.drawString(s, 10, 20);
		
		//draw distance between players
		//s = "Distance between players: " + (new DecimalFormat("000.0").format(players.get(0).getDistanceTo(players.get(1))));
		//g.drawString(s, 10, 40);
		
		//- - - - - - - - DEBUG TEXT - - - - - - - -
		
	}
	
	private void gameDraw() {
		
		//The fourth and fifth parameters are how we stretch the game to fit the screen
		Graphics g2 = this.getGraphics();								//Get the graphics context of this class?
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);	//Draw what we put on the image canvas in gameRender
		g2.dispose();													//???
		
	}
	
	
	
	public void keyPressed(KeyEvent key) {
		Keys.keySet(key.getKeyCode(), true);
	}
	
	public void keyReleased(KeyEvent key) {
		Keys.keySet(key.getKeyCode(), false);
	}
	
	public void keyTyped(KeyEvent key) {
		
	}
	
}
