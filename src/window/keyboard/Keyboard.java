package window.keyboard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import window.Coordinate;
import window.MainWindow;
import backend.dictionary.Dictionary;
import backend.stringDist.LevenshteinDistance;

public class Keyboard extends JPanel implements MouseInputListener
{
	private static final int ROW_ONE_LOC = 50;
	private static final int ROW_TWO_LOC = ROW_ONE_LOC + KeyRect.HEIGHT + KeyRect.BORDER_ZONE;
	private static final int ROW_THREE_LOC = ROW_TWO_LOC + KeyRect.HEIGHT + KeyRect.BORDER_ZONE;
	private static final int ROW_FOUR_LOC = ROW_THREE_LOC + KeyRect.HEIGHT + KeyRect.BORDER_ZONE;
	private static final int TAIL_LENGTH = 100;
	
	private static final int ROW_ONE_COL_ONE_LOC = 50;
	private static final int ROW_TWO_COL_ONE_LOC = ROW_ONE_COL_ONE_LOC+10;
	private static final int ROW_THREE_COL_ONE_LOC = ROW_TWO_COL_ONE_LOC+20;
	private static final int ROW_FOUR_COL_ONE_LOC = ROW_THREE_COL_ONE_LOC;
	
	private static final char CHARACTER_KEY_ARRAY[] = {'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M',' '};
	
	private static final int NUM_KEYS_ROW_ONE = 10;
	private static final int NUM_KEYS_ROW_TWO = 9;
//	private static final int NUM_KEYS_ROW_THREE = 7;
//	private static final int NUM_KEYS_ROW_FOUR = CHARACTER_KEY_ARRAY.length - NUM_KEYS_ROW_ONE - NUM_KEYS_ROW_TWO - NUM_KEYS_ROW_THREE;
	
	private static final int PERIOD = 10;
	
	private static final double BIG_THRESHOLD = 150.0;
//	private static final double SMALL_THRESHOLD = 10.0;
	
	private final ArrayList<Coordinate> mouseCoords;
	private final ArrayList<Character> mouseChars;
	
	private final Timer timer;
	private final TimerTask mouseCheck;
	
	private final KeyRect keyRectArray[];
	
	private volatile Coordinate currentPos;
	
	protected volatile boolean mouseDragged;
	
	private final MainWindow parent;
	
	private String wholeInput;
	private String wholeOutput;

	public Keyboard(MainWindow parent)
	{
		addMouseListener(this);
		addMouseMotionListener(this);
		
		this.currentPos = new Coordinate(0,0);
		
		this.mouseCoords = new ArrayList<Coordinate>();
		this.mouseChars = new ArrayList<Character>();
		
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
		
		this.mouseDragged = false;
		
		this.setPreferredSize(new Dimension(ROW_ONE_COL_ONE_LOC+NUM_KEYS_ROW_ONE*KeyRect.WIDTH+NUM_KEYS_ROW_ONE*KeyRect.BORDER_ZONE, 4*KeyRect.HEIGHT+4*KeyRect.BORDER_ZONE));
		
		this.parent = parent;
		
		this.wholeInput = "";
		this.wholeOutput = "";
	}
	
