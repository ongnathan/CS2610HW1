package window.keyboard;

import window.Coordinate;

/**
 * The KeyRect that represents the space bar.
 * Needs its own class since it has a different size than other keys.
 * @author Nathan Ong and Jose Michael Joseph
 */
public class SpaceBarKeyRect extends KeyRect
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8502915013043747722L;

	/**
	 * The constructor that takes in the X and Y coordinates of the top left corner.
	 * @param leftX The X coordinate of the left side.
	 * @param topY The Y coordinate of the top side.
	 */
	public SpaceBarKeyRect(int leftX, int topY)
	{
		this(new Coordinate(leftX, topY));
	}
	
	/**
	 * The constructor that takes in a Coordinate of the top left corner.
	 * @param c The Coordinate that represents the top left corner.
	 */
	public SpaceBarKeyRect(Coordinate c)
	{
		super(c, ' ');
		super.width = 7*KeyRect.WIDTH + 6*KeyRect.BORDER_ZONE;	//width is different, it's longer
		super.disp = "SPACE";									//display String is also different
	}
}//end class
