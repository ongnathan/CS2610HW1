package window;

public final class Coordinate
{
	public final int x;
	public final int y;
	
	private static final int TOO_CLOSE_THRESHOLD = 10;
	
	public Coordinate(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public static double getAngle(Coordinate before, Coordinate middle, Coordinate after)
	{
		double angle1 = Math.atan2(((double)after.y - middle.y), ((double)after.x - middle.x));
		double angle2 = Math.atan2(((double)before.y - middle.y), ((double)before.x - middle.x));
//		double angle1 = 0.0;
//		double angle2 = 0.0;
//		if(after.x - middle.x == 0)
//		{
//			if(after.y - middle.y > 0)
//			{
//				angle1 = Math.PI/2;
//			}
//			else if(after.y - middle.y < 0)
//			{
//				angle1 = -Math.PI/2;
//			}
//		}
//		else
//		{
//			angle1 = Math.atan((after.y-middle.y)/(after.x-middle.x));
//		}
//		
//		if(middle.x - before.x == 0)
//		{
//			if(middle.y - before.y > 0)
//			{
//				angle2 = Math.PI/2;
//			}
//			else if(middle.y - before.y < 0)
//			{
//				angle2 = -Math.PI/2;
//			}
//		}
//		else
//		{
//			angle2 = Math.atan((middle.y-before.y)/(middle.x-before.x));
//		}
//		
////		double angle1 = Math.atan((after.y-middle.y)/(after.x-middle.x));
////		double angle2 = Math.atan((middle.y-before.y)/(middle.x-before.x));
		System.out.println("(" + middle.x + "," + middle.y + ") => (" + after.x + "," + after.y + ") ==> " + Math.toDegrees(angle1));
		System.out.println("(" + middle.x + "," + middle.y + ") => (" + before.x + "," + before.y + ") ==> " + Math.toDegrees(angle2));
		double realAngle = angle1 - angle2;
		System.out.println("Total angle = " + Math.toDegrees(realAngle));
		System.out.println("----------");
		return Math.toDegrees(realAngle);
	}
	
	public static boolean tooClose(Coordinate c1, Coordinate c2)
	{
		return Math.abs(c2.y - c1.y) <= TOO_CLOSE_THRESHOLD && Math.abs(c2.x - c1.x) <= TOO_CLOSE_THRESHOLD;
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
