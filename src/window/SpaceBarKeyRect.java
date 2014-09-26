package window;

public class SpaceBarKeyRect extends KeyRect
{	
	public SpaceBarKeyRect(int leftX, int topY)
	{
		super(leftX, topY, ' ');
		super.width = 7*KeyRect.WIDTH + 6*KeyRect.BORDER_ZONE;	//width is different, it's longer
		super.disp = "SPACE";
	}
}
