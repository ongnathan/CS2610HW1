package window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.PriorityQueue;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import backend.dictionary.AlphaNode;
import backend.dictionary.Dictionary;

public class MainWindow {
	
	private final JFrame mainFrame;
	private final JPanel mainPanel;
	private final JPanel inputArea;
	
	private final JTextArea sample;
	private final JTextArea inputText;
	private final JPanel alternateSuggestionsPanel;
	private final JButton[] alternateSuggestionsButtons;
	private final JPanel keyboard; //TODO replace with actual keyboard
	
	private final TextFocusBorderListener tfbl;
	private final SuggestionButtonListener sbl;
	private final KeyboardListener kl;
	
	private final Dictionary dictionary;
	
	public MainWindow()
	{
		this.dictionary = Dictionary.importFromTextFile("all.num");
		
		this.mainFrame = setUpFrame();
		this.mainPanel = new JPanel(new BorderLayout(50,50));
		this.inputArea = new JPanel(new GridLayout(2,1));
		
		JPanel samplePanel = new JPanel();
		this.sample = new JTextArea("A QUICK BROWN FOX JUMPS OVER THE LAZY DOG", 1, 34);
		this.sample.setEditable(false);
		samplePanel.add(this.sample);
		samplePanel.setBorder(BorderFactory.createEmptyBorder(50, 65, 50, 65));
		
		this.inputText = new JTextArea(1, 50);
		this.inputText.setEditable(false);
		
		this.alternateSuggestionsPanel = new JPanel(new GridLayout(1,4));
		this.alternateSuggestionsButtons = new JButton[4];
		this.sbl = new SuggestionButtonListener(this);
		for(int i = 0; i < this.alternateSuggestionsButtons.length; i++)
		{
			this.alternateSuggestionsButtons[i] = new JButton();
			this.alternateSuggestionsButtons[i].addActionListener(this.sbl);
			this.alternateSuggestionsPanel.add(this.alternateSuggestionsButtons[i], i);
		}
		this.inputArea.add(this.inputText);
		this.inputArea.add(this.alternateSuggestionsPanel);
		
		this.keyboard = new JPanel();
		this.keyboard.setBorder(BorderFactory.createEmptyBorder(50, 65, 50, 65));
		
		this.kl = new KeyboardListener(this);
		this.keyboard.addKeyListener(this.kl);
		this.inputArea.addKeyListener(this.kl);
		this.inputText.addKeyListener(this.kl);
		
		this.tfbl = new TextFocusBorderListener(this.inputText, this.keyboard);
		this.inputText.addFocusListener(this.tfbl);
		this.keyboard.addFocusListener(this.tfbl);
		
		this.mainPanel.add(samplePanel, BorderLayout.NORTH);
		this.mainPanel.add(this.inputArea, BorderLayout.CENTER);
		this.mainPanel.add(this.keyboard, BorderLayout.SOUTH);
		this.mainPanel.add(new JPanel(), BorderLayout.EAST);
		this.mainPanel.add(new JPanel(), BorderLayout.WEST);
		
		this.mainFrame.add(this.mainPanel);
//		this.mainFrame.setVisible(true);
	}
	
	private void clearSuggestionButtons()
	{
		for(int i = 0; i < this.alternateSuggestionsButtons.length; i++)
		{
			this.alternateSuggestionsButtons[i].setText(null);
		}
	}
	
	public void getWordFromSwipes(String potentialKeys)
	{
		PriorityQueue<AlphaNode> queue = this.dictionary.getPotentialWords(potentialKeys);
		if(queue.isEmpty())
		{
			//FIXME need some kind of error thingy
			return;
		}
		this.inputText.setText(this.inputText.getText() + " " + queue.remove().getWord());
		for(int i = 0; i < this.alternateSuggestionsButtons.length; i++)
		{
			if(queue.isEmpty())
			{
				break;
			}
			String suggestionWord = queue.remove().getWord();
			this.alternateSuggestionsButtons[i].setText(suggestionWord);
			this.alternateSuggestionsButtons[i].setToolTipText(suggestionWord);
		}
	}
	
	protected void addText(char c)
	{
		this.addText(String.valueOf(c));
	}
	
	protected void addText(String text)
	{
		this.inputText.setText(this.inputText.getText() + text.toUpperCase());
		this.clearSuggestionButtons();
	}
	
	protected void backspace()
	{
		String currText = this.inputText.getText();
		if(currText.isEmpty())
		{
			return;
		}
		this.inputText.setText(currText.substring(0,currText.length()-1));
		
		this.clearSuggestionButtons();
	}
	
	protected void switchWordToSuggestion(String suggestion)
	{
		if(suggestion == null)
		{
			return;
		}
		
		String[] currentStrings = this.inputText.getText().split(" ");
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < currentStrings.length-1; i++)
		{
			builder.append(currentStrings[i] + " ");
		}
		builder.append(suggestion);
		this.inputText.setText(builder.toString());
		
		for(int i = 0; i < this.alternateSuggestionsButtons.length; i++)
		{
			this.alternateSuggestionsButtons[i].setText(null);
		}
	}
	
	private static final JFrame setUpFrame()
	{
		//TODO change title
		JFrame frame = new JFrame("TITLE");
		frame.setSize(new Dimension(500, 500));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return frame;
	}
	
	public void showGUI()
	{
		this.mainFrame.setVisible(true);
	}
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				MainWindow mw = new MainWindow();
				mw.showGUI();
				mw.getWordFromSwipes("_WA_ASDRT_TRE_ER_");
			}
		});
	}
}

class SuggestionButtonListener implements ActionListener
{
	private final MainWindow mw;
	
	public SuggestionButtonListener(MainWindow mw)
	{
		this.mw = mw;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		this.mw.switchWordToSuggestion(e.getActionCommand());
	}
}

class KeyboardListener implements KeyListener
{
	private final MainWindow mw;
	
	public KeyboardListener(MainWindow mw)
	{
		this.mw = mw;
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		char c = e.getKeyChar();
		if(Character.isAlphabetic(c) || c == ' ')
		{
			this.mw.addText(c);
		}
//		else
//		{
//			System.out.println(e.getKeyCode());
//		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			this.mw.backspace();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		//empty
	}
}

//Shows focus border on both the text area and the keyboard
//FIXME need to change when the keyboard becomes available
class TextFocusBorderListener implements FocusListener
{
	private final JTextArea jta;
	private final JPanel keyboard;
	
	public TextFocusBorderListener(JTextArea jta, JPanel keyboard)
	{
		this.jta = jta;
		this.keyboard = keyboard;
	}
	
	@Override
	public void focusGained(FocusEvent e)
	{
		this.jta.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.keyboard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(50, 65, 50, 65), BorderFactory.createLineBorder(Color.BLACK)));
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		this.jta.setBorder(BorderFactory.createEmptyBorder());
		this.keyboard.setBorder(BorderFactory.createEmptyBorder(50, 65, 50, 65));
	}
}