	//TODO HANDLE SPACE
	private String[] generateFormattedString()
	{
		List<Character> chars = new ArrayList<Character>(this.mouseChars);
		
		StringBuilder sb = new StringBuilder("" + Dictionary.DELIMITER + String.valueOf(chars.get(0)));
		
		//retrieve three valid coordinates to check for angles
		int i = 0;
		Coordinate prevCoord = this.mouseCoords.get(0);
		Coordinate thisCoord = this.mouseCoords.get(1);
		for(i = 2; i < chars.size()-1 && Coordinate.tooClose(prevCoord, thisCoord); i++)
		{
			thisCoord = this.mouseCoords.get(i);
		}
		if(i >= chars.size()-1)
		{
			if(sb.charAt(sb.length()-1) != chars.get(chars.size()-1))
			{
				sb.append(String.valueOf(chars.get(chars.size()-1)));
			}
			sb.append(Dictionary.DELIMITER);
			this.wholeInput = sb.toString();
			return splitFormattedString(this.wholeInput);
		}
		
		boolean isCorner = true;
		char thisChar = chars.get(i);
		out: for(i = i+1; i < chars.size()-1; i++)
		{
//			char thisChar = chars.get(i);
			Coordinate nextCoord = this.mouseCoords.get(i);
			while(Coordinate.tooClose(thisCoord, nextCoord))
			{
				i++;
				if(i >= chars.size() - 1)
				{
					break out;
				}
				nextCoord = this.mouseCoords.get(i);
			}
			
			char nextChar = chars.get(i);
			
			if(isCorner && sb.charAt(sb.length()-1) == thisChar)
			{
				prevCoord = thisCoord;
				thisCoord = nextCoord;
				thisChar = nextChar;
				continue;
			}
			
			isCorner = false;
			
			//CHECK CORNER
//			if(!isCorner)
//			{
				double angle = Coordinate.getAngle(prevCoord, thisCoord, nextCoord);
//				if((angle < BIG_THRESHOLD && angle > SMALL_THRESHOLD) || (angle > -BIG_THRESHOLD && angle < -SMALL_THRESHOLD))
				if(angle < BIG_THRESHOLD && angle > -BIG_THRESHOLD)
				{
//					System.out.println(thisChar);
					if(sb.charAt(sb.length()-2) != Dictionary.DELIMITER)
					{
						if(sb.charAt(sb.length()-1) != thisChar)
						{
							sb.append("" + thisChar);
						}
						sb.append("" + Dictionary.DELIMITER + thisChar);
					}
//					System.out.println(sb.toString());
					prevCoord = thisCoord;
					thisCoord = nextCoord;
					thisChar = nextChar;
					isCorner = true;
					continue;
				}
//			}
			
			//CHECK SAME CHARACTER
			if(sb.charAt(sb.length()-1) != thisChar)
			{
				sb.append(String.valueOf(thisChar));
//				isCorner = true;
			}
			
			prevCoord = thisCoord;
			thisCoord = nextCoord;
			thisChar = nextChar;
		}
		
		if(sb.charAt(sb.length()-1) != chars.get(chars.size()-1))
		{
			sb.append(String.valueOf(chars.get(chars.size()-1)));
		}
		sb.append(""+Dictionary.DELIMITER);
		if(sb.length() > 3 && sb.charAt(sb.length()-2) == sb.charAt(sb.length()-4) && sb.charAt(sb.length()-3) == Dictionary.DELIMITER)
		{
			this.wholeInput = sb.substring(0, sb.length()-2);
//			return splitFormattedString(sb.substring(0, sb.length()-2));
		}
		else
		{
			this.wholeInput = sb.toString();
		}
		return splitFormattedString(this.wholeInput);
	}
	
	//TODO FIXME
	//TODO handle space not being on a corner (do a questionable check on the ends and beginnings)
	private static String[] splitFormattedString(String formatted)
	{
//		int numDelimitersBefore = 0;
//		int delimiterBefore = 0;
//		int space = 0;
		String[] split = formatted.split(" ");
		if(split.length == 1)
		{
			return new String[]{formatted};
		}
		
		ArrayList<String> newFormattedStrings = new ArrayList<String>();
//		boolean skip = false;
//		boolean skipTwo = false;
		String combined = "";
		for(int i = 0; i < formatted.length(); i++)
		{
//			if(skip)
//			{
//				if(formatted.charAt(i) == Dictionary.DELIMITER)
//				{
//					if(skipTwo)
//					{
//						skipTwo = false;
//					}
//					else
//					{
//						skip = false;
//					}
//				}
//			}
//			else if(formatted.charAt(i) == Dictionary.DELIMITER)
//			{
//				combined += "" + formatted.charAt(i);
//			}
			if(formatted.charAt(i) == ' ')
			{
//				skip = true;
//				if(i < formatted.length()-2)
//				{
//					skipTwo = i < formatted.length()-2 && formatted.charAt(i+1) == Dictionary.DELIMITER && formatted.charAt(i+2) == ' ';
//				}
				
				String[] delimiterSplit = combined.split("" + Dictionary.DELIMITER);
				if(delimiterSplit.length == 0)
				{
					combined = "";
					continue;
				}
				
				String[] newDelimiterSplit = new String[delimiterSplit.length-2];
				
				if(newDelimiterSplit.length == 0)
				{
					newFormattedStrings.add("" + Dictionary.DELIMITER + combined.charAt(1) + Dictionary.DELIMITER);
				}
				else
				{
//					System.arraycopy(delimiterSplit, 1, newDelimiterSplit, 0, newDelimiterSplit.length);
					
//					String newFormattedString = "" + Dictionary.DELIMITER;
//					for(int j = 0; j < delimiterSplit.length; j++)
//					{
//						newFormattedString += delimiterSplit[j] + Dictionary.DELIMITER;
//					}
//					newFormattedStrings.add(newFormattedString);
					newFormattedStrings.add(combined);
				}
				
				combined = "";
			}
			else
			{
				combined += "" + formatted.charAt(i);
			}
		}
		
		if(/*!skip && */!combined.isEmpty())
		{
			newFormattedStrings.add(combined);
		}
		
		return newFormattedStrings.toArray(new String[0]);
	}
	
