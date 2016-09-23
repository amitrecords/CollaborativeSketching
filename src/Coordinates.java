import java.awt.Color;




public class Coordinates {

	
	private  int x;
	private  int y;
	private Color color;
	private String timest;
	
	public Coordinates(int x,int y)
	{
		this.x = x;
		this.y = y;
	}

	public Coordinates() {
		
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void setColor(Color c) {
		this.color = c;
	}

	public Color getColor() {
		return color;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setTime(String string) {
		this.timest= string;
		
	}

	public String getTime() {
		
		return timest;
	}
	
	
	
}
