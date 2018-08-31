package Main;

import javax.swing.JFrame;	//The class that creates the window

public class Game {
	
	public static void main(String[] args) {
		
		JFrame window = new JFrame("Don't Touch That Thing");	//Creates the window under the given name
		
		window.setContentPane(new GamePanel());					//Set GamePanel as the thing we want to display?
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	//Sets the default action to take when the user goes to close the window
		
		window.setResizable(false);			//Sets whether or not the window can be resized
		
		window.pack();						//Sets the window size to whatever is specified in the program
		
		//window.setLocationRelativeTo(null);	//Starts the window in the center of the screen
		
		window.setVisible(true);			//Sets if the window is visible or not
		
	}
	
}