	protected void addMouseCoordinates()
	{
		Coordinate currMousePosition = this.currentPos;
		boolean isDragged = this.mouseDragged;
		
		if((this.mouseCoords.isEmpty() && isDragged) || !this.mouseCoords.get(this.mouseCoords.size()-1).equals(currMousePosition))
		{
			for(int i = 0; i < this.keyRectArray.length; i++)
			{
				if(this.keyRectArray[i].isInside(currMousePosition))
				{
					synchronized(this)
					{
						this.mouseChars.add(this.keyRectArray[i].getKeyChar());
						this.mouseCoords.add(currMousePosition);
					}
					break;
				}
			}
		}
		
//		repaint();
	}
	
//	public Coordinate getTopLeftOfKey(char c)
//	{
//		for(int i = 0; i < this.keyRectArray.length; i++)
//		{
//			if(keyRectArray[i].getKeyChar() == c)
//			{
//				return keyRectArray[i].getCoordinate();
//			}
//		}
//		return null;
//	}
	
	public void simulateKeyPress(char c)
	{
		for(int i = 0; i < this.keyRectArray.length; i++)
		{
			if(keyRectArray[i].getKeyChar() == c)
			{
				keyRectArray[i].isTyped = true;
				continue;
			}
			keyRectArray[i].isTyped = false;
		}
		repaint();
	}
	
//	public void simulateKeyRelease(char c)
//	{
//		for(int i = 0; i < this.keyRectArray.length; i++)
//		{
//			keyRectArray[i].isTyped = false;
//		}
//	}
	
	public String[] getLevenshteinSet()
	{
		return new String[]{LevenshteinDistance.getRealInputFromCombo(this.wholeInput), this.wholeOutput, LevenshteinDistance.levenshteinDistance(this.wholeInput, this.wholeOutput)};
	}
	
	public String getLastInputLevenshteinDistance()
	{
		return LevenshteinDistance.levenshteinDistance(this.wholeInput, this.wholeOutput);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for(int i=0; i<this.keyRectArray.length; i++)
		{
			keyRectArray[i].paintComponent(g);
		}
		
		ArrayList<Coordinate> mouseCoordinatesPath = new ArrayList<Coordinate>(this.mouseCoords);
		
		g.setColor(Color.BLUE);
		if(mouseCoordinatesPath.size() > 1)
		{
			for(int i = mouseCoordinatesPath.size()-1; i > mouseCoordinatesPath.size() - TAIL_LENGTH && i > 0; i--)
			{
				Coordinate before = mouseCoordinatesPath.get(i-1);
				Coordinate now = mouseCoordinatesPath.get(i);
				g.drawLine(before.x,before.y,now.x,now.y);
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		synchronized(this.currentPos)
		{
			this.currentPos = new Coordinate(e.getX(), e.getY());
		}
		
		for(int i = 0; i < this.keyRectArray.length; i++)
		{
			if(this.keyRectArray[i].isInside(e.getX(), e.getY()))
			{
				this.keyRectArray[i].isInDragged = true;
				this.keyRectArray[i].isIn = true;
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
		synchronized(this.currentPos)
		{
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
				synchronized(this)
				{
					this.mouseChars.add(this.keyRectArray[i].getKeyChar());
					this.mouseCoords.add(new Coordinate(e.getX(), e.getY()));
				}
			}
			this.keyRectArray[i].isTyped = false;
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
				synchronized(this)
				{
					this.mouseChars.add(this.keyRectArray[i].getKeyChar());
					this.mouseCoords.add(new Coordinate(e.getX(), e.getY()));
				}
				//TODO do something with the generated formatted string
				this.wholeOutput = this.parent.getWordFromSwipes(this.generateFormattedString());
//				System.out.println(this.wholeInput);
//				System.out.println(this.wholeOutput);
//				System.out.println(LevenshteinDistance.levenshteinDistance(this.wholeInput, this.wholeOutput));
//				String[] formattedStrings = this.generateFormattedString();
//				for(String formattedString : formattedStrings)
//				{
//					System.out.println(formattedString);
//					this.parent.getWordFromSwipes(formattedString);
//				}
			}
			else
			{
				this.keyRectArray[i].isIn = false;
			}
		}
		
		synchronized(this)
		{
			this.mouseCoords.clear();
			this.mouseChars.clear();
			this.mouseDragged = false;
		}
		
		repaint();
		e.consume();
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		this.requestFocusInWindow(); //MAYBE?
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		repaint();
		//empty
	}
	
//	public static void main(String args[])
//	{
//		Keyboard object = new Keyboard();
//		
//		JFrame frame = new JFrame("Test");
//		frame.add(object);
//		frame.setVisible(true);
//		frame.setSize(450,300);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
//	}
}

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