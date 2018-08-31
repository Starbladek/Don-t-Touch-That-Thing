package GameState;

import java.awt.*;
import java.util.ArrayList;

import Audio.JukeBox;
import Entity.PeekShiba;
import Handlers.Content;
import Handlers.Keys;
import Main.GamePanel;
import TileMap.Background;

public class MenuState extends GameState {
	
	private Background bg;
	
	private ArrayList<PeekShiba> peekingShibas;
	
	private int currentChoice = 0;
	private String[] options = {
			"Start",
			"Help",
			"Quit"
	};
	
	private Color titleColor;
	private Font titleFont;
	private Font font;

	public MenuState(GameStateManager gsm) {
		
		this.gsm = gsm;
		init();
		
		try {
			
			bg = new Background(Content.MenuBG, 0);
			bg.setVelocity(-0.2, 0);
			
			titleColor = new Color(0, 0, 0);
			titleFont = new Font("Century Gothic", Font.PLAIN, 28);
			
			font = new Font("Arial", Font.PLAIN, 12);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void init() {
		
		peekingShibas = new ArrayList<PeekShiba>();
		
		JukeBox.loop("MenuMusic");
		
	}
	
	public void update() {
		
		//Check for key inputs
		handleInput();
		
		//Update the background
		bg.update();
		
		//Update peeking shibas
		for (int i = 0; i < peekingShibas.size(); i++)
			peekingShibas.get(i).update();
		
		//Check to remove peeking shibas
		for (int i = 0; i < peekingShibas.size(); i++)
			if (peekingShibas.get(i).readyToBeDeleted == true)
				peekingShibas.remove(i);
		
	}
	
	public void draw(Graphics2D g) {
		
		//draw bg
		bg.draw(g);
		
		//draw title
		g.setColor(titleColor);
		g.setFont(titleFont);
		
		String s = "Don't Touch That Thing";
		int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		
		g.drawString(s, (GamePanel.WIDTH - length) / 2, 80);
		
		//draw menu options
		g.setFont(font);
		for (int i = 0; i < options.length; i++) {
			
			if (i == currentChoice)
				g.setColor(Color.BLACK);
			else
				g.setColor(Color.RED);
			
			int length2 = (int) g.getFontMetrics().getStringBounds(options[i], g).getWidth();
			g.drawString(options[i], (GamePanel.WIDTH - length2) / 2, 140 + (i * 15));
			
		}
		
		//draw peeking shibe
		for (int i = 0; i < peekingShibas.size(); i++)
			peekingShibas.get(i).draw(g);
		
	}
	
	private void select() {
		
		if (currentChoice == 0 && GameStateManager.currentlyWiping == false && GameStateManager.currentlyWipeReturning == false) {
			JukeBox.play("Transition1");
			gsm.wipeTransitionState(GameStateManager.LEVEL1STATE);
		}
		if (currentChoice == 1) {
			peekingShibas.add(new PeekShiba());
		}
		if (currentChoice == 2)
			System.exit(0);
		
	}
	
	
	
	public void handleInput() {
		
		if(Keys.isPressed(Keys.UP)) {
			currentChoice--;
			JukeBox.play("MenuSelect");
			if (currentChoice == -1)
				currentChoice = options.length - 1;
		}
		
		if (Keys.isPressed(Keys.DOWN)) {
			currentChoice++;
			JukeBox.play("MenuSelect");
			if(currentChoice == options.length)
				currentChoice = 0;
		}
		
		if (Keys.isPressed(Keys.ENTER))
			select();
		
	}
	
}