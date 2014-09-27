package window;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

public class Keyboard extends JPanel implements MouseInputListener
{
	private static final int ROW_ONE_LOC = 15;
	private static final int ROW_TWO_LOC = ROW_ONE_LOC + KeyRect.HEIGHT + KeyRect.BORDER_ZONE;
	private static final int ROW_THREE_LOC = ROW_TWO_LOC + KeyRect.HEIGHT + KeyRect.BORDER_ZONE;
	private static final int ROW_FOUR_LOC = ROW_THREE_LOC + KeyRect.HEIGHT + KeyRect.BORDER_ZONE;
	private static final int TAIL_LENGTH = 100;
	
	private static final int ROW_ONE_COL_ONE_LOC = 10;
	private static final int ROW_TWO_COL_ONE_LOC = 20;
	private static final int ROW_THREE_COL_ONE_LOC = 40;
	private static final int ROW_FOUR_COL_ONE_LOC = 40;
	
	private static final char CHARACTER_KEY_ARRAY[] = {'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M',' '};
	
	private static final int NUM_KEYS_ROW_ONE = 10;
	private static final int NUM_KEYS_ROW_TWO = 9;
	private static final int NUM_KEYS_ROW_THREE = 7;
	private static final int NUM_KEYS_ROW_FOUR = CHARACTER_KEY_ARRAY.length - NUM_KEYS_ROW_ONE - NUM_KEYS_ROW_TWO - NUM_KEYS_ROW_THREE;
	
	private static final int PERIOD = 20;
	
	private static final double BIG_THRESHOLD = 120.0;
	private static final double SMALL_THRESHOLD = 10.0;
	
//	private static final int xs[] = {10,35,60,85,110,135,160,185,210,235,20,45,70,95,120,145,170,195,220,40,65,90,115,140,165,190,40};
//	private char strlst[] = new char[100];
//	private String comboString;
	 
//	private final ArrayList<Integer> mouseCoordX;	//All X coords
//	private final ArrayList<Integer> mouseCoordY;	//All Y coords
	private final ArrayList<Coordinate> mouseCoords;
	private final ArrayList<Character> mouseChars;
//	private final ArrayList<Double> listOfAngles;	//All angles, 0 --> 0, 1, 2
	
	private final Timer timer;
	private final TimerTask mouseCheck;
	
	private final KeyRect keyRectArray[];
//	private int pos = 0;
	
//	private volatile int currMouseX,currMouseY;
	private volatile Coordinate currentPos;
//	private int ox,oy;
	
	protected volatile boolean mouseDragged;

	public Keyboard()
	{
		addMouseListener(this);
		addMouseMotionListener(this);
		
		this.currentPos = null;
		
//		this.mouseCoordX = new ArrayList<Integer>();
//		this.mouseCoordY = new ArrayList<Integer>();
		this.mouseCoords = new ArrayList<Coordinate>();
		this.mouseChars = new ArrayList<Character>();
//		this.listOfAngles = new ArrayList<Double>();
		
		this.keyRectArray = new KeyRect[CHARACTER_KEY_ARRAY.length];
		
		for(int i = 0; i < this.keyRectArray.length; i++)
		{
			if(i < NUM_KEYS_ROW_ONE)
			{
				this.keyRectArray[i] = new KeyRect(ROW_ONE_COL_ONE_LOC+KeyRect.WIDTH*i+KeyRect.BORDER_ZONE*i, ROW_ONE_LOC, CHARACTER_KEY_ARRAY[i]);
			}
			else if(i < NUM_KEYS_ROW_TWO+NUM_KEYS_ROW_ONE)
			{
				this.keyRectArray[i] = new KeyRect(ROW_TWO_COL_ONE_LOC+KeyRect.WIDTH*(i-10)+KeyRect.BORDER_ZONE*(i-10), ROW_TWO_LOC, CHARACTER_KEY_ARRAY[i]);
			}
			else
			{
				this.keyRectArray[i] = new KeyRect(ROW_THREE_COL_ONE_LOC+KeyRect.WIDTH*(i-19)+KeyRect.BORDER_ZONE*(i-19), ROW_THREE_LOC, CHARACTER_KEY_ARRAY[i]);
			}
			this.add(this.keyRectArray[i]);
		}
		this.keyRectArray[this.keyRectArray.length-1] = new SpaceBarKeyRect(ROW_FOUR_COL_ONE_LOC, ROW_FOUR_LOC);
		
		this.timer = new Timer();
		this.mouseCheck = new MouseCheck(this);
		this.timer.schedule(this.mouseCheck, PERIOD, PERIOD);
		
//		this.comboString = "";
		
		this.mouseDragged = false;
	}
	
