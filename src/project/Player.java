package project;

//import java.util.Map;

public class Player {
	private int playerNumber; // defines whether this is player 1 or 2
	private int numberOfBulletsFired; // stores the number of bullets currently
										// on the field from this player
	private int direction; // the direction the tank is currently facing
							// 0 is North, 180 is South
	private Point coordinates; // the coordinates of the centre of the tank
	private static final int rotationSpeed = 3; // the speed at which each tank can turn
	private static final double forwardSpeed = 1; // the speed at which each tank can move forwards
	private static final double reverseSpeed = -.65; // the speed at which each tank can move backwards
	
	private GameEngine engine;
	
	// Movement methods:
	public void turnRight() {
		this.direction += rotationSpeed;
		if (this.direction>359){
			this.direction-=360;
		}
	}

	public void turnLeft() {
		this.direction -= rotationSpeed;
		if (this.direction<0){
			this.direction+=360;
		}
	}
	private void move(double speed) {
		Point nextPoint = new Point(this.coordinates.getxCoord()
				+ (speed * Math.cos(Math.toRadians(90-this.direction))),
				this.coordinates.getyCoord()
				- (speed * Math.sin(Math
						.toRadians(90-this.direction))));
		if (cornerCrash(nextPoint,GameEngine.tankWidth)){//if the tank would crash into a corner...
			nextPoint = new Point(this.coordinates.getxCoord()//try moving it horizontally
					+ (speed * Math.cos(Math.toRadians(90-this.direction))),
					this.coordinates.getyCoord());
			if(cornerCrash(nextPoint,GameEngine.tankWidth)||wallCrashVertical(nextPoint,GameEngine.tankWidth)||wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){
				nextPoint = new Point(this.coordinates.getxCoord(),//if moving it horizontally would cause a crash, try moving it vertically
						this.coordinates.getyCoord()
						- (speed * Math.sin(Math.toRadians(90-this.direction))));
				if(cornerCrash(nextPoint,GameEngine.tankWidth)||wallCrashVertical(nextPoint,GameEngine.tankWidth)||wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){
					nextPoint= new Point(this.coordinates.getxCoord(),//if moving it vertically would also cause a crash, just don't move it
							this.coordinates.getyCoord());//TODO stop it going through T walls
				}
			}
		}else {
			if (wallCrashVertical(nextPoint,GameEngine.tankWidth)&&wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){
				nextPoint= new Point(this.coordinates.getxCoord(),//if moving it at all would cause a crash, just don't move it
						this.coordinates.getyCoord());
			}
			if (wallCrashVertical(nextPoint, GameEngine.tankWidth)){ //if the tank would go over a vertical wall...

				nextPoint = new Point(this.coordinates.getxCoord(),//just move it vertically
						this.coordinates.getyCoord()
						- (speed * Math.sin(Math.toRadians(90-this.direction))));
			}
			if(wallCrashHorizontal(nextPoint,GameEngine.tankWidth)){//if the tank would go over a horizontal wall...
				System.out.println("test");
				nextPoint = new Point(this.coordinates.getxCoord()//just move it horizontally
						+ (speed * Math.cos(Math.toRadians(90-this.direction))),
						this.coordinates.getyCoord());
			}
		} 
		this.coordinates=nextPoint;
		// move the coordinates of the tank so that it travels a distance of
		// forwardSpeed in direction direction
	}
	public void goForward(){
		move(forwardSpeed);
	}

	public void reverse() {
		move(reverseSpeed);
	}
	// End of movement methods
	
	private boolean wallCrashVertical(Point p, int w){
		//when given a point p, returns whether or not the circular object of width w at that point is in/over a wall
		w=w/2;
		int xWall=0;
		if (this.currentXSquare()!=(int)this.currentXSquare()){
			xWall=1;//offset if tank is currently in the gap between squares
		}
		//if there is a wall to the left of this square,
		boolean byLeftWall = engine.maze.isWallLeft((int)this.currentXSquare(), (int)this.currentYSquare());
		//and the object is in/over the left wall,
		boolean inLeftWall = (p.getxCoord()-w<=
				((int)this.currentXSquare())*GameEngine.squareWidth+((int)this.currentXSquare()+1)*GameEngine.wallWidth);
		//same for right:
		boolean byRightWall = engine.maze.isWallRight((int)this.currentXSquare(), (int)this.currentYSquare());
		boolean inRightWall = (p.getxCoord()+w>=
				((int)this.currentXSquare()+1+xWall)*GameEngine.squareWidth+((int)this.currentXSquare()+1+xWall)*GameEngine.wallWidth);
		return ((byLeftWall&&inLeftWall)||(byRightWall&&inRightWall));
	}
	private boolean wallCrashHorizontal(Point p, int w){
		//when given a point p, returns whether or not the circular object of width w at that point is in/over a wall
		w=w/2;
		int yWall=0;
		if (this.currentYSquare()!=(int)this.currentYSquare()){
			yWall=1;//offset if tank is currently in the gap between squares
		}
		//if there is a wall above this square,
		boolean byTopWall = engine.maze.isWallAbove((int)this.currentXSquare(), (int)this.currentYSquare());
		boolean inTopWall = (p.getyCoord()-w<=
				((int)this.currentYSquare())*GameEngine.squareWidth+((int)this.currentYSquare()+1)*GameEngine.wallWidth);
		//same for bottom
		boolean byBottomWall = engine.maze.isWallBelow((int) this.currentXSquare(), (int) this.currentYSquare());
		boolean inBottomWall = (p.getyCoord()+w>=
				((int)this.currentYSquare()+1+yWall)*GameEngine.squareWidth+((int)this.currentYSquare()+1+yWall)*GameEngine.wallWidth);
		return ((byTopWall&&inTopWall)|(byBottomWall&&inBottomWall));
	}
	
