package project;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

public class GameLauncher extends Canvas implements Runnable {
	//to keep it quiet:
	private static final long serialVersionUID = 1L;

	private GameEngine engine = new GameEngine();
	
	public static final int xDimension=380;
	public static final int yDimension=380;//screen dimensions
	
	private boolean running=false;
	private Thread thread;
	
	//Images used:
	private BufferedImage background;
	private BufferedImage tank1;
	private BufferedImage tank2;
	private BufferedImage bullet;
	private BufferedImage hWall;
	private BufferedImage vWall;
	
	private boolean[] instructionsArray = new boolean[10]; //W,A,S,D,Q,UP,Left,Down,Right,Enter
	
	private synchronized void start(){
		if (running){
			return;
		}
		running=true;
		thread=new Thread(this);
		thread.start();
	}
	
	private void init(){
		//Loads images
		BufferedImageLoader loader = new BufferedImageLoader();
		try{
			background = loader.loadImage("/background.png");
			tank1 = loader.loadImage("/tank1.png");
			tank2 = loader.loadImage("/tank2.png");
			bullet = loader.loadImage("/bullet.png");
			hWall = loader.loadImage("/hWall.png");
			vWall = loader.loadImage("/vWall.png");
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
		addKeyListener(new KeyboardInput(this));
		
	}
	
	private synchronized void stop(){
		if (!running){
			return;
		}
		
		running=false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(1);
	}
	
	
	public void run(){
		init();
		long lastTime = System.nanoTime();
		final double amountOfTicks=60.0;
		double ns = 1000000000/amountOfTicks;
		double delta = 0;
		while(running){
			long now = System.nanoTime();
			delta+=(now-lastTime)/ns;
			lastTime=now;
			if(delta>=1){
				tick();
				render();
				delta--;
			}
		}
		stop();
	}
	
	private void tick(){
		//every frame:
		
		//move the players as necessary{
		if (instructionsArray[0]){
			engine.player1.goForward();
		} else if (instructionsArray[2]){
			engine.player1.reverse();
		}
		if (instructionsArray[1]){
			engine.player1.turnLeft();
		}else if (instructionsArray[3]){
			engine.player1.turnRight();
		}
		if (instructionsArray[5]){
			engine.player2.goForward();
		} else if (instructionsArray[7]){
			engine.player2.reverse();
		}
		if (instructionsArray[6]){
			engine.player2.turnLeft();
		}else if (instructionsArray[8]){
			engine.player2.turnRight();
		}
		//move the players as necessary}
		//fire if necessary{
		if (instructionsArray[4]){
			engine.player1.shoot();
			instructionsArray[4]=false;
		}
		if (instructionsArray[9]){
			engine.player2.shoot();
			instructionsArray[9]=false;
		}
		//TODO: set a delay in firing
		//fire if necessary}
		
		//For each bullet...
		for (int i = 0 ;i<GameEngine.bulletList.size();i++){
			GameEngine.bulletList.get(i).moveBullet();//move it
			boolean removed = GameEngine.bulletList.get(i).reduceTimer();//count down it's timer
			if (!removed){//if the bullet hasn't run out...
				GameEngine.bulletList.get(i).tankCollision();//check for a collision
			}
		}

	}
	private void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs==null){
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		//Draw the objects{
		
		//Draw the background:
		g.drawImage(background, 0, 0, this);
		
		//Draw the walls:
		for (int x = 0; x<7;x++){
			for (int y = 0; y<7; y++){
				if (engine.maze.isWallBelow(x, y)){
					g.drawImage(hWall,(GameEngine.squareWidth+GameEngine.wallWidth)*x,(GameEngine.squareWidth+GameEngine.wallWidth)*(y+1),this);
				}
				if (engine.maze.isWallRight(x, y)){
					g.drawImage(vWall,(GameEngine.squareWidth+GameEngine.wallWidth)*(x+1),(GameEngine.squareWidth+GameEngine.wallWidth)*y,this);
				}
			}
		}
		

		//Draw the rotated tanks:
		double rotationRequired = Math.toRadians(engine.player1.getDirection());
		double locationX = tank1.getWidth() / 2;
		double locationY = tank1.getHeight() / 2;
		AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		g.drawImage(op.filter(tank1, null), (int)(engine.player1.getCoordinates().getxCoord()-GameEngine.tankWidth/2), (int)(engine.player1.getCoordinates().getyCoord()-GameEngine.tankWidth/2), this);
		
		AffineTransform tx2 = AffineTransform.getRotateInstance(Math.toRadians(engine.player2.getDirection()), tank2.getWidth() / 2, tank2.getHeight() / 2);
		AffineTransformOp op2 = new AffineTransformOp(tx2, AffineTransformOp.TYPE_BILINEAR);
		g.drawImage(op2.filter(tank2, null), (int)(engine.player2.getCoordinates().getxCoord()-GameEngine.tankWidth/2), (int)(engine.player2.getCoordinates().getyCoord()-GameEngine.tankWidth/2), this);
		//Draw the bullets:
		for (int i = 0 ;i<GameEngine.bulletList.size();i++){
			g.drawImage(bullet,(int)(GameEngine.bulletList.get(i).getPosition().getxCoord()-GameEngine.bulletWidth/2),(int)(GameEngine.bulletList.get(i).getPosition().getyCoord()-GameEngine.bulletWidth/2),this);
		}
		
		//Draw the objects}
		g.dispose();
		bs.show();
	}
	
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		if (key==KeyEvent.VK_W){
			instructionsArray[0]=true;
		}else if (key==KeyEvent.VK_A){
			instructionsArray[1]=true;
		}else if (key==KeyEvent.VK_S){
			instructionsArray[2]=true;
		}else if (key==KeyEvent.VK_D){
			instructionsArray[3]=true;
		}else if (key==KeyEvent.VK_Q){
			instructionsArray[4]=true;
		}else if (key==KeyEvent.VK_UP){
			instructionsArray[5]=true;
		}else if (key==KeyEvent.VK_LEFT){
			instructionsArray[6]=true;
		}else if (key==KeyEvent.VK_DOWN){
			instructionsArray[7]=true;
		}else if (key==KeyEvent.VK_RIGHT){
			instructionsArray[8]=true;
		}else if (key==KeyEvent.VK_ENTER){
			instructionsArray[9]=true;
		}else if (key==KeyEvent.VK_SPACE){
			//do testy stuff TODO remove
			System.out.println("Player 1: "+GameEngine.player1_score + ", Player 2: "+GameEngine.player2_score);
			//System.out.println(GameEngine.player1.getCoordinates());
			//System.out.println(engine.player1.getBulletsFired());
			//System.out.println(engine.player1.currentXSquare()+","+engine.player1.currentYSquare());
			//System.out.println(engine.bulletList.get(0).currentXSquare());
		}
	}
	public void keyReleased(KeyEvent e){
		int key = e.getKeyCode();
		if (key==KeyEvent.VK_W){
			instructionsArray[0]=false;
		}else if (key==KeyEvent.VK_A){
			instructionsArray[1]=false;
		}else if (key==KeyEvent.VK_S){
			instructionsArray[2]=false;
		}else if (key==KeyEvent.VK_D){
			instructionsArray[3]=false;
		}else if (key==KeyEvent.VK_Q){
			instructionsArray[4]=false;
		}else if (key==KeyEvent.VK_UP){
			instructionsArray[5]=false;
		}else if (key==KeyEvent.VK_LEFT){
			instructionsArray[6]=false;
		}else if (key==KeyEvent.VK_DOWN){
			instructionsArray[7]=false;
		}else if (key==KeyEvent.VK_RIGHT){
			instructionsArray[8]=false;
		}else if (key==KeyEvent.VK_ENTER){
			instructionsArray[9]=false;
		}
	}

	public static void main(String args[]){
		GameLauncher game = new GameLauncher();

		//set window size:
		game.setPreferredSize(new Dimension(GameLauncher.xDimension,GameLauncher.yDimension));
		game.setMaximumSize(new Dimension(GameLauncher.xDimension,GameLauncher.yDimension));
		game.setMinimumSize(new Dimension(GameLauncher.xDimension,GameLauncher.yDimension));

		JFrame frame = new JFrame("Tank Trouble");
		frame.add(game);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		game.start();

	}
}
