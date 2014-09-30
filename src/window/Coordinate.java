package window;

/**
 * Representing a coordinate.
 * @author Nathan Ong and Jose Michael Joseph.
 */
public final class Coordinate
{
	/**
	 * The X value.
	 */
	public final int x;
	
	/**
	 * The Y value.
	 */
	public final int y;
	
	private static final int TOO_CLOSE_THRESHOLD = 5;	//The Manhattan Distance radius for which a point is considered too close to another. 
	
	/**
	 * The constructor.  Takes an X and Y point.
	 * @param x The X coordinate.
	 * @param y The Y coordinate.
	 */
	public Coordinate(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Calculates the smallest angle, where the middle is the vertex, and the before and after coordinates determine the lines.
	 * @param before The before Coordinate.
	 * @param middle The middle Coordinate.
	 * @param after The after Coordinate.
	 * @return Returns the angle, in degrees, of the two lines.
	 */
	public static double getAngle(Coordinate before, Coordinate middle, Coordinate after)
	{
		double angle1 = Math.toDegrees(Math.atan2(((double)after.y - middle.y), ((double)after.x - middle.x)));
		double angle2 = Math.toDegrees(Math.atan2(((double)before.y - middle.y), ((double)before.x - middle.x)));
		double realAngle = angle1 - angle2;
		
		//Make sure the angles are within -180 -- 180
		if(realAngle > 180.0)
		{
			realAngle -= 360.0;
		}
		else if(realAngle < -180.0)
		{
			realAngle += 360.0;
		}
		
		return realAngle;
	}
	
	/**
	 * Calculates whether or not two Coordinates are too close to each other to be considered for computing the line.
	 * The distance is based on Manhattan Distance and is dependent on the {@link Coordinate#TOO_CLOSE_THRESHOLD} field.
	 * The distance is currently set at 5.
	 * @param c1 The first Coordinate.
	 * @param c2 The second Coordinate.
	 * @return Returns whether or not the two Coordinates are too close together.  Returns false if the Coordinates are far away enough.
	 */
	public static boolean tooClose(Coordinate c1, Coordinate c2)
	{
		return Math.abs(c2.y - c1.y) <= TOO_CLOSE_THRESHOLD && Math.abs(c2.x - c1.x) <= TOO_CLOSE_THRESHOLD;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object o)
	{
		if(!(o instanceof Coordinate))
		{
			return false;
		}
		Coordinate other = (Coordinate)o;
		return this.x == other.x && this.y == other.y;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
		return "(" + this.x + "," + this.y + ")";
	}
}//end class
