package playshapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

public class draw extends JPanel implements MouseMotionListener{
	
	int xs[] = {10,35,60,85,110,135,160,185,210,235,20,45,70,95,120,145,170,195,220,40,65,90,115,140,165,190};
	char vals[] = {'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M'};
	char strlst[] = new char[100];
	//int pt_x[] = new int[100];
	//int pt_y[] = new int[100];
	 int pt_pos = 0;
	//strlst[0] = '_';
	
	recta r[]=new recta[26];
	int pos = 0;
	//recta t = new recta(10,50);
	
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
	
	public static void main(String args[])
	{
		
	draw  x = new draw();
	
	final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    service.scheduleWithFixedDelay(new Runnable()
      {
        @Override
        public void run()
        {
          System.out.println(new Date());
        }
      }, 0, 10, TimeUnit.SECONDS);
	
    
	}
	public void allDraw(int excp, Graphics g)
	{
		g.setColor(Color.BLUE);
		for(int i=0; i<10; i++)
		{
			g.drawRect(xs[i],15,20,30);
		}
		for(int i=10; i<19; i++)
		{
			g.drawRect(xs[i],50,20,30);
		}
		for(int i=19; i<26; i++)
		{
			g.drawRect(xs[i],85,20,30);
		}
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for(int i=0; i<26; i++)
			r[i].paintComponent(g);
		
		
		 
		if(mouseDragged)
		{
		/*	if(pt_pos>0)
			for(int i=0; i<pt_pos-1; i++)
				g.drawLine(pt_x[i], pt_y[i], pt_x[i], pt_y[i+1]);
				*/
		}
		
		else
		{
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		ox = mx;
		oy = my;
		mx = e.getX();
		my = e.getY();
		/*
		pt_x[pos] = mx;
		pt_y[pos] = my;
		pos++;
		*/
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
					if(strlst[pos]!=r[i].val)
					{
						pos++;
						strlst[pos]=r[i].val;
						System.out.println(strlst[pos]);
					}
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
					if(strlst[pos]!=r[i].val)
					{
						pos++;
						strlst[pos]=r[i].val;
						System.out.println(strlst[pos]);
					}
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
						if(strlst[pos]!=r[i].val)
						{
							pos++;
							strlst[pos]=r[i].val;
							System.out.println(strlst[pos]);
						}
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
						if(strlst[pos]!=r[i].val)
						{
							pos++;
							strlst[pos]=r[i].val;
							System.out.println(strlst[pos]);
						}
					}
					else
						r[i].isOn=false;
				}
				
				
			}
			
		}
		
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