	private String generateFormattedString()
	{
		List<Character> chars = null;
		synchronized(this.mouseChars)
		{
			chars = new ArrayList<Character>(this.mouseChars);
		}
		
		//CALCULATE ANGLES
//		if(chars.size() <= 2)
//		{
//			if(chars.isEmpty())
//			{
//				return "";
//			}
//			else if (chars.size() == 1 || chars.get(0) == chars.get(1))
//			{
//				return String.valueOf(chars.get(0));
//			}
//			return String.valueOf(chars.get(0)) + String.valueOf(chars.get(1));
//		}
		
		final double[] listOfAngles = new double[chars.size()];
		listOfAngles[0] = Double.NaN;
		for(int i = 1; i < listOfAngles.length-2; i++)
		{
			Coordinate after = this.mouseCoords.get(i+1);
			Coordinate middle = this.mouseCoords.get(i);
			Coordinate before = this.mouseCoords.get(i-1);
			//FIXME need to change how to compute this since the x/y coordinates are different.
			double angle1 = Math.atan2(((double)after.y - middle.y), ((double)after.x - middle.x));
			double angle2 = Math.atan2(((double)middle.y - before.y), ((double)middle.x - before.x));
//			double angle1 = 0.0;
//			double angle2 = 0.0;
//			if(after.x - middle.x == 0)
//			{
//				if(after.y - middle.y > 0)
//				{
//					angle1 = Math.PI/2;
//				}
//				else if(after.y - middle.y < 0)
//				{
//					angle1 = -Math.PI/2;
//				}
//			}
//			else
//			{
//				angle1 = Math.atan((after.y-middle.y)/(after.x-middle.x));
//			}
//			
//			if(middle.x - before.x == 0)
//			{
//				if(middle.y - before.y > 0)
//				{
//					angle2 = Math.PI/2;
//				}
//				else if(middle.y - before.y < 0)
//				{
//					angle2 = -Math.PI/2;
//				}
//			}
//			else
//			{
//				angle2 = Math.atan((middle.y-before.y)/(middle.x-before.x));
//			}
//			
////			double angle1 = Math.atan((after.y-middle.y)/(after.x-middle.x));
////			double angle2 = Math.atan((middle.y-before.y)/(middle.x-before.x));
			System.out.println("(" + middle.x + "," + middle.y + ") <=> (" + after.x + "," + after.y + ") ==> " + angle1);
			double realAngle = angle1 - angle2;
			listOfAngles[i] = Math.toDegrees(realAngle);
			
//			double angle1 = 
//			
//			listOfAngles[i]
//			System.out.println("The total is "+Math.toDegrees(total));
		}
		listOfAngles[listOfAngles.length-1] = Double.NaN;
		
		//FIXME DO SOMETHING
		
		StringBuilder sb = new StringBuilder(String.valueOf('_'));
//		for(int i = 0; i < chars.size(); i++)
//		{
//			sb.append(chars.get(i));
//		}
		
		boolean isCorner = false;
		for(int i = 0; i < listOfAngles.length; i++)
		{
			if(!isCorner && sb.charAt(sb.length()-1) == chars.get(i).charValue())
			{
				if(listOfAngles[i] != 0.0 && (listOfAngles[i] < BIG_THRESHOLD && listOfAngles[i] > SMALL_THRESHOLD) ||  (listOfAngles[i] > -BIG_THRESHOLD && listOfAngles[i] < -SMALL_THRESHOLD))
				{
					System.out.println(listOfAngles[i]);
					sb.append("_" + String.valueOf(chars.get(i)));
					isCorner = true;
				}
				continue;
			}
//			else if(!isCorner)
//			{
			else if(!isCorner && listOfAngles[i] != 0.0 && (listOfAngles[i] < BIG_THRESHOLD && listOfAngles[i] > SMALL_THRESHOLD) ||  (listOfAngles[i] > -BIG_THRESHOLD && listOfAngles[i] < -SMALL_THRESHOLD))
			{
				System.out.println(listOfAngles[i]);
				sb.append(String.valueOf(chars.get(i)) + "_" + String.valueOf(chars.get(i)));
				isCorner = true;
				continue;
			}
//			}
//			else if(sb.charAt(sb.length()-1) == chars.get(i).charValue())
//			{
//				continue;
//			}
			sb.append(String.valueOf(chars.get(i)));
			
			isCorner = false;
		}
		sb.append(String.valueOf('_'));
		return sb.toString();
	}
	
