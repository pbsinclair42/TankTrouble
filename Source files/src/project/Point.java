package project;

public class Point {
	private double xCoord;
	private double yCoord;//so the point is (xCoord,yCoord)
	
	//Basic constructor:
	public Point(double a, double b){
		this.setxCoord(a);
		this.setyCoord(b);
	}

	//Getter and Setter methods:
	public double getxCoord() {
		return xCoord;
	}

	public void setxCoord(double xCoord) {
		this.xCoord = xCoord;
	}

	public double getyCoord() {
		return yCoord;
	}

	public void setyCoord(double yCoord) {
		this.yCoord = yCoord;
	}
	
	public void setCoords(double xCoord, double yCoord){
		this.xCoord=xCoord;
		this.yCoord=yCoord;
	}
	
	public String toString(){
		return "" + xCoord + "," + yCoord;
	}
	
	public boolean equals(Point a){
		return this.getxCoord()==a.getxCoord()&&this.getyCoord()==a.getyCoord();
	}
	public static double distance(Point p1, Point p2){
		double xDistance = p1.getxCoord()-p2.getxCoord();
		double yDistance = p1.getyCoord()-p2.getyCoord();
		return Math.sqrt(xDistance*xDistance+yDistance*yDistance);
	}
}


