package project;

public class Bullet{
	
	private int timer;
	private int owner;
	private int angle;
	private Point position;
	private static final double bulletSpeed = 1.5;
	
	private GameEngine engine;
	
	public Bullet(int player, Point position, int angle, GameEngine e) {
		this.owner = player;
		this.angle = angle;
		this.position = position;		
		this.timer = 1000; //set the time duration of the bullet's presence to 100 frames
		this.engine=e;
	}
	
	public Point getPosition() {
		return position;
	}
	public int getAngle(){
		return angle;
	}
	
	public void removeBullet(){
		if (this.owner==0){
			engine.player1.decreaseNumberOfBulletsFired();
		}else{
			engine.player2.decreaseNumberOfBulletsFired();
		}
		GameEngine.bulletList.remove(this);
	}
	
	public void moveBullet(){
		Point nextPoint = new Point(this.position.getxCoord()
				+ (bulletSpeed * Math.cos(Math.toRadians(90-this.angle))),
				this.position.getyCoord()
				- (bulletSpeed * Math.sin(Math
						.toRadians(90-this.angle))));
		if (wallCrashHorizontal(nextPoint, GameEngine.bulletWidth)){ //if the bullet would go over any horizontal walls
			flipBulletH();
			nextPoint = new Point(this.position.getxCoord()
					+ (bulletSpeed * Math.cos(Math.toRadians(90-this.angle))),
					this.position.getyCoord());
		}else if (wallCrashVertical(nextPoint, GameEngine.bulletWidth)){ // if the bullet would go over any vertical walls
			flipBulletV();
			nextPoint = new Point(this.position.getxCoord(),
					this.position.getyCoord()
					- (bulletSpeed * Math.sin(Math.toRadians(90-this.angle))));
		}else if(cornerCrash(nextPoint,GameEngine.bulletWidth)){
			if(this.currentYSquare()==(int)this.currentYSquare()){
				flipBulletH();
				nextPoint = new Point(this.position.getxCoord()
						+ (bulletSpeed * Math.cos(Math.toRadians(90-this.angle))),
						this.position.getyCoord());
			}else{
				flipBulletV();
				nextPoint = new Point(this.position.getxCoord(),
						this.position.getyCoord()
						- (bulletSpeed * Math.sin(Math.toRadians(90-this.angle))));
			}
		}
		this.position=nextPoint;
	}
	
	public boolean reduceTimer(){
		//count down the timer.  If time is up, remove the bullet.  Return whether the bullet was removed
		timer--;
		if (timer<0){
			this.removeBullet();
			return true;
		}
		return false;
	}
	
	private void flipBulletV(){
		this.angle=(-this.angle) + 360;
	}
	
	private void flipBulletH(){
		if (this.angle>180){
			this.angle = -this.angle+540;
		}else{
			this.angle=-this.angle+180;
		}
	}	
	
	public void tankCollision(){
		//checks whether the bullet has collided with player1 or player2, calling the appropriate functions if so
			if (collision(engine.player1)){
				this.removeBullet();
				engine.player1.hit();
			}else if (collision(engine.player2)){
				this.removeBullet();
				engine.player2.hit();
			}
		
	}
	
	private boolean collision(Player player){
		//work out the distance between the centre of the bullet and the player
		double distance = Point.distance(player.getCoordinates(),this.position);
		//return whether they're overlapping
		return (distance<=(GameEngine.tankWidth/2+GameEngine.bulletWidth/2));
	}
	
	public double currentXSquare() {
		// return the x value of the grid square the centre of the tank is currently in
		for (int i =0; i<7;i++){
			if ((GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*i<this.position.getxCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.position.getxCoord()){
				return i;
			}
		}
		for (int i =0; i<8;i++){
			if ((GameEngine.wallWidth*(i)+GameEngine.squareWidth*i<=this.position.getxCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.position.getxCoord()){
				return i-0.5;
			}
		}
		return -1;//Throw an exception
	}

	public double currentYSquare() {
		// return the y value of the grid square the centre of the tank is currently in
		for (int i =0; i<7;i++){
			if ((GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*i<this.position.getyCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.position.getyCoord()){
				return i;
			}
		}
		for (int i =0; i<8;i++){
			if ((GameEngine.wallWidth*(i)+GameEngine.squareWidth*i<=this.position.getyCoord())&&
					(GameEngine.wallWidth*(i+1)+GameEngine.squareWidth*(i+1))>this.position.getyCoord()){
				return i-0.5;
			}
		}
		return -1;//Throw an exception

	}
	
	private boolean wallCrashVertical(Point p, int w){
		//when given a point p, returns whether or not the circular object of width w at that point is in/over a vertical wall
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
		//when given a point p, returns whether or not the circular object of width w at that point is in/over a horizontal wall
		w=w/2;
		int yWall=0;
		if (this.currentYSquare()!=(int)this.currentYSquare()){
			yWall=1;//offset if tank is currently in the gap between squares
		}
		//same for top:
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
}



