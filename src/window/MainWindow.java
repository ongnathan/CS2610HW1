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

/**
 * The main display window.
 * @author Nathan Ong and Jose Joseph
 */
public class MainWindow
{
	
	private final JFrame mainFrame;							//The frame
	private final JPanel mainPanel;							//The main panel
	private final JPanel inputArea;							//The input area panel including the textbox and the suggestions buttons
	
	private final JTextArea sample;							//The sample text to display
	private final JTextArea inputText;						//The input text
	private final JPanel alternateSuggestionsPanel;			//The suggestions buttons panel
	private final JButton[] alternateSuggestionsButtons;	//The suggestion buttons
	private final JPanel keyboard; //TODO replace with actual keyboard
	
	private final TextFocusBorderListener tfbl;				//The focus listener for the text area and the keyboard
	private final SuggestionButtonListener sbl;				//All of the button listeners
	private final KeyboardListener kl;						//The keyboard listener
	
	private final Dictionary dictionary;					//The dictionary
	
	/**
	 * The constructor.
	 */
	public MainWindow()
	{
		//dictionary
		this.dictionary = Dictionary.importFromTextFile("all.num");
		
		//main components
		this.mainFrame = setUpFrame();
		this.mainPanel = new JPanel(new BorderLayout(50,50));
		this.inputArea = new JPanel(new GridLayout(2,1));
		
		//sample panel
		JPanel samplePanel = new JPanel();
		this.sample = new JTextArea("A QUICK BROWN FOX JUMPS OVER THE LAZY DOG", 1, 34);
		this.sample.setEditable(false);
		samplePanel.add(this.sample);
		samplePanel.setBorder(BorderFactory.createEmptyBorder(50, 65, 50, 65));
		
		//input text area
		this.inputText = new JTextArea(1, 50);
		this.inputText.setEditable(false);
		this.inputText.setText("_"); //cursor
		
		//suggestions panel
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
		
		//keyboard panel
		this.keyboard = new JPanel();
		this.keyboard.setBorder(BorderFactory.createEmptyBorder(50, 65, 50, 65));
		
		//keyboard listener
		this.kl = new KeyboardListener(this);
		this.keyboard.addKeyListener(this.kl);
		this.inputArea.addKeyListener(this.kl);
		this.inputText.addKeyListener(this.kl);
		
		//focus listener
		this.tfbl = new TextFocusBorderListener(this.inputText, this.keyboard);
		this.inputText.addFocusListener(this.tfbl);
		this.keyboard.addFocusListener(this.tfbl);
		
		//add all components to the main panel
		this.mainPanel.add(samplePanel, BorderLayout.NORTH);
		this.mainPanel.add(this.inputArea, BorderLayout.CENTER);
		this.mainPanel.add(this.keyboard, BorderLayout.SOUTH);
		this.mainPanel.add(new JPanel(), BorderLayout.EAST);
		this.mainPanel.add(new JPanel(), BorderLayout.WEST);
		
		this.mainFrame.add(this.mainPanel);
//		this.mainFrame.setVisible(true);
	}//end constructor()
	
	/**
	 * Clear all the text from the suggestion buttons
	 */
	private void clearSuggestionButtons()
	{
		for(int i = 0; i < this.alternateSuggestionsButtons.length; i++)
		{
			this.alternateSuggestionsButtons[i].setText(null);
		}
	}
	
	/**
	 * Get the words based on the potential phrases.
	 * See {@link Dictionary#getPotentialWords(String)} for more information on the format of the String.
	 * @param potentialKeys The swipe String.
	 */
	public void getWordFromSwipes(String potentialKeys)
	{
		//Check if there are any words
		PriorityQueue<AlphaNode> queue = this.dictionary.getPotentialWords(potentialKeys);
		if(queue.isEmpty())
		{
			//FIXME need some kind of error thingy?
			return;
		}
		
		//prepare for new words
		this.clearSuggestionButtons();
		
		//add the most frequent word to the text
		this.inputText.setText(this.inputText.getText().substring(0, this.inputText.getText().length()-1) + " " + queue.remove().getWord() + "_");
		
		//add additional suggestions with less frequency to the buttons
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
	}//end method(String)
	
	/**
	 * Adds a character to the text input.
	 * @param c The character to add.
	 */
	protected void addText(char c)
	{
		this.addText(String.valueOf(c));
	}
	
	/**
	 * Adds a String to the text input.
	 * @param text The String to add.
	 */
	protected void addText(String text)
	{
		this.inputText.setText(this.inputText.getText().substring(0, this.inputText.getText().length()-1) + text.toUpperCase() + "_");
		this.clearSuggestionButtons();
	}
	
	/**
	 * Removes the last character from the input.
	 */
	protected void backspace()
	{
		String currText = this.inputText.getText().substring(0,this.inputText.getText().length()-1);
		if(currText.isEmpty())
		{
			return;
		}
		this.inputText.setText(currText.substring(0,currText.length()-1) + "_");
		
		
		this.clearSuggestionButtons();
	}
	
	/**
	 * Switches the last word to a new one.
	 * @param suggestion The word to switch.  Should be from a suggestion button.
	 */
	protected void switchWordToSuggestion(String suggestion)
	{
		//if the button was null, ignore the button press
		if(suggestion == null)
		{
			return;
		}
		
		//remove the old word
		String[] currentStrings = this.inputText.getText().split(" ");
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < currentStrings.length-1; i++)
		{
			builder.append(currentStrings[i] + " ");
		}
		
		//add the new word
		builder.append(suggestion);
		builder.append("_");
		this.inputText.setText(builder.toString());
		
		for(int i = 0; i < this.alternateSuggestionsButtons.length; i++)
		{
			this.alternateSuggestionsButtons[i].setText(null);
		}
	}
	
	/**
	 * Sets up the frame.
	 * @return Returns the set up JFrame.
	 */
	private static final JFrame setUpFrame()
	{
		//TODO change title
		JFrame frame = new JFrame("TITLE");
		frame.setSize(new Dimension(500, 500));
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		return frame;
	}
	
	/**
	 * Shows the frame.
	 */
	public void showGUI()
	{
		this.mainFrame.setVisible(true);
	}
	
	/**
	 * The main method.
	 * @param args Unused.
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				MainWindow mw = new MainWindow();
				mw.showGUI();
				mw.getWordFromSwipes("_WA_ASDRT_TRE_ER_");
				mw.getWordFromSwipes("_IUYTFDS_");
				mw.getWordFromSwipes("_GHJUIO_OIUHGFD_");
			}
		});
	}
}

/**
 * The button listener.
 * @author Nathan Ong
 *
 */
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

/**
 * The keyboard listener.
 * @author Nathan Ong
 *
 */
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

//FIXME need to change when the keyboard becomes available
/**
 * The focus listener.
 * Shows a border on the text area and the keyboard when focused.
 * @author Nathan Ong
 *
 */
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
