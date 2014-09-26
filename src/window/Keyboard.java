package window;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Keyboard extends JPanel implements MouseMotionListener, MouseListener
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
	
//	private static final int xs[] = {10,35,60,85,110,135,160,185,210,235,20,45,70,95,120,145,170,195,220,40,65,90,115,140,165,190,40};
	private char strlst[] = new char[100];
	 
	private ArrayList<Integer> mouseCoordX = new ArrayList<Integer>();	//All X coords
	private ArrayList<Integer> mouseCoordY = new ArrayList<Integer>();	//All Y coords
	private ArrayList<Double> listOfAngles = new ArrayList<Double>();	//All angles, 0 --> 0, 1, 2
	
	private final Timer timer;
	
	private final  KeyRect keyRectArray[];
	private int pos = 0;
	
	private int mx,my;
	private int ox,oy;
	
	boolean mouseDragged=false;

	public Keyboard()
	{
		addMouseMotionListener(this);
		
//		int colStartLoc = ROW_ONE_COL_ONE_LOC;
		
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
//		this.timer.schedule(task, delay, period);
	}
	
//	//on mouse release, you clear the buffer, set all red rectangles to empty (false)
//	public void drawing()
//	{
//		repaint();
//	}
	
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
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		ox = mx;
		oy = my;
		mx = e.getX();
		my = e.getY();
		mouseCoordX.add(mx);
		mouseCoordY.add(my);
		strlst[0] = '_';
		char hold='\0';
		/*
		pt_x[pos] = mx;
		pt_y[pos] = my;
		pos++;
		*/
		
		if(mouseCoordX.size()>3)
		{
			for(int i=listOfAngles.size(); i<mouseCoordX.size()-3; i++)
			{
				double temp1 = Math.atan2(mouseCoordY.get(i+2)-mouseCoordY.get(i+1), mouseCoordX.get(i+2)-mouseCoordX.get(i+1));
				double temp2 = Math.atan2(mouseCoordY.get(i+1)-mouseCoordY.get(i), mouseCoordX.get(i+1)-mouseCoordX.get(i));
				double total = temp1 - temp2;
				listOfAngles.add(Math.toDegrees(total));
				System.out.println("The total is "+Math.toDegrees(total));
			}
		}
		
		
		//System.out.println("Is there anybody out there?");
		
//		//int g=0;
//		if(my<45)
//		{
//		for(int i=0; i<10; i++)
//		{
//			if(i!=9)
//			{
//				if(mx>xs[i]&&mx<xs[i+1])
//				{
//					r[i].isOn = true;
//					hold  = r[i].getKeyChar();
//					/*if(strlst[pos]!=r[i].val)
//					{
//						pos++;
//						strlst[pos]=r[i].val;
//						//System.out.println(strlst[pos]);
//					}*/
//					
//				}
//				else
//				{
//					r[i].isOn = false;
//				}
//			}
//			else
//			{
//				if(mx>xs[i]&&mx<xs[i]+20)
//				{
//					r[i].isOn = true;
//					hold = r[i].getKeyChar();
//					/*if(strlst[pos]!=r[i].val)
//					{
//						pos++;
//						strlst[pos]=r[i].val;
//						//System.out.println(strlst[pos]);
//					}*/
//				}
//				else
//					r[i].isOn=false;
//			}
//			
//			
//		}
//		}
//		else if(my<80)
//		{
//		
//			for(int i=10; i<19; i++)
//			{
//				if(i!=18)
//				{
//					if(mx>xs[i]&&mx<xs[i+1])
//					{
//						r[i].isOn = true;
//						hold = r[i].getKeyChar();
//						/*if(strlst[pos]!=r[i].val)
//						{
//							pos++;
//							strlst[pos]=r[i].val;
//							//System.out.println(strlst[pos]);
//						}*/
//					}
//					else
//					{
//						r[i].isOn = false;
//					}
//				}
//				else
//				{
//					if(mx>xs[i]&&mx<xs[i]+20)
//					{
//						r[i].isOn = true;
//						/*if(strlst[pos]!=r[i].val)
//						{
//							pos++;
//							strlst[pos]=r[i].val;
//							//System.out.println(strlst[pos]);
//						}*/
//						hold = r[i].getKeyChar();
//					}
//					else
//						r[i].isOn=false;
//				}
//				
//				
//			}
//			
//		}//elif ends my 80
//		else if(my<115)
//		{
//			for(int i=19; i<26; i++)
//			{
//				if(i!=25)
//				{
//					if(mx>xs[i]&&mx<xs[i+1])
//					{
//						r[i].isOn = true;
//						/*if(strlst[pos]!=r[i].val)
//						{
//							pos++;
//							strlst[pos]=r[i].val;
//							//System.out.println(strlst[pos]);
//						}*/
//						hold = r[i].getKeyChar();
//					}
//					else
//					{
//						r[i].isOn = false;
//					}
//				}
//				else
//				{
//					if(mx>xs[i]&&mx<xs[i]+20)
//					{
//						r[i].isOn = true;
//						/*if(strlst[pos]!=r[i].val)
//						{
//							pos++;
//							strlst[pos]=r[i].val;
//							//System.out.println(strlst[pos]);
//						}*/
//						hold = r[i].getKeyChar();
//					}
//					else
//						r[i].isOn=false;
//				}
//				
//				
//			}
//		
//		}//end of all check ifs
		
		for(int i = 0; i < this.keyRectArray.length; i++)
		{
			if(this.keyRectArray[i].isInside(this.mx, this.my))
			{
				this.keyRectArray[i].isOn = true;
				hold = this.keyRectArray[i].getKeyChar();
			}
			else
			{
				this.keyRectArray[i].isOn = false;
			}
		}
		
		if(strlst[pos]!=hold&&hold!='\0')
		{
			pos++;
			strlst[pos]=hold;
		}
		
		System.out.print("New word set ");
		for(int j=0; j<pos; j++)
			System.out.print(strlst[j]+" angle("+listOfAngles.get(j)+")" );
		System.out.println();
		
		mouseDragged = true;
		repaint();
		e.consume();
	}
	

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
		mouseDragged = false;
		mx = e.getX();
		my = e.getY();
		
		for(int i=0; i<19; i++)
		{
			keyRectArray[i].isOn=false;
		}
		pos=0;
		
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
		//START TIMER
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		this.mouseCoordX.clear();
		this.mouseCoordY.clear();
		this.listOfAngles.clear();
		//DO MATH?
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
//		this.requestFocusInWindow(); //MAYBE?
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		
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
		// TODO Auto-generated method stub
		
	}
	
}