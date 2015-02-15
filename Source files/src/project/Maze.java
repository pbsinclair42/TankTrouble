package project;
import java.util.ArrayList;

public class Maze {
	private static final int gridWidth = 7;
	//number of grid squares in the grid (along and down)
	private static boolean[][][] walls = new boolean[2][gridWidth + 1][gridWidth + 1];
	//array of booleans where true means there is a wall there, false means there isn't
	//walls[0=horizontal, 1=vertical][x][y]
	//so the grid square (a,b) will have:
	//left wall = [1][a][b]
	//right wall = [1][a+1][b]
	//top wall = [0][b][a]
	//bottom wall = [0][b+1][a]
	
	public Maze() {
		new Maze(30);
		//no argument constructor automatically sets the density to the default value of 30
	}
	public Maze(int density) {
		walls = generateMaze(density);
		//create a randomly filled grid of walls, where the higher the density, the more walls there will be
		Square[][] areas = this.mergeAll();
		//find all the areas which are separate from one another
		while (areas.length>1){
			removeWall(areas[0]);
			//remove a wall between two adjacent separate areas
			areas = this.mergeAll();
			//find the seperate areas again
		}
	}

	public boolean isWallLeft(int x, int y) {
		//inputs: x: x coordinate of the grid square being checked, y: y coordinate of the grid square being checked
		//output: true if there is a wall to the left of the square, false otherwise
		return walls[1][x][y];
	}

	public boolean isWallRight(int x, int y) {
		//inputs: x: x coordinate of the grid square being checked, y: y coordinate of the grid square being checked
		//output: true if there is a wall to the right of the square, false otherwise
		return walls[1][x + 1][y];
	}

	public boolean isWallAbove(int x, int y) {
		//inputs: x: x coordinate of the grid square being checked, y: y coordinate of the grid square being checked
		//output: true if there is a wall above the square, false otherwise
		return walls[0][y][x];
	}

	public boolean isWallBelow(int x, int y) {
		//inputs: x: x coordinate of the grid square being checked, y: y coordinate of the grid square being checked
		//output: true if there is a wall below the square, false otherwise
		return walls[0][y + 1][x];
	}

