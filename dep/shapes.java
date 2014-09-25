//package playshapes;

import javax.swing.JFrame;

import window.Keyboard;

public class shapes {

	public static void main(String args[])
	{
		JFrame frame = new JFrame("Test");
		frame.setVisible(true);
		frame.setSize(400,200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Keyboard object = new Keyboard();
		frame.add(object);
		
		object.drawing();
		
	}
	
}
