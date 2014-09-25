package window;
//package playshapes;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class KeyRect extends JPanel {
	
	//champojan
	
	int x,y;
	
	boolean isOn;
	
	char val;
	
	public KeyRect(int x1, int y1)
	{
		x = x1;
		y = y1;
		//repaint();
	}
	
	public void paintComponent(Graphics g)
	{
		//System.out.println("We're inside the cores of recta");
		super.paintComponent(g);
		if(!isOn)
		{
			g.setColor(Color.BLACK);
			g.drawRect(x, y, 20, 30);
		}
		else
		{
			g.setColor(Color.RED);
			g.fillRect(x, y, 20, 30);
			System.out.print(val);
		}
	}
}
