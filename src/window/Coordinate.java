package window;

public final class Coordinate
{
	public final int x;
	public final int y;
	
	public Coordinate(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Object o)
	{
		if(!(o instanceof Coordinate))
		{
			return false;
		}
		Coordinate other = (Coordinate)o;
		return this.x == other.x && this.y == other.y;
	}
}
