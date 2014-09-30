package window.keyboard;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import window.Coordinate;

/**
 * The rectangle representing the key on the keyboard.
 * @author Nathan Ong and Jose Michael Joseph
 */
public class KeyRect extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2453008013393846987L;

	/**
	 * The width of a key, in pixels.
	 */
	public static final int WIDTH = 40;
	
	/**
	 * The height of a key, in pixels.
	 */
	public static final int HEIGHT = 50;
	
	/**
	 * The amount of space to surround the key, in pixels.
	 */
	public static final int BORDER_ZONE = 1;
	
	/**
	 * The font size for the characters in the keys.
	 */
	public static final int FONT_SIZE = 16;
	
	/**
	 * The movement of the key character to the right, in pixels, to align with the key.
	 */
	public static final int FONT_BUFFER_WIDTH = 10;
	
	/**
	 * The movement of the key character downwards, in pixels, to align with the key.
	 */
	public static final int FONT_BUFFER_HEIGHT = 15;
	
	/**
	 * Whether or not the mouse is inside the key and is dragging.
	 */
	public boolean isInDragged;
	
	/**
	 * Whether or not the mouse is inside the key.
	 */
	public boolean isIn;
	
	/**
	 * Whether or not the key was typed on the physical keyboard.
	 */
	public boolean isTyped;
	
	/**
	 * The width of the key.
	 */
	protected int width;
	
	/**
	 * The height of the key.
	 */
	protected int height;
	
	/**
	 * The String to display on the key.
	 */
	protected String disp;
	
	private final Coordinate location;	//The location of the top left of the key
	private final char keyChar;			//The character that this key represents
	
	/**
	 * The constructor that takes in the coordinates for the top left corner, in pixels, and the character that this key represents.
	 * @param leftX The X coordinate representing the left.
	 * @param topY The Y coordinate representing the top.
	 * @param keyChar The character that this key represents.
	 */
	public KeyRect(int leftX, int topY, char keyChar)
	{
		this(new Coordinate(leftX,topY), keyChar);
	}
	
	/**
	 * The constructor that takes in the Coordinate representing the top left corner and the character that this key represents.
	 * @param location The Coordinate representing the top left corner.
	 * @param keyChar The character that this key represents
	 */
	public KeyRect(Coordinate location, char keyChar)
	{
		this.location = location;
		this.keyChar = keyChar;
		this.disp = String.valueOf(this.keyChar);
		this.width = WIDTH;
		this.height = HEIGHT;
		
		this.isInDragged = false;
		this.isIn = false;
	}
	
	/**
	 * Retrieves the character that this key represents.
	 * @return Returns the character that this key represents.
	 */
	public char getKeyChar()
	{
		return this.keyChar;
	}
	
	/**
	 * Retrieves the Coordinate of the top left corner.
	 * @return Returns the Coordinate of the top left corner of the key.
	 */
	public Coordinate getCoordinate()
	{
		return this.location;
	}
	
	/**
	 * Determines whether or not the given Coordinate resides inside the key.
	 * @param c The Coordinate to check.
	 * @return Returns whether or not the given Coordinate resides in the key.  Returns false if the Coordinate does not lie within the key boundary.
	 */
	public boolean isInside(Coordinate c)
	{
		return this.isInside(c.x, c.y);
	}
	
	/**
	 * Determines whether or not the given coordinates resides inside the key.
	 * @param x The X coordinate to check.
	 * @param y The Y coordinate to check.
	 * @return Returns whether or not the given coordinates reside in the key.  Returns false if the coordinates do not lie within the key.
	 */
	public boolean isInside(int x, int y)
	{
		return x >= this.location.x && x <= this.location.x + this.width && y >= this.location.y && y <= this.location.y + this.height;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		//draws the key based on its state
		if(this.isInDragged)
		{
			g.setColor(Color.RED);
			g.fillRect(this.location.x, this.location.y, this.width, this.height);
		}
		else if(this.isTyped)
		{
			g.setColor(Color.CYAN);
			g.fillRect(this.location.x, this.location.y, this.width, this.height);
		}
		else if(this.isIn)
		{
			g.setColor(Color.RED);
		}
		else
		{
			g.setColor(Color.BLACK);
		}
		g.drawRect(this.location.x, this.location.y, this.width, this.height);
		
		//draw the string inside the key.
		g.setColor(Color.BLACK);
		g.setFont(new Font("default", Font.BOLD, FONT_SIZE));
		g.drawChars(this.disp.toCharArray(), 0, this.disp.length(), this.location.x+FONT_BUFFER_WIDTH, this.location.y+FONT_BUFFER_HEIGHT);
	}//end method(Graphics)
}//end class
