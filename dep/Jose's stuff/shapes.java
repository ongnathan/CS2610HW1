package playshapes;

import javax.swing.JFrame;

public class shapes {

	public static void main(String args[])
	{
		JFrame frame = new JFrame("Test");
		frame.setVisible(true);
		frame.setSize(400,200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		draw object = new draw();
		frame.add(object);
		
		object.drawing();
		
	}
	
}
