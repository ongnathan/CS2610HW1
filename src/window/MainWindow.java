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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import window.keyboard.Keyboard;
import window.levenshteinPopup.LevenshteinPopup;
import backend.dictionary.AlphaNode;
import backend.dictionary.Dictionary;

/**
 * The main display window.
 * @author Nathan Ong and Jose Joseph
 */
public class MainWindow extends JPanel
{
	private static final String CURSOR = "_";
	
	private final JPanel inputArea;							//The input area panel including the textbox and the suggestions buttons
	
	private final JTextArea sample;							//The sample text to display
	private final JTextArea inputText;						//The input text
	private final JPanel alternateSuggestionsPanel;			//The suggestions buttons panel
	private final JButton[] alternateSuggestionsButtons;	//The suggestion buttons
	private final Keyboard keyboard;						//The keyboard
	
	private final TextFocusBorderListener tfbl;				//The focus listener for the text area and the keyboard
	private final SuggestionButtonListener sbl;				//All of the button listeners
	private final KeyboardListener kl;						//The keyboard listener
	
	private final Dictionary dictionary;					//The dictionary
	
	private boolean wasSwiped;
	
	private final JMenuBar menuBar;
	private final JMenu view;
	private final JMenuItem viewLevenshtein;
	
	protected static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(20, 25, 20, 25);
	
