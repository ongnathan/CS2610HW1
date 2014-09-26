package window;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class KeyRect extends JPanel
{
	public static final int WIDTH = 20;
	public static final int HEIGHT = 30;
	public static final int BORDER_ZONE = 5;
	
	private int leftX,topY;
	
	public boolean isInDragged;
	public boolean isIn;
	
	private final char keyChar;
	
	protected int width;
	protected int height;
	protected String disp;
	
	public KeyRect(int leftX, int topY, char keyChar)
	{
		this.leftX = leftX;
		this.topY = topY;
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
	
	public boolean isInside(int x, int y)
	{
		return x >= this.leftX && x <= this.leftX + this.width && y >= this.topY && y <= this.topY + this.height;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		g.setColor(Color.BLACK);
		g.drawChars(this.disp.toCharArray(), 0, this.disp.length(), this.leftX, this.topY);
		
		if(this.isInDragged)
		{
			g.setColor(Color.RED);
			g.fillRect(leftX, topY, this.width, this.height);
//			System.out.print(keyChar);
		}
		else if(this.isIn)
		{
			g.setColor(Color.RED);
		}
		else
		{
			g.setColor(Color.BLACK);
//			g.drawRect(leftX, topY, this.width, this.height);
		}
		g.drawRect(leftX, topY, this.width, this.height);
	}
}
