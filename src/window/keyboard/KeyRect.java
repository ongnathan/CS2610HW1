package window.keyboard;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import window.Coordinate;

public class KeyRect extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -180781374082638096L;
	
	public static final int WIDTH = 40;
	public static final int HEIGHT = 50;
	public static final int BORDER_ZONE = 1;
	
	public static final int FONT_SIZE = 16;
	public static final int FONT_BUFFER_WIDTH = 10;
	public static final int FONT_BUFFER_HEIGHT = 15;
	
//	private int leftX,topY;
	private final Coordinate location;
	
	public boolean isInDragged;
	public boolean isIn;
	public boolean isTyped;
	
	private final char keyChar;
	
	protected int width;
	protected int height;
	protected String disp;
	
	public KeyRect(int leftX, int topY, char keyChar)
	{
		this(new Coordinate(leftX,topY), keyChar);
	}
	
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
	
	public char getKeyChar()
	{
		return this.keyChar;
	}
	
	public Coordinate getCoordinate()
	{
		return this.location;
	}
	
	public boolean isInside(Coordinate c)
	{
		return this.isInside(c.x, c.y);
	}
	
	public boolean isInside(int x, int y)
	{
		return x >= this.location.x && x <= this.location.x + this.width && y >= this.location.y && y <= this.location.y + this.height;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
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
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("default", Font.BOLD, FONT_SIZE));
		g.drawChars(this.disp.toCharArray(), 0, this.disp.length(), this.location.x+FONT_BUFFER_WIDTH, this.location.y+FONT_BUFFER_HEIGHT);
	}
}