	/**
	 * The constructor.
	 */
	public MainWindow()
	{
		super(new BorderLayout());
		//dictionary
		this.dictionary = Dictionary.importFromTextFile("word_freq.txt");
		this.wasSwiped = false;
		
		//main components
//		this.mainPanel = new JPanel(new BorderLayout());
		this.inputArea = new JPanel(new GridLayout(3,1));
		
		//sample panel
//		JPanel samplePanel = new JPanel();
		this.sample = new JTextArea("ACTIONS SPEAK LOUDER THAN WORDS", 1, 34);
		this.sample.setEditable(false);
//		samplePanel.add(this.sample);
		this.sample.setBorder(BorderFactory.createCompoundBorder(EMPTY_BORDER, BorderFactory.createLineBorder(Color.BLUE)));
		
		//input text area
		this.inputText = new JTextArea(1, 50);
		this.inputText.setEditable(false);
		this.inputText.setText("_"); //cursor
		this.inputText.setBorder(BorderFactory.createCompoundBorder(EMPTY_BORDER, BorderFactory.createEtchedBorder(EtchedBorder.RAISED)));
		
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
		this.inputArea.add(this.sample);
		this.inputArea.add(this.inputText);
		this.inputArea.add(this.alternateSuggestionsPanel);
		
		//keyboard panel
//		this.keyboard = new JPanel();
		this.keyboard = new Keyboard(this);
		this.keyboard.setBorder(EMPTY_BORDER);
		
		//keyboard listener
		this.kl = new KeyboardListener(this,this.keyboard);
		this.keyboard.addKeyListener(this.kl);
		this.inputArea.addKeyListener(this.kl);
		this.inputText.addKeyListener(this.kl);
		
		//focus listener
		this.tfbl = new TextFocusBorderListener(this.inputText, this.keyboard);
		this.inputText.addFocusListener(this.tfbl);
		this.inputArea.addFocusListener(this.tfbl);
		this.keyboard.addFocusListener(this.tfbl);
		
		//add all components to the main panel
//		this.mainPanel.add(samplePanel, BorderLayout.NORTH);
		this.add(this.inputArea, BorderLayout.NORTH);
		this.add(this.keyboard, BorderLayout.CENTER);
//		this.mainPanel.add(new JPanel(), BorderLayout.EAST);
//		this.mainPanel.add(new JPanel(), BorderLayout.WEST);
		
		this.menuBar = new JMenuBar();
		this.view = new JMenu("View");
		this.viewLevenshtein = new JMenuItem("Levenshtein Distance Calculator");
		ViewLevenshteinDistanceMenuListener vldml = new ViewLevenshteinDistanceMenuListener(this.keyboard);
		this.viewLevenshtein.addActionListener(vldml);
		
		this.view.add(this.viewLevenshtein);
		this.menuBar.add(this.view);
		
//		this.add(this.mainPanel);
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
			this.alternateSuggestionsButtons[i].setToolTipText(null);
		}
	}
	
	public JMenuBar getMenuBar()
	{
		return this.menuBar;
	}
	
	public String getWordFromSwipes(String[] allGroupsOfPotentialKeys)
	{
		String output = "";
		for(String potentialKeys : allGroupsOfPotentialKeys)
		{
			output += this.getWordFromSwipes(potentialKeys);
		}
		return output;
	}
	
	/**
	 * Get the words based on the potential phrases.
	 * See {@link Dictionary#getPotentialWords(String)} for more information on the format of the String.
	 * @param potentialKeys The swipe String.
	 */
	public String getWordFromSwipes(String potentialKeys)
	{
		String output = "";
		String currentInputText = this.inputText.getText();
		if(potentialKeys.length() == 3)
		{
//			if(currentInputText.length() > 1 && currentInputText.charAt(currentInputText.length()-2) == ' ')
//			{
//				this.inputText.setText(this.inputText.getText().substring(0, this.inputText.getText().length()-1) + String.valueOf(potentialKeys.charAt(1)) + "_");
//			}
//			else
//			{
//				this.inputText.setText(this.inputText.getText().substring(0, this.inputText.getText().length()-1) + " " + String.valueOf(potentialKeys.charAt(1)) + "_");
//			}
			output = ""+potentialKeys.charAt(1);
			this.inputText.setText(this.inputText.getText().substring(0, this.inputText.getText().length()-1) + output + "_");
			return output;
		}
		
		//Check if there are any words
		PriorityQueue<AlphaNode> queue = this.dictionary.getPotentialWords(potentialKeys);
		if(queue.isEmpty())
		{
			//FIXME need some kind of error thingy?
			return output;
		}
		if(queue.size() == 1)
		{
			return output+queue.remove().character;
		}
		
		this.wasSwiped = true;
		//prepare for new words
		this.clearSuggestionButtons();
		
		//add the most frequent word to the text
		if(currentInputText.length() > 1 && currentInputText.charAt(currentInputText.length()-2) == ' ')
		{
			output = queue.remove().getWord();
//			this.inputText.setText(this.inputText.getText().substring(0, this.inputText.getText().length()-1) + output + "_");
		}
		else
		{
			output = " " + queue.remove().getWord();
//			this.inputText.setText(this.inputText.getText().substring(0, this.inputText.getText().length()-1) + " " + queue.remove().getWord() + "_");
		}
		this.inputText.setText(this.inputText.getText().substring(0, this.inputText.getText().length()-1) + output + "_");
		
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
		
		return output;
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
		this.inputText.setText(this.inputText.getText().substring(0, this.inputText.getText().length()-1) + text.toUpperCase() + CURSOR);
		this.clearSuggestionButtons();
	}
	
	/**
	 * Removes the last String from the input.
	 * If the last word was swiped, it will remove the whole word.  Then backspace will continue to remove only single characters.
	 */
	protected void backspace()
	{
		String currText = this.inputText.getText().substring(0,this.inputText.getText().length()-1);
		if(currText.isEmpty())
		{
			return;
		}
		if(this.wasSwiped)
		{
			String[] words = currText.split(" ");
			currText = "";
			if(words.length == 1)
			{
				this.inputText.setText(CURSOR);
			}
			else
			{
				for(int i = 0; i < words.length-1; i++)
				{
					currText += words[i] + " ";
				}
				this.inputText.setText(currText + CURSOR);
			}
		}
		else
		{
			this.inputText.setText(currText.substring(0,currText.length()-1) + CURSOR);
		}
		
		this.wasSwiped = false;
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
	 * Shows the frame.
	 */
	public static void showGUI(MainWindow mw)
	{
		//TODO change title
		JFrame frame = new JFrame("MEWSIK");
		frame.setMinimumSize(new Dimension(510, 525));
//		frame.setMinimumSize(new Dimension(1020, 800));
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(mw);
		frame.setJMenuBar(mw.getMenuBar());
		frame.setVisible(true);
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
				showGUI(mw);
//				mw.getWordFromSwipes("_WA_ASDRT_TRE_ER_");
//				mw.getWordFromSwipes("_IUYTFDS_");
//				mw.getWordFromSwipes("_GHJUIO_OIUHGFD_");
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
	private final Keyboard k;
	
	public KeyboardListener(MainWindow mw, Keyboard k)
	{
		this.mw = mw;
		this.k = k;
	}

	//TODO FIGURE OUT HOW TO SIMULATE CLICK
	@Override
	public void keyTyped(KeyEvent e)
	{
		char c = Character.toUpperCase(e.getKeyChar());
		if(Character.isAlphabetic(c) || c == ' ')
		{
			this.mw.addText(c);
			this.k.simulateKeyPress(c);
		}
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
		this.jta.setBorder(BorderFactory.createCompoundBorder(MainWindow.EMPTY_BORDER, BorderFactory.createLineBorder(Color.BLACK)));
		this.keyboard.setBorder(BorderFactory.createCompoundBorder(MainWindow.EMPTY_BORDER, BorderFactory.createLineBorder(Color.BLACK)));
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		this.jta.setBorder(BorderFactory.createCompoundBorder(MainWindow.EMPTY_BORDER, BorderFactory.createEtchedBorder(EtchedBorder.RAISED)));
		this.keyboard.setBorder(MainWindow.EMPTY_BORDER);
	}
}

class ViewLevenshteinDistanceMenuListener implements ActionListener
{
	private final Keyboard k;
	
	public ViewLevenshteinDistanceMenuListener(Keyboard k)
	{
		this.k = k;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JFrame levenshteinPopup = new JFrame("Levenshtein Distance");
		levenshteinPopup.add(new LevenshteinPopup(this.k.getLevenshteinSet()));
		levenshteinPopup.pack();
		levenshteinPopup.setResizable(false);
		levenshteinPopup.setVisible(true);
	}
}
