package window.levenshteinPopup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import backend.stringDist.LevenshteinDistance;

public class LevenshteinPopup extends JPanel
{
	private final String input;
	private final String output;
	private final String levenshteinDistanceString;
	
	private static final Border BORDER_BLACK = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20), BorderFactory.createLineBorder(Color.BLACK));
	private static final Border BORDER_WHITE = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20), BorderFactory.createLineBorder(Color.WHITE));
	private static final Font FONT = new Font("default", Font.BOLD, 16);
	
	public LevenshteinPopup(String[] levenshteinSet)
	{
		this(levenshteinSet[0], levenshteinSet[1], levenshteinSet[2]);
	}
	
	public LevenshteinPopup(String input, String output, String levenshteinDistanceString)
	{
		super();
		this.input = input;
		this.output = output;
		this.levenshteinDistanceString = levenshteinDistanceString;
		
		this.setLayout(new BorderLayout());
		
		JTextArea inputArea = new JTextArea("Input: " + this.input);
		inputArea.setFont(FONT);
		inputArea.setEditable(false);
		inputArea.setBorder(BORDER_BLACK);
		JTextArea outputArea = new JTextArea("Output: " + this.output);
		outputArea.setEditable(false);
		outputArea.setBorder(BORDER_BLACK);
		outputArea.setFont(FONT);
		LevenshteinVisualizationTextArea levenshteinDistanceArea = new LevenshteinVisualizationTextArea(this.levenshteinDistanceString);
//		levenshteinDistanceArea.setEditable(false);
		levenshteinDistanceArea.setBorder(BORDER_WHITE);
		levenshteinDistanceArea.setFont(FONT);
		
		JPanel center = new JPanel(new GridLayout(3,1));
		center.add(inputArea);
		center.add(outputArea);
		center.add(levenshteinDistanceArea);
//		center.setMinimumSize(new Dimension(500, 300));
		
		this.add(center);
		this.setPreferredSize(new Dimension(1000, 500));
	}
}

class LevenshteinVisualizationTextArea extends JPanel
{
//	private final String input;
	private final String levenshteinDistanceString;
	
//	private static final int charWidth = 
	
	public LevenshteinVisualizationTextArea(String levenshteinDistanceString)
	{
		super();
		super.setBackground(Color.BLACK);
//		this.input = input;
		this.levenshteinDistanceString = levenshteinDistanceString;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
//		Point topLeft = super.getLocation();
//		System.out.println("(" + topLeft.x + "," + topLeft.y + ")");
		FontMetrics fm = g.getFontMetrics();
		int currX = 20;
		int currY = 20 + fm.getHeight();
		
//		g.setColor(Color.BLACK);
		for(int i = 0; i < this.levenshteinDistanceString.length(); i++)
		{
			char c = this.levenshteinDistanceString.charAt(i);
			switch(c)
			{
				case '+':
					g.setColor(Color.YELLOW);
					break;
				case '=':
					g.setColor(Color.CYAN);
					break;
				case '-':
					g.setColor(Color.RED);
					break;
				default:
					char[] toDraw = null;
					if(c == ' ')
					{
						toDraw = new char[]{this.levenshteinDistanceString.charAt(i-1), c};
					}
					else
					{
						toDraw = new char[]{c};
					}
					g.drawChars(toDraw, 0, toDraw.length, currX, currY);
					currX += fm.charsWidth(toDraw, 0, toDraw.length);
					break;
			}
		}
		
		currX = 20;
		currY = 20 + 2*fm.getHeight();
		g.setColor(Color.WHITE);
		String distance = ", Distance = " + LevenshteinDistance.calculateLevenshteinDistance(this.levenshteinDistanceString);
		g.drawChars(distance.toCharArray(), 0, distance.length(), currX, currY);
		
		currX = 20;
		currY = 20 + 3*fm.getHeight();
		String[] colorDescriptions = new String[]{"YELLOW indicates addition, ", "CYAN indicates the same, ", "RED indicates removal."};
		for(int i = 0; i < colorDescriptions.length; i++)
		{
			switch(i)
			{
				case 0:
					g.setColor(Color.YELLOW);
					break;
				case 1:
					g.setColor(Color.CYAN);
					break;
				case 2:
					g.setColor(Color.RED);
					break;
			}
			g.drawChars(colorDescriptions[i].toCharArray(), 0, colorDescriptions[i].length(), currX, currY);
			currX += fm.charsWidth(colorDescriptions[i].toCharArray(), 0, colorDescriptions[i].length());
		}
		
		currX = 20;
		currY = 20 + 4*fm.getHeight();
		g.setColor(Color.WHITE);
		String spaceDescription = "A symbol with a space indicates the operation needed regarding the space in the input/output";
		g.drawChars(spaceDescription.toCharArray(), 0, spaceDescription.length(), currX, currY);
	}
}