	private boolean cornerCrash(Point p, int w){
		//when given a point p, returns whether or not the circular object of width w at that point is in/over the nearest corner
		w=w/2;
		//Work out the nearest corner to p{
		int x=0;
		int y=0;//remember, here x and y are corners NOT squares
		int xCounter=(int)p.getxCoord();
		while (xCounter>GameEngine.wallWidth+GameEngine.squareWidth/2){
			xCounter=xCounter-GameEngine.wallWidth-GameEngine.squareWidth;
			x++;
		}
		int yCounter=(int)p.getyCoord();
		while (yCounter>GameEngine.wallWidth+GameEngine.squareWidth/2){
			yCounter=yCounter-GameEngine.wallWidth-GameEngine.squareWidth;
			y++;
		}
		//Work out the nearest corner to p}
		//so the nearest corner to p is the corner (x,y)
		//work out if there's a wall in the nearest corner{
		boolean isWallInCorner=engine.maze.isWallAbove(x,y)||engine.maze.isWallLeft(x,y);
		if(x>0){
			isWallInCorner=isWallInCorner||engine.maze.isWallAbove(x-1,y);
		}
		if(y>0){
			isWallInCorner=isWallInCorner||engine.maze.isWallLeft(x,y-1);
		}
		//work out if there's a wall in the nearest corner}
		if(!isWallInCorner){
			return false;//if there's no wall there, there's nothing to crash into
		}
		//Find the distance between p and the closest bit of that corner{
		Point p1=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth),y*(GameEngine.wallWidth+GameEngine.squareWidth));
		Point p2=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth,y*(GameEngine.wallWidth+GameEngine.squareWidth));
		Point p3=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth),y*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth);
		Point p4=new Point(x*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth,y*(GameEngine.wallWidth+GameEngine.squareWidth)+GameEngine.wallWidth);
		double distance1=Point.distance(p1,p);
		double distance2=Point.distance(p2,p);
		double distance3=Point.distance(p3,p);
		double distance4=Point.distance(p4,p);
		double distance=Math.min(distance1, Math.min(distance2,Math.min(distance3,distance4)));
		//Find the distance between p and the closest bit of that corner}
		return distance<w;
	
	}
	
	// Shooting method:
	public void shoot() {
		if (this.numberOfBulletsFired < 5) {
			//declare where the bullet will start:	
			Point bulletStart = new Point(this.coordinates.getxCoord()
					+ ((GameEngine.bulletWidth/2+GameEngine.tankWidth/2) * Math.cos(Math.toRadians(90-this.direction))),
					this.coordinates.getyCoord()
					- ((GameEngine.bulletWidth/2+GameEngine.tankWidth/2) * Math.sin(Math.toRadians(90-this.direction)))); 
			
			
			if (wallCrashVertical(bulletStart, GameEngine.bulletWidth)||wallCrashHorizontal(bulletStart,GameEngine.bulletWidth)||cornerCrash(bulletStart,GameEngine.bulletWidth)){ //if the bullet would start in/over any of the walls
				this.hit();
			} else {
				GameEngine.bulletList.add(new Bullet(playerNumber, bulletStart, direction,engine));
				// create a new bullet instance, travelling in the direction
				// currently faced, with coordinates just in front of where the tank
				// currently is, with owner playerNumber
				this.numberOfBulletsFired += 1;
		    }
		}
	}

	// Death method:
	public void hit() {
		if (this.playerNumber == 0) {
			GameEngine.player1_dead=true;
		} else {
			GameEngine.player2_dead=true;
		}
		engine.roundOver();
	}

	public double currentXSquare() {
		// return the x value of the grid square the centre of the tank is currently in
		for (int i =0; i<7;i++){
			if ((GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*i<this.coordinates.getxCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.coordinates.getxCoord()){
				return i;
			}
		}
		for (int i =0; i<8;i++){
			if ((GameEngine.wallWidth*(i)+GameEngine.squareWidth*i<=this.coordinates.getxCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.coordinates.getxCoord()){
				return i-0.5;
			}
		}
		return -1;//Throw an exception
	}

	public double currentYSquare() {
		// return the y value of the grid square the centre of the tank is currently in
		for (int i =0; i<7;i++){
			if ((GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*i<this.coordinates.getyCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.coordinates.getyCoord()){
				return i;
			}
		}
		for (int i =0; i<8;i++){
			if ((GameEngine.wallWidth*(i)+GameEngine.squareWidth*i<this.coordinates.getyCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.coordinates.getyCoord()){
				return i-0.5;
			}
		}
		return -1;//Throw an exception

	}

	// Constructor
	// Inputs:
	// playerNo: 1 for WASD player, 2 for arrow player
	// x: x coordinate of the starting point
	// y: y coordinate of the starting point
	public Player(int playerNo, double x, double y,GameEngine e) {
		this.playerNumber = playerNo;
		this.numberOfBulletsFired = 0;
		this.direction = (int) (Math.random() * 360);
		this.coordinates = new Point(x, y);
		this.engine=e;
	}
	
	// Getter and setter functions:
	
	public void decreaseNumberOfBulletsFired() {
		if (this.numberOfBulletsFired>0){
			this.numberOfBulletsFired -= 1;
		}else{
			//Throw an exception
			System.out.println("You're trying to set negative bullets fired now, silly thing");
		}
	}
	
	public Point getCoordinates(){
		return this.coordinates;
	}
	
	public int getDirection(){
		return this.direction;
	}
	
	//Probably not needed:
	public void setCoordinates(double x, double y){
		this.coordinates=(new Point(x,y));
	}
	public int getBulletsFired(){
		return this.numberOfBulletsFired;
	}

	// End of getter and setter functions
}