	protected void addMouseCoordinates()
	{
//		int currX = -1;
//		int currY = -1;
		Coordinate currMousePosition = null;
		boolean isDragged = false;
		
		synchronized(this)
		{
//			currX = this.currMouseX;
//			currY = this.currMouseY;
			currMousePosition = this.currentPos;
			isDragged = this.mouseDragged;
		}
		
		Coordinate lastCoor = this.mouseCoords.get(this.mouseCoords.size()-1);
		if((this.mouseCoords.isEmpty() && isDragged) || (Math.abs(lastCoor.y-currMousePosition.y) > 4 && Math.abs(lastCoor.x - currMousePosition.x) > 4))
		{/*!this.mouseCoords.get(this.mouseCoords.size()-1).equals(currMousePosition)*/
//			if(this.mouseCoordX.isEmpty())
//			{
//				System.out.println("(" + currX + "," + currY + ")");
//			}
			
//			boolean found = false;
			for(int i = 0; i < this.keyRectArray.length; i++)
			{
				if(this.keyRectArray[i].isInside(currMousePosition))
				{
					synchronized(this)
					{
						this.mouseChars.add(this.keyRectArray[i].getKeyChar());
//						this.mouseCoordX.add(currX);
//						this.mouseCoordY.add(currY);
						this.mouseCoords.add(currMousePosition);
					}
//					found = true;
					break;
				}
			}
//			if(!found)
//			{
//				synchronized(this)
//				{
//					this.mouseChars.add('\0');
//					this.mouseCoordX.add(currX);
//					this.mouseCoordY.add(currY);
//				}
//			}
		}
		
		repaint();
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for(int i=0; i<this.keyRectArray.length; i++)
		{
			keyRectArray[i].paintComponent(g);
		}
		
//		ArrayList<Integer> coordX = null;
//		ArrayList<Integer> coordY = null;
		ArrayList<Coordinate> mouseCoordinatesPath = null;
		synchronized(this)
		{
//			coordX = new ArrayList<Integer>(this.mouseCoordX);
//			coordY = new ArrayList<Integer>(this.mouseCoordY);
			mouseCoordinatesPath = new ArrayList<Coordinate>(this.mouseCoords);
		}
		
		g.setColor(Color.BLUE);
		if(mouseCoordinatesPath.size() > 1)
		{
			for(int i = mouseCoordinatesPath.size()-1; i > mouseCoordinatesPath.size() - TAIL_LENGTH && i > 0; i--)
			{
//				System.out.println(i);
				Coordinate before = mouseCoordinatesPath.get(i-1);
				Coordinate now = mouseCoordinatesPath.get(i);
				g.drawLine(before.x,before.y,now.x,now.y);
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		synchronized(this)
		{
//			this.currMouseX = e.getX();
//			this.currMouseY = e.getY();
			this.currentPos = new Coordinate(e.getX(), e.getY());
		}
		
		for(int i = 0; i < this.keyRectArray.length; i++)
		{
			if(this.keyRectArray[i].isInside(e.getX(), e.getY()))
			{
				this.keyRectArray[i].isInDragged = true;
				this.keyRectArray[i].isIn = true;
//				if(this.comboString.isEmpty() || this.comboString.charAt(this.comboString.length()-1) != this.keyRectArray[i].getKeyChar())
//				{
//					this.comboString += this.keyRectArray[i].getKeyChar();
//				}
//				this.mouseChars.add(this.keyRectArray[i].getKeyChar());
			}
			else
			{
				this.keyRectArray[i].isInDragged = false;
				this.keyRectArray[i].isIn = false;
			}
		}
		
		repaint();
		e.consume();
	}
	

	@Override
	public void mouseMoved(MouseEvent e)
	{
		synchronized(this)
		{
//			this.currMouseX = e.getX();
//			this.currMouseY = e.getY();
			this.currentPos = new Coordinate(e.getX(), e.getY());
		}
		
		for(int i = 0; i < this.keyRectArray.length; i++)
		{
			this.keyRectArray[i].isInDragged = false;
			if(this.keyRectArray[i].isInside(e.getX(), e.getY()))
			{
				this.keyRectArray[i].isIn = true;
			}
			else
			{
				this.keyRectArray[i].isIn = false;
			}
		}

		repaint();
		e.consume();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		//empty
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		for(int i = 0; i < this.keyRectArray.length; i++)
		{
			if(this.keyRectArray[i].isInside(e.getX(), e.getY()))
			{
				this.keyRectArray[i].isInDragged = true;
				this.keyRectArray[i].isIn = true;
//				if(this.comboString.isEmpty() || this.comboString.charAt(this.comboString.length()-1) != this.keyRectArray[i].getKeyChar())
//				{
//					this.comboString += this.keyRectArray[i].getKeyChar();
//				}
//				this.listOfAngles.add(Double.NaN);
				synchronized(this)
				{
					this.mouseChars.add(this.keyRectArray[i].getKeyChar());
//					this.mouseCoordX.add(e.getX());
//					this.mouseCoordY.add(e.getY());
					this.mouseCoords.add(new Coordinate(e.getX(), e.getY()));
				}
				break;
			}
//			else
//			{
//				this.keyRectArray[i].isInDragged = false;
//				this.keyRectArray[i].isIn = false;
//			}
		}
		synchronized(this)
		{
			this.mouseDragged = true;
		}
		
		repaint();
		e.consume();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		for(int i = 0; i < this.keyRectArray.length; i++)
		{
			this.keyRectArray[i].isInDragged = false;
			if(this.keyRectArray[i].isInside(e.getX(), e.getY()))
			{
				this.keyRectArray[i].isIn = true;
//				if(this.comboString.isEmpty() || this.comboString.charAt(this.comboString.length()-1) != this.keyRectArray[i].getKeyChar())
//				{
//					this.comboString += this.keyRectArray[i].getKeyChar();
//				}
				synchronized(this)
				{
					this.mouseChars.add(this.keyRectArray[i].getKeyChar());
//					this.mouseCoordX.add(e.getX());
//					this.mouseCoordY.add(e.getY());
					this.mouseCoords.add(new Coordinate(e.getX(), e.getY()));
				}
				
//				//add second to last angle
//				double temp1 = Math.atan2((this.mouseCoordY.get(this.mouseCoordY.size()-1) - this.mouseCoordY.get(this.mouseCoordY.size()-2)), (this.mouseCoordX.get(this.mouseCoordX.size()-1) - this.mouseCoordX.get(this.mouseCoordX.size()-2)));
//				double temp2 = Math.atan2((this.mouseCoordY.get(this.mouseCoordY.size()-2) - this.mouseCoordY.get(this.mouseCoordY.size()-3)), (this.mouseCoordX.get(this.mouseCoordX.size()-2) - this.mouseCoordX.get(this.mouseCoordX.size()-3)));
//				double total = temp1 - temp2;
//				this.listOfAngles.add(Math.toDegrees(total));
//				
//				//add last angle
//				this.listOfAngles.add(Double.NaN);
				
//				System.out.println(this.comboString);
				System.out.println(this.generateFormattedString());
			}
			else
			{
				this.keyRectArray[i].isIn = false;
			}
		}
		
//		this.mouseCoordX.clear();
//		this.mouseCoordY.clear();
		this.mouseCoords.clear();
//		this.listOfAngles.clear();
		this.mouseChars.clear();
		
//		this.comboString = "";
		
		synchronized(this)
		{
			this.mouseDragged = false;
		}
		
		repaint();
		e.consume();
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
//		this.requestFocusInWindow(); //MAYBE?
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		//empty
	}
	
	public static void main(String args[])
	{
		Keyboard object = new Keyboard();
		
		JFrame frame = new JFrame("Test");
		frame.add(object);
		frame.setVisible(true);
		frame.setSize(800,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}
}

//class 

class MouseCheck extends TimerTask
{
	private final Keyboard k;
	
	public MouseCheck(Keyboard k)
	{
		super();
		this.k = k;
	}
	
	@Override
	public void run()
	{
		boolean isDragged = false;
		
		synchronized(this.k)
		{
			isDragged = this.k.mouseDragged;
		}
		
		if(isDragged)
		{
			this.k.addMouseCoordinates();
		}
	}
}