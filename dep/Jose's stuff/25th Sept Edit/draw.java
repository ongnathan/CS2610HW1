package playshapes;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class draw extends JPanel implements MouseMotionListener{
	
	int xs[] = {10,35,60,85,110,135,160,185,210,235,20,45,70,95,120,145,170,195,220,40,65,90,115,140,165,190};
	char vals[] = {'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M'};
	char strlst[] = new char[100];
	
	//public int pt_x[] = new int[100];
	//public int pt_y[] = new int[100];
	 int pt_pos = 0;
	 
	 ArrayList<Integer> xco = new ArrayList<Integer>();
	 ArrayList<Integer> yco = new ArrayList<Integer>();
	 ArrayList<Double> alist = new ArrayList<Double>();
	
	recta r[]=new recta[26];
	int pos = 0;
	//recta t = new recta(10,50);
	
	//draw obj = new draw();
	
	
	
	public draw()
	{
		addMouseMotionListener(this);
		for(int i=0; i<10; i++)
		{
			r[i] = new recta(xs[i], 15);
			this.add(r[i]);
			r[i].val = vals[i];
		}
		for(int i=10; i<19; i++)
		{
			r[i] = new recta(xs[i],50);
			this.add(r[i]);
			r[i].val = vals[i];
		}
		for(int i=19; i<26; i++)
		{
			r[i] = new recta(xs[i],85);
			this.add(r[i]);
			r[i].val = vals[i];
		}
		//this.add(t);
		
	}
	
	int mx,my;
	int ox,oy;
	
	boolean mouseDragged=false;
	
	public void drawing()
	{
		repaint();
		
	}
	
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for(int i=0; i<26; i++)
			r[i].paintComponent(g);
		if(xco.size()>1)
		{
			for(int i=1; i<xco.size(); i++)
				g.drawLine(xco.get(i-1),yco.get(i-1),xco.get(i),yco.get(i));
		}
		
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		ox = mx;
		oy = my;
		mx = e.getX();
		my = e.getY();
		xco.add(mx);
		yco.add(my);
		strlst[0] = '_';
		char hold='\0';
		/*
		pt_x[pos] = mx;
		pt_y[pos] = my;
		pos++;
		*/
		
		if(xco.size()>3)
		{
			for(int i=alist.size(); i<xco.size()-3; i++)
			{
				double temp1 = Math.atan2(yco.get(i+2)-yco.get(i+1), xco.get(i+2)-xco.get(i+1));
				double temp2 = Math.atan2(yco.get(i+1)-yco.get(i), xco.get(i+1)-xco.get(i));
				double total = temp1 - temp2;
				alist.add(Math.toDegrees(total));
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
			System.out.print(strlst[j]+" angle("+alist.get(j)+")" );
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
	

}

