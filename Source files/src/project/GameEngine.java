package project;

import java.util.ArrayList;

public class GameEngine {
	// This class handles movement input, starting and ending rounds.
	public static int player1_score;
	public static int player2_score;
	public static boolean player1_dead = false;
	public static boolean player2_dead = false;
	public static final int tankWidth = 26;
	public static final int bulletWidth = 6;
	public static final int squareWidth = 50;
	public static final int wallWidth = 5;
	public Maze maze;
	public Player player1;
	public Player player2;
	public static ArrayList<Bullet> bulletList = new ArrayList<Bullet>(10);
	public GameEngine() {
		// constructor
		player1_score=0;
		player2_score = 0;
		maze = new Maze();
		// this creates instances of the players and spawns them randomly
		int x = (int) (Math.random() * 7);
		int y = (int) (Math.random() * 7);
		int x1 = (int) (Math.random() * 7);
		int y1 = (int) (Math.random() * 7);
		while (x1 == x && y1==y) {//make sure they don't spawn in the same place
			x1 = (int) (Math.random() * 7);
			y1 = (int) (Math.random() * 7);
		}
		x=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*x;
		y=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*y;
		x1=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*x1;
		y1=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*y1;
		this.player1 = new Player(0, x, y,this);
		this.player2 = new Player(1, x1, y1,this);
	}

	public void roundOver() {
		if (GameEngine.player1_dead) {
			GameEngine.player2_score++;
			GameEngine.player1_dead=false;
		} else if (GameEngine.player2_dead) {
			GameEngine.player1_score++;
			GameEngine.player2_dead=false;
		}
		
		//TODO: pause for a second or two
		// this creates instances of the players and spawns them randomly
		int x = (int) (Math.random() * 7);
		int y = (int) (Math.random() * 7);
		int x1 = (int) (Math.random() * 7);
		int y1 = (int) (Math.random() * 7);
		while (x1 == x && y1==y) {//make sure they don't spawn in the same place
			x1 = (int) (Math.random() * 7);
			y1 = (int) (Math.random() * 7);
		}
		x=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*x;
		y=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*y;
		x1=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*x1;
		y1=GameEngine.wallWidth+GameEngine.squareWidth/2+(GameEngine.wallWidth+GameEngine.squareWidth)*y1;
		this.player1 = new Player(0, x, y,this);
		this.player2 = new Player(1, x1, y1,this);
		GameEngine.bulletList = new ArrayList<Bullet>(10);
		maze = new Maze();
	}




}




