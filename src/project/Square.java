package project;

public class Square {
	private int xCoord;
	private int yCoord;//so the point is (xCoord,yCoord)
	
	//Basic constructor:
	public Square(int a, int b){
		this.xCoord=a;
		this.yCoord=b;
	}

	//Getter and Setter methods:
	public int getxCoord() {
		return xCoord;
	}
	
	public int getyCoord() {
		return yCoord;
	}
	
	public String toString(){
		return "" + xCoord + "," + yCoord;
	}
	
	public boolean equals(Square a){
		return this.getxCoord()==a.getxCoord()&&this.getyCoord()==a.getyCoord();
	}
}