	private Square[] neighbours(Square square) {
		// returns all squares adjacent to and in the same area as the square inputed, including itself
		// inputs: square: the square to be checked
		// output: an array of all squares that fit the description above
		Square[] neighbours = new Square[5];
		int numberOfNeighbours = 0;
		//find all neighbours{
		if (!isWallLeft(square.getxCoord(), square.getyCoord())) {
			neighbours[numberOfNeighbours] = new Square(square.getxCoord() - 1,
					square.getyCoord());
			numberOfNeighbours++;
		}
		if (!isWallRight(square.getxCoord(), square.getyCoord())) {
			neighbours[numberOfNeighbours] = new Square(square.getxCoord() + 1,
					square.getyCoord());
			numberOfNeighbours++;
		}
		if (!isWallAbove(square.getxCoord(), square.getyCoord())) {
			neighbours[numberOfNeighbours] = new Square(square.getxCoord(),
					square.getyCoord() - 1);
			numberOfNeighbours++;
		}
		if (!isWallBelow(square.getxCoord(), square.getyCoord())) {
			neighbours[numberOfNeighbours] = new Square(square.getxCoord(),
					square.getyCoord() + 1);
			numberOfNeighbours++;
		}
		neighbours[numberOfNeighbours] = square;
		//find all neighbours}
		
		// remove nulls{
		int realLength = 0;
		boolean found = false;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i] == null) {
				realLength = i;
				found = true;
				break;
			}
		}
		if (found) {
			Square[] cutReturnValue = new Square[realLength];
			for (int i = 0; i < realLength; i++) {
				cutReturnValue[i] = neighbours[i];
			}
			return cutReturnValue;
		} else {
			return neighbours;
		}
		//remove nulls}
	}
	private static boolean isNeighbour(Square a, Square b){
		//takes two squares and says whether or not they are direct neighbours
		int x = a.getxCoord();
		int y = a.getyCoord();
		int x2 = b.getxCoord();
		int y2 = b.getyCoord();
		return (x2-1==x && y2==y)||(x2+1==x && y2==y)||(x2==x && y2-1==y)||(x2==x && y2+1==y);
	}
	private void removeWall(Square[] area){
		// when given an array of squares that make up an area, remove a random wall (or two) between that area and another
		// ie connect that area to a random other one (or two)
		Square[] neighbours = areaNeighbours(area);
		//find all neighbours of the area
		int numberToRemove=2;
		//may or may not want to remove multiple walls to connect to multiple areas?
		for (int j = 0; j<numberToRemove;j++){
			
		
		int randomNum = (int) (Math.random()*neighbours.length);
		Square chosen = neighbours[randomNum];
		//choose a random square from the list of neighbours
		Square areaSquareChosen = new Square(0,0);
		for (int i = 0; i<area.length; i++){
			if (isNeighbour(area[i],chosen)){
				areaSquareChosen=area[i];
				break;
			}
		}
		//find a square in the area adjacent to that one
		int x = chosen.getxCoord();
		int y = chosen.getyCoord();
		int x2 = areaSquareChosen.getxCoord();
		int y2 = areaSquareChosen.getyCoord();
		if (x==x2){
			walls[0][Math.max(y,y2)][x]=false;
		}else{
			walls[1][Math.max(x2, x)][y]=false;
		}
		}
		//remove the wall between those two squares
	}
	private Square[] areaNeighbours(Square[] area){
		//when given an array of squares that make up an area, returns all neighbours of that area
		//doesn't return any squares in the area itself
		Square[] neighbours = new Square[49];
		//return all walled neighbours{
		for (int i = 0; i<area.length;i++){
			neighbours =  merge(neighbours,walledNeighbours(area[i]));	
		}
		//return all walled neighbours}
		//remove any squares found that are actually in the area itself{
		Square[] cutNeighbours = new Square[neighbours.length];
		int cutNeighboursLength=0;
		for (int i = 0;i<neighbours.length;i++){
			boolean found = false;
			for (int j=0;j<area.length;j++){
				if (neighbours[i].equals(area[j])){
					found = true;
				}
			}
			if (!found){
				cutNeighbours[cutNeighboursLength]=neighbours[i];
				cutNeighboursLength++;
			}
		}
		//remove any squares found that are actually in the area itself}
		//remove nulls{
		Square[] returnValue = new Square[cutNeighboursLength];
		for (int i = 0; i<cutNeighboursLength;i++){
			returnValue[i]=cutNeighbours[i];
		}
		//remove nulls}
		return returnValue;
	}
	private Square[] walledNeighbours(Square square){
		//returns all neighbour squares of the input square which are separated by a wall
		Square[] neighbours = new Square[4];
		int numberOfNeighbours = 0;
		//find all walled neighbours{
		if (isWallLeft(square.getxCoord(), square.getyCoord())&&square.getxCoord()-1>=0) {
			neighbours[numberOfNeighbours] = new Square(square.getxCoord() - 1,
					square.getyCoord());
			numberOfNeighbours++;
		}
		if (isWallRight(square.getxCoord(), square.getyCoord())&&square.getxCoord()+1<7) {
			neighbours[numberOfNeighbours] = new Square(square.getxCoord() + 1,
					square.getyCoord());
			numberOfNeighbours++;
		}
		if (isWallAbove(square.getxCoord(), square.getyCoord())&&square.getyCoord()-1>=0) {
			neighbours[numberOfNeighbours] = new Square(square.getxCoord(),
					square.getyCoord() - 1);
			numberOfNeighbours++;
		}
		if (isWallBelow(square.getxCoord(), square.getyCoord())&&square.getyCoord()+1<7) {
			neighbours[numberOfNeighbours] = new Square(square.getxCoord(),
					square.getyCoord() + 1);
			numberOfNeighbours++;
		}
		//find all walled neighbours}
		// remove nulls{
		int realLength = 0;
		boolean found = false;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i] == null) {
				realLength = i;
				found = true;
				break;
			}
		}
		if (found) {
			Square[] cutReturnValue = new Square[realLength];
			for (int i = 0; i < realLength; i++) {
				cutReturnValue[i] = neighbours[i];
			}
			return cutReturnValue;
		} else {
			return neighbours;
		}
		//remove nulls}
	}

	private Square[][] connections() {
		//returns a list of what other squares each square in the maze is connected to
		Square[][] connections = new Square[49][5];
		for (int i = 0; i < 7; i++) { 
			for (int j = 0; j < 7; j++) { //for each square...
				connections[i + 7 * j] = neighbours(new Square(i, j));//find its neighbours
			}
		}
		return connections;
	}

	private Square[][] mergeAll() {
		//returns a list of all separate areas in the maze
		Square[][] set = connections();
		//find the squares each square is connected to
		//convert the array into a list{
		ArrayList<Square[]> list = new ArrayList<Square[]>(49);
		for (int i = 0; i < 49; i++) {
			list.add(set[i]);
		}
		//convert the array into a list}
		//merge all the areas with squares in common{
		for (int i = 0; i<list.size()-1;i++){
			for (int j = (i+1); j<list.size();j++){
				if (intersection(list.get(i), list.get(j)).length != 0) {
					//if the two connections have any squares in common...
					list.set(i, merge(list.get(i), list.get(j)));
					//store that both connections are one
					list.remove(j);
					i=0;
					j=0;
				}//do this for 1;2, 1;3; 1;4, 1;5, 1;6... 2;3, 2;4, 2;5, ... n-1;n , resetting each time a merge is successful
			}
		}
		//merge all the areas with squares in common}
		//convert back to an array{
		Square[][] returnValue = new Square[list.size()][49];
		for (int i = 0; i < list.size(); i++) {
			returnValue[i]=list.get(i);
		}
		//convert back to an array}
		return returnValue;
	}
	//TODO: continue adding comments from here onwards
	private static Square[] merge(Square[] a, Square[] b) {
		Square[] returnValue = new Square[b.length + a.length];
		for (int i = 0; i < a.length; i++) {
			if (!(a[i] == null)) {
				returnValue[i] = a[i];
			}
		}// return every value in a that isn't null
		for (int i = 0; i < b.length; i++) {// check every value in b
			boolean found = false;
			for (int j = 0; j < a.length; j++) {
				if (!(a[j] == null) && !(b[i] == null) && b[i].equals(a[j])) {
					found = true;
				}
			}// check whether that value is already in a
			if (!found && !(b[i] == null)) {// if not, return it too
				for (int j = 0; j < returnValue.length; j++) {
					if (returnValue[j] == null) {
						returnValue[j] = b[i];
						break;
					}
				}
			}
		}
		// remove nulls
		int realLength = 0;
		boolean found = false;
		for (int i = 0; i < returnValue.length; i++) {
			if (returnValue[i] == null) {
				realLength = i;
				found = true;
				break;
			}
		}
		if (found) {
			Square[] cutReturnValue = new Square[realLength];
			for (int i = 0; i < realLength; i++) {
				cutReturnValue[i] = returnValue[i];
			}
			return cutReturnValue;
		} else {
			return returnValue;
		}
	}

	private static Square[] intersection(Square[] a, Square[] b) {
		Square[] returnValue = new Square[b.length];
		for (int i = 0; i < b.length; i++) {// check every value in b
			boolean found = false;
			for (int j = 0; j < a.length; j++) {
				if (!(a[j] == null) && !(b[i] == null) && b[i].equals(a[j])) {
					found = true;
				}
			}// check whether that value is also in a
			if (found && !(b[i] == null)) {// if so, return it too
				for (int j = 0; j < returnValue.length; j++) {
					if (returnValue[j] == null) {
						returnValue[j] = b[i];
						break;
					}
				}
			}
		}
		// remove nulls
		int realLength = 0;
		boolean found = false;
		for (int i = 0; i < returnValue.length; i++) {
			if (returnValue[i] == null) {
				realLength = i;
				found = true;
				break;
			}
		}
		if (found) {
			Square[] cutReturnValue = new Square[realLength];
			for (int i = 0; i < realLength; i++) {
				cutReturnValue[i] = returnValue[i];
			}
			return cutReturnValue;
		} else {
			return returnValue;
		}
	}

	private boolean[][][] generateMaze(int density) {
		boolean[][][] walls = generateEmptyMaze();
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < density; i++) {
				int randomNumber = (int) (Math.random() * (7));
				int randomNumber2 = (int) (Math.random() * (7));
				walls[j][randomNumber][randomNumber2] = true;
			}
		}
		return walls;
	}

	private boolean[][][] generateEmptyMaze() {
		boolean[][][] walls = new boolean[2][gridWidth + 1][gridWidth + 1];
		for (int i = 0; i < 7; i++) {
			walls[0][0][i] = true;
			walls[0][7][i] = true;
			walls[1][0][i] = true;
			walls[1][7][i] = true;
		}
		return walls;
	}

	public String toString() {
		String[] lines = new String[8];
		lines[0] = " ";
		for (int i = 0; i < 7; i++) {
			lines[0] = lines[0] + (walls[0][0][i] ? "_" : " ") + " ";
		}
		for (int i = 1; i < 8; i++) {
			lines[i] = "";
			for (int j = 0; j < 8; j++) {
				lines[i] = lines[i] + (walls[1][j][i - 1] ? "|" : " ")
						+ (walls[0][i][j] ? "_" : " ");
			}
		}
		String toReturn = "";
		for (int i = 0; i < 8; i++) {
			toReturn = toReturn + lines[i] + "\n";
		}
		return toReturn;
	}

	public static void main(String[] args) {
		Maze maze = new Maze(40);
		System.out.print(maze);
	}
}



