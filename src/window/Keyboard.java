package window;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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
	
	private static final int ROW_ONE_COL_ONE_LOC = 10;
	private static final int ROW_TWO_COL_ONE_LOC = 20;
	private static final int ROW_THREE_COL_ONE_LOC = 40;
	private static final int ROW_FOUR_COL_ONE_LOC = 40;
	
	private static final char CHARACTER_KEY_ARRAY[] = {'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M',' '};
	
	private static final int NUM_KEYS_ROW_ONE = 10;
	private static final int NUM_KEYS_ROW_TWO = 9;
	private static final int NUM_KEYS_ROW_THREE = 7;
	private static final int NUM_KEYS_ROW_FOUR = CHARACTER_KEY_ARRAY.length - NUM_KEYS_ROW_ONE - NUM_KEYS_ROW_TWO - NUM_KEYS_ROW_THREE;
	
	private static final int PERIOD = 10;
	
//	private static final int xs[] = {10,35,60,85,110,135,160,185,210,235,20,45,70,95,120,145,170,195,220,40,65,90,115,140,165,190,40};
//	private char strlst[] = new char[100];
	private String comboString;
	 
	private ArrayList<Integer> mouseCoordX = new ArrayList<Integer>();	//All X coords
	private ArrayList<Integer> mouseCoordY = new ArrayList<Integer>();	//All Y coords
	private ArrayList<Double> listOfAngles = new ArrayList<Double>();	//All angles, 0 --> 0, 1, 2
	
	private final Timer timer;
	private final TimerTask mouseCheck;
	
	private final  KeyRect keyRectArray[];
//	private int pos = 0;
	
	private volatile int currMouseX,currMouseY;
//	private int ox,oy;
	
	protected volatile boolean mouseDragged;

	public Keyboard()
	{
		addMouseListener(this);
		addMouseMotionListener(this);
		
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
		
		this.comboString = "_";
		
		this.mouseDragged = false;
	}
	
	protected void addMouseCoordinates()
	{
		int currX = -1;
		int currY = -1;
		boolean isDragged = false;
		
		synchronized(this)
		{
			currX = this.currMouseX;
			currY = this.currMouseY;
			isDragged = this.mouseDragged;
		}
		
		if((this.mouseCoordX.isEmpty() && isDragged) || !(this.mouseCoordX.get(this.mouseCoordX.size()-1) == currX && this.mouseCoordY.get(this.mouseCoordY.size()-1) == currY))
		{
			if(this.mouseCoordX.isEmpty())
			{
				System.out.println("(" + currX + "," + currY + ")");
			}
			this.mouseCoordX.add(currX);
			this.mouseCoordY.add(currY);
		}
		
		if(this.mouseCoordX.size() > 3)
		{
			for(int i = this.listOfAngles.size(); i < this.mouseCoordX.size() - 3; i++)
			{
				double temp1 = Math.atan2((this.mouseCoordY.get(i+2) - this.mouseCoordY.get(i+1)), (this.mouseCoordX.get(i+2) - this.mouseCoordX.get(i+1)));
				double temp2 = Math.atan2((this.mouseCoordY.get(i+1) - this.mouseCoordY.get(i)), (this.mouseCoordX.get(i+1) - this.mouseCoordX.get(i)));
				double total = temp1 - temp2;
				this.listOfAngles.add(Math.toDegrees(total));
				System.out.println("The total is "+Math.toDegrees(total));
			}
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
		
		g.setColor(Color.BLUE);
		if(this.mouseCoordX.size()>1)
		{
			for(int i=1; i<this.mouseCoordX.size(); i++)
			{
				g.drawLine(this.mouseCoordX.get(i-1),this.mouseCoordY.get(i-1),this.mouseCoordX.get(i),this.mouseCoordY.get(i));
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		synchronized(this)
		{
			this.currMouseX = e.getX();
			this.currMouseY = e.getY();
		}
		
		for(int i = 0; i < this.keyRectArray.length; i++)
		{
			if(this.keyRectArray[i].isInside(e.getX(), e.getY()))
			{
				this.keyRectArray[i].isInDragged = true;
				this.keyRectArray[i].isIn = true;
				if(!(this.comboString.charAt(this.comboString.length()-1) == this.keyRectArray[i].getKeyChar()))
				{
					this.comboString += this.keyRectArray[i].getKeyChar();
				}
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
			this.currMouseX = e.getX();
			this.currMouseY = e.getY();
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
				if(!(this.comboString.charAt(this.comboString.length()-1) == this.keyRectArray[i].getKeyChar()))
				{
					this.comboString += this.keyRectArray[i].getKeyChar();
				}
				break;
			}
//			else
//			{
//				this.keyRectArray[i].isInDragged = false;
//				this.keyRectArray[i].isIn = false;
//			}
		}
		this.mouseDragged = true;
		
		repaint();
		e.consume();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		this.mouseCoordX.clear();
		this.mouseCoordY.clear();
		this.listOfAngles.clear();
		
		for(int i = 0; i < this.keyRectArray.length; i++)
		{
			this.keyRectArray[i].isInDragged = false;
			if(this.keyRectArray[i].isInside(e.getX(), e.getY()))
			{
				this.keyRectArray[i].isIn = true;
				this.comboString += this.keyRectArray[i].getKeyChar() + "_";
				System.out.println(this.comboString); //TODO DO SOMETHING HERE WITH THIS.COMBOSTRING
			}
			else
			{
				this.keyRectArray[i].isIn = false;
			}
		}
		
		this.comboString = "_";
		this.mouseDragged = false;
		
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
		frame.setSize(400,200);
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