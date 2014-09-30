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
	/**
	 * 
	 */
	private static final long serialVersionUID = -3423600453351522338L;
	
	private static final int ROW_ONE_LOC = 50;														//The first row's relative Y location
	private static final int ROW_TWO_LOC = ROW_ONE_LOC + KeyRect.HEIGHT + KeyRect.BORDER_ZONE;		//The second row's relative Y location
	private static final int ROW_THREE_LOC = ROW_TWO_LOC + KeyRect.HEIGHT + KeyRect.BORDER_ZONE;	//The third row's relative Y location
	private static final int ROW_FOUR_LOC = ROW_THREE_LOC + KeyRect.HEIGHT + KeyRect.BORDER_ZONE;	//The fourth row's relative Y location
	private static final int TAIL_LENGTH = 150;														//The length of the drag tail.
	
	private static final int ROW_ONE_COL_ONE_LOC = 50;												//The first row's relative X start location
	private static final int ROW_TWO_COL_ONE_LOC = ROW_ONE_COL_ONE_LOC+10;							//The second row's relative X start location
	private static final int ROW_THREE_COL_ONE_LOC = ROW_TWO_COL_ONE_LOC+20;						//The third row's relative X start location
	private static final int ROW_FOUR_COL_ONE_LOC = ROW_THREE_COL_ONE_LOC;							//The fourth row's relative X start location
	
	//An array of all valid characters
	private static final char CHARACTER_KEY_ARRAY[] = {'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M',' '};
	
	private static final int NUM_KEYS_ROW_ONE = 10;													//The number of keys on the first row
	private static final int NUM_KEYS_ROW_TWO = 9;													//The number of keys on the second row
