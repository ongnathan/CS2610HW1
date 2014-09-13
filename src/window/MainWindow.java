package window;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MainWindow {
	
	private final JFrame mainFrame;
	private final JPanel mainPanel;
	
	private final JTextArea sample;
	private final JTextArea input;
	private final JPanel keyboard; //TODO replace with actual keyboard
	
	public MainWindow()
	{
		this.mainFrame = setUpFrame();
		this.mainPanel = new JPanel(new BorderLayout());
		
		this.sample = new JTextArea("A QUICK BROWN FOX JUMPS OVER THE LAZY DOG", 1, 50);
		this.input = new JTextArea(1, 50);
		this.keyboard = new JPanel();
		
		this.mainPanel.add(this.sample, BorderLayout.NORTH);
		this.mainPanel.add(this.input, BorderLayout.CENTER);
		this.mainPanel.add(this.keyboard, BorderLayout.SOUTH);
		
		this.mainFrame.add(this.mainPanel);
//		this.mainFrame.setVisible(true);
	}
	
	private static final JFrame setUpFrame()
	{
		//TODO change title
		JFrame frame = new JFrame("TITLE");
		frame.setSize(new Dimension(500, 500));
		frame.setResizable(false);
		return frame;
	}

}
