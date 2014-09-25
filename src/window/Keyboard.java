package window;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Keyboard extends JPanel implements MouseMotionListener, MouseListener
{
	
	private final int xs[] = {10,35,60,85,110,135,160,185,210,235,20,45,70,95,120,145,170,195,220,40,65,90,115,140,165,190};
	private final char charKeyVals[] = {'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M'};
	private char strlst[] = new char[100];
	
//	int pt_pos = 0;
	 
	private ArrayList<Integer> mouseCoordX = new ArrayList<Integer>();	//All X coords
	private ArrayList<Integer> mouseCoordY = new ArrayList<Integer>();	//All Y coords
	private ArrayList<Double> listOfAngles = new ArrayList<Double>();	//All angles, 0 --> 0, 1, 2
	
	private KeyRect r[]=new KeyRect[26];
	private int pos = 0;
	
	private int mx,my;
	private int ox,oy;
	
	boolean mouseDragged=false;

	public Keyboard()
	{
		addMouseMotionListener(this);
		for(int i=0; i<10; i++)
		{
			r[i] = new KeyRect(xs[i], 15);
			this.add(r[i]);
			r[i].val = charKeyVals[i];
		}
		for(int i=10; i<19; i++)
		{
			r[i] = new KeyRect(xs[i],50);
			this.add(r[i]);
			r[i].val = charKeyVals[i];
		}
		for(int i=19; i<26; i++)
		{
			r[i] = new KeyRect(xs[i],85);
			this.add(r[i]);
			r[i].val = charKeyVals[i];
		}
	}
	
//	//on mouse release, you clear the buffer, set all red rectangles to empty (false)
//	public void drawing()
//	{
//		repaint();
//	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for(int i=0; i<26; i++)
		{
			r[i].paintComponent(g);
		}
		if(mouseCoordX.size()>1)
		{
			for(int i=1; i<mouseCoordX.size(); i++)
			{
				g.drawLine(mouseCoordX.get(i-1),mouseCoordY.get(i-1),mouseCoordX.get(i),mouseCoordY.get(i));
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
		
		//int g=0;
		if(my<45)
		{
		for(int i=0; i<10; i++)
		{
			if(i!=9)
			{
				if(mx>xs[i]&&mx<xs[i+1])
				{
					r[i].isOn = true;
					hold  = r[i].val;
					/*if(strlst[pos]!=r[i].val)
					{
						pos++;
						strlst[pos]=r[i].val;
						//System.out.println(strlst[pos]);
					}*/
					
				}
				else
				{
					r[i].isOn = false;
				}
			}
			else
			{
				if(mx>xs[i]&&mx<xs[i]+20)
				{
					r[i].isOn = true;
					hold = r[i].val;
					/*if(strlst[pos]!=r[i].val)
					{
						pos++;
						strlst[pos]=r[i].val;
						//System.out.println(strlst[pos]);
					}*/
				}
				else
					r[i].isOn=false;
			}
			
			
		}
		}
		else if(my<80)
		{
		
			for(int i=10; i<19; i++)
			{
				if(i!=18)
				{
					if(mx>xs[i]&&mx<xs[i+1])
					{
						r[i].isOn = true;
						hold = r[i].val;
						/*if(strlst[pos]!=r[i].val)
						{
							pos++;
							strlst[pos]=r[i].val;
							//System.out.println(strlst[pos]);
						}*/
					}
					else
					{
						r[i].isOn = false;
					}
				}
				else
				{
					if(mx>xs[i]&&mx<xs[i]+20)
					{
						r[i].isOn = true;
						/*if(strlst[pos]!=r[i].val)
						{
							pos++;
							strlst[pos]=r[i].val;
							//System.out.println(strlst[pos]);
						}*/
						hold = r[i].val;
					}
					else
						r[i].isOn=false;
				}
				
				
			}
			
		}//elif ends my 80
		else if(my<115)
		{
			for(int i=19; i<26; i++)
			{
				if(i!=25)
				{
					if(mx>xs[i]&&mx<xs[i+1])
					{
						r[i].isOn = true;
						/*if(strlst[pos]!=r[i].val)
						{
							pos++;
							strlst[pos]=r[i].val;
							//System.out.println(strlst[pos]);
						}*/
						hold = r[i].val;
					}
					else
					{
						r[i].isOn = false;
					}
				}
				else
				{
					if(mx>xs[i]&&mx<xs[i]+20)
					{
						r[i].isOn = true;
						/*if(strlst[pos]!=r[i].val)
						{
							pos++;
							strlst[pos]=r[i].val;
							//System.out.println(strlst[pos]);
						}*/
						hold = r[i].val;
					}
					else
						r[i].isOn=false;
				}
				
				
			}
		
		}//end of all check ifs
		
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
			r[i].isOn=false;
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