//	private static final int NUM_KEYS_ROW_THREE = 7;												//The number of keys on the third row
//	private static final int NUM_KEYS_ROW_FOUR = CHARACTER_KEY_ARRAY.length - NUM_KEYS_ROW_ONE - NUM_KEYS_ROW_TWO - NUM_KEYS_ROW_THREE;	//The number of keys on the fourth row
	
	private static final int PERIOD = 10;															//The delay before reading new mouse points
	
	private static final double BIG_THRESHOLD = 150.0;												//The upper-threshold to consider a key to be a corner and to be confidently in the output
	
	private final ArrayList<Coordinate> mouseCoords;												//The ArrayList of mouse coordinates that were dragged
	private final ArrayList<Character> mouseChars;													//The ArrayList of characters that correspond to the mouse coordinates
	
	private final Timer timer;																		//A Timer to delay capturing mouse coordinates
	private final TimerTask mouseCheck;																//The TimerTask that captures mouse coordinates
	
	private final KeyRect keyRectArray[];															//The array of all key rectangles
	
	private volatile Coordinate currentPos;															//The current position of the mouse
	
	protected volatile boolean mouseDragged;														//Whether or not the mouse is currently dragging
	
	private final MainWindow parent;																//The parent window
	
	private String wholeInput;																		//The whole combination String of the last swiped word
	private String wholeOutput;																		//The output word or words
	
	/**
	 * The Constructor.
	 * @param parent The parent MainWindow.
	 */
	public Keyboard(MainWindow parent)
	{
		//Add the mouse and mouse motion listeners.
		addMouseListener(this);
		addMouseMotionListener(this);
		
		this.currentPos = new Coordinate(0,0);
		
		this.mouseCoords = new ArrayList<Coordinate>();
		this.mouseChars = new ArrayList<Character>();
		
		//set up the key rectangles for the keyboard
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
		
		//set up the timer
		this.timer = new Timer();
		this.mouseCheck = new MouseCheck(this);
		this.timer.schedule(this.mouseCheck, PERIOD, PERIOD);
		
		this.mouseDragged = false;
		
		this.setPreferredSize(new Dimension(ROW_ONE_COL_ONE_LOC+NUM_KEYS_ROW_ONE*KeyRect.WIDTH+NUM_KEYS_ROW_ONE*KeyRect.BORDER_ZONE, 4*KeyRect.HEIGHT+4*KeyRect.BORDER_ZONE));
		
		this.parent = parent;
		
		this.wholeInput = "";
		this.wholeOutput = "";
	}//end constuctor(MainWindow)
	
	/**
	 * Switches the most recent output to the given String.
	 * This is only for the Levenshtein Distance computation.
	 * Note that this will mess up the distance computation for outputs longer than a word.
	 * @param word The String that replaced the original output.
	 */
	public void switchToWord(String word)
	{
		this.wholeOutput = word;
	}
	
	/**
	 * Simulates a key press by changing the state of the key's rectangle.
	 * @param c The character
	 */
	public void simulateKeyPress(char c)
	{
		c = Character.toUpperCase(c);
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
	
	/**
	 * Retrieves the three Strings that correspond to the Levenshtein computation, the keyboard input, the output, and the Levenshtein Distance String.
	 * @return Returns an array of three Strings corresponding to the keyboard input, the output, and the Levenshtein Distance String.
	 */
	public String[] getLevenshteinSet()
	{
		return new String[]{LevenshteinDistance.getRealInputFromCombo(this.wholeInput), this.wholeOutput, LevenshteinDistance.levenshteinDistance(this.wholeInput, this.wholeOutput)};
	}
	
	/**
	 * Retrieves the Levenshtein Distance String of the most recent input and output.
	 * @return Returns the Levenshtein String.
	 */
	public String getLastInputLevenshteinDistance()
	{
		return LevenshteinDistance.levenshteinDistance(this.wholeInput, this.wholeOutput);
	}
	
	/**
	 * Adds the mouse coordinates to the arraylist.
	 * Should be called by a TimerTask to prevent over-sensitivity.
	 */
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
		}//end if
	}//end method
	
	/**
	 * Generates an array of formatted Strings.
	 * The array size is depenedent on the number of spaces that the user has inputted.
	 * @return Returns an array of the input Strings that are correctly formatted to be processed.
	 */
	private String[] generateFormattedString()
	{
		List<Character> chars = new ArrayList<Character>(this.mouseChars);
		
		//Start with delimiter and first character
		StringBuilder sb = new StringBuilder("" + Dictionary.DELIMITER + String.valueOf(chars.get(0)));
		
		//retrieve three valid coordinates to check for angles
		int i = 0;
		//first coordinate
		Coordinate prevCoord = this.mouseCoords.get(0);
		
		//second coordinate
		Coordinate thisCoord = this.mouseCoords.get(1);
		for(i = 2; i < chars.size()-1 && Coordinate.tooClose(prevCoord, thisCoord); i++)
		{
			thisCoord = this.mouseCoords.get(i);
		}
		
		//exception if there is only one character
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
		
		//third coordinate
		boolean isCorner = true;
		char thisChar = chars.get(i);
		out: for(i = i+1; i < chars.size()-1; i++)
		{
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
			
			//get the character
			char nextChar = chars.get(i);
			
			//if the previous angle showed a corner and the character is still the same,
			//just go to the next valid three points
			if(isCorner && sb.charAt(sb.length()-1) == thisChar)
			{
				prevCoord = thisCoord;
				thisCoord = nextCoord;
				thisChar = nextChar;
				continue;
			}
			
			isCorner = false;
			
			//Checks if the corner is small enough
			double angle = Coordinate.getAngle(prevCoord, thisCoord, nextCoord);
//			if((angle < BIG_THRESHOLD && angle > SMALL_THRESHOLD) || (angle > -BIG_THRESHOLD && angle < -SMALL_THRESHOLD))
			if(angle < BIG_THRESHOLD && angle > -BIG_THRESHOLD)
			{
				if(sb.charAt(sb.length()-2) != Dictionary.DELIMITER)
				{
					if(sb.charAt(sb.length()-1) != thisChar)
					{
						sb.append("" + thisChar);
					}
					sb.append("" + Dictionary.DELIMITER + thisChar);
				}
				prevCoord = thisCoord;
				thisCoord = nextCoord;
				thisChar = nextChar;
				isCorner = true;
				continue;
			}//end if

			//Checks for an already added character
			if(sb.charAt(sb.length()-1) != thisChar)
			{
				sb.append(String.valueOf(thisChar));
			}
			
			prevCoord = thisCoord;
			thisCoord = nextCoord;
			thisChar = nextChar;
		}//end for
		
		//check for end character case.
		if(sb.charAt(sb.length()-1) != chars.get(chars.size()-1))
		{
			sb.append(String.valueOf(chars.get(chars.size()-1)));
		}
		
		//end with delimiter
		sb.append(""+Dictionary.DELIMITER);
		
		
		//check for singular ending character
		if(sb.length() > 3 && sb.charAt(sb.length()-2) == sb.charAt(sb.length()-4) && sb.charAt(sb.length()-3) == Dictionary.DELIMITER)
		{
			this.wholeInput = sb.substring(0, sb.length()-2);
		}
		else
		{
			this.wholeInput = sb.toString();
		}
		
		return splitFormattedString(this.wholeInput);
	}//end method()
	
	/**
	 * Splits the formatted String based on spaces.
	 * @param formatted The whole formatted combination String.
	 * @return Returns an array of Strings that were split by spaces.
	 */
	private static String[] splitFormattedString(String formatted)
	{
		//exception of a space character
		if(formatted.equals("_ _"))
		{
			return new String[]{formatted};
		}
		
		String[] split = formatted.split(" ");
		
		//exception of a single character
		if(split.length == 1)
		{
			return new String[]{formatted};
		}
		
		//begin splitting and finding the spaces
		ArrayList<String> newFormattedStrings = new ArrayList<String>();
		String combined = "";
		for(int i = 0; i < formatted.length(); i++)
		{
			if(formatted.charAt(i) == ' ')
			{
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
					newFormattedStrings.add(combined);
				}
				
				combined = "";
			}
			else
			{
				combined += "" + formatted.charAt(i);
			}
		}//end for
		
		if(/*!skip && */!combined.isEmpty())
		{
			newFormattedStrings.add(combined);
		}
		
		return newFormattedStrings.toArray(new String[0]);
	}//end method(String)
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		//paint keys
		for(int i=0; i<this.keyRectArray.length; i++)
		{
			keyRectArray[i].paintComponent(g);
		}
		
		//get mouse coordinates
		ArrayList<Coordinate> mouseCoordinatesPath = null;
//		synchronized(this)
//		{
			mouseCoordinatesPath = new ArrayList<Coordinate>(this.mouseCoords);
//		}
		
		//paint mouse line.
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
	}//end method(Graphics)
	
	/**
	 * {@inheritDoc}
	 * If the mouse is dragging, update the current position and update the display state of the keys.
	 */
	@Override
	public void mouseDragged(MouseEvent e)
	{
		//update current position
		synchronized(this.currentPos)
		{
			this.currentPos = new Coordinate(e.getX(), e.getY());
		}
		
		//update key rectangles
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
	}//end method(MouseEvent)
	
	/**
	 * {@inheritDoc}
	 * If the mouse is moving, update the current position and update the display state of the keys.
	 */
	@Override
	public void mouseMoved(MouseEvent e)
	{
		//update current position
		synchronized(this.currentPos)
		{
			this.currentPos = new Coordinate(e.getX(), e.getY());
		}
		
		//update key rectangles
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
	}//end method(MouseEvent)
	
	/**
	 * {@inheritDoc}
	 * Unused.
	 */
	@Override
	public void mouseClicked(MouseEvent e)
	{
		//empty
	}
	
	/**
	 * {@inheritDoc}
	 * A mouse press will update the display state of the key and will begin the dragging state.
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		//update key displays
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
		}//end for
		
		//update dragging state
		synchronized(this)
		{
			this.mouseDragged = true;
		}
		
		repaint();
		e.consume();
	}//end method(MouseEvent)
	
	/**
	 * {@inheritDoc}
	 * A mouse release will update the display state of the key and will end the dragging state.
	 * In addition, the words will be computed from the inputted keys.
	 */
	@Override
	public void mouseReleased(MouseEvent e)
	{	
		//update key displays
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
			}
			else
			{
				this.keyRectArray[i].isIn = false;
			}
		}//end for
		
		//gets the words
		this.wholeOutput = this.parent.getWordFromSwipes(this.generateFormattedString());
		
		//update dragging state and reset for new mouse dragging event
		synchronized(this)
		{
			this.mouseCoords.clear();
			this.mouseChars.clear();
			this.mouseDragged = false;
		}
		
		repaint();
		e.consume();
	}//end method(MouseEvent)
	
	/**
	 * {@inheritDoc}
	 * If the mouse enters, just put focus to the keyboard and the input text area.
	 */
	@Override
	public void mouseEntered(MouseEvent e)
	{
		this.requestFocusInWindow(); //MAYBE?
		repaint();
	}
	
	/**
	 * {@inheritDoc}
	 * Only updates the display.
	 */
	@Override
	public void mouseExited(MouseEvent e)
	{
		repaint();
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
}//end class

/**
 * The TimerTask for timing mouse position capture.
 * @author Nathan Ong and Jose Michael Joseph
 */
class MouseCheck extends TimerTask
{
	private final Keyboard k;	//The Keyboard
	
	/**
	 * The constructor.
	 * @param k The Keyboard.
	 */
	public MouseCheck(Keyboard k)
	{
		super();
		this.k = k;
	}
	
	/**
	 * {@inheritDoc}
	 * When the Timer goes off, the method will ask the Keyboard to take note of the position of the mouse.
	 */
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
}//end class
