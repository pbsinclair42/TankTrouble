package project;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class KeyboardInput extends KeyAdapter {
	private GameLauncher game;
	public KeyboardInput(GameLauncher game){
		this.game=game;
	}
	public void keyPressed(KeyEvent e){
		game.keyPressed(e);
	}
	public void keyReleased(KeyEvent e){
		game.keyReleased(e);
	}
}
