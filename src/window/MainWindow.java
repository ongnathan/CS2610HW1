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
	/**
	 * 
	 */
	private static final long serialVersionUID = 1485954960228722431L;

	private static final String CURSOR = "_";				//The cursor character
	
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
	
	private boolean wasSwiped;								//Whether or not the last word was swiped.
	
	private final JMenuBar menuBar;							//The menu bar
	private final JMenu view;								//The menu
	private final JMenuItem viewLevenshtein;				//The menu item to view the Levenshtein Distance
	
	/**
	 * An empty border.
	 */
	protected static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(20, 25, 20, 25);
	
	/**
	 * The constructor.
	 */
	public MainWindow()
	{
		super(new BorderLayout());
		//dictionary
		this.dictionary = Dictionary.importFromTextFile("all.num");
		this.wasSwiped = false;
		
		//main components
		this.inputArea = new JPanel(new GridLayout(3,1));
		
		//sample panel
		this.sample = new JTextArea("ACTIONS SPEAK LOUDER THAN WORDS", 1, 34);
		this.sample.setEditable(false);
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
		this.add(this.inputArea, BorderLayout.NORTH);
		this.add(this.keyboard, BorderLayout.CENTER);
		
		this.menuBar = new JMenuBar();
		this.view = new JMenu("View");
		this.viewLevenshtein = new JMenuItem("Levenshtein Distance Calculator");
		ViewLevenshteinDistanceMenuListener vldml = new ViewLevenshteinDistanceMenuListener(this.keyboard);
		this.viewLevenshtein.addActionListener(vldml);
		
		this.view.add(this.viewLevenshtein);
		this.menuBar.add(this.view);
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
	
	/**
	 * Gets the JMenuBar.
	 * @return Returns the JMenuBar associated with the window.
	 */
	public JMenuBar getMenuBar()
	{
		return this.menuBar;
	}
	
	/**
	 * Gets all the words associated with the list of combination strings.
	 * @param allGroupsOfPotentialKeys The array of Strings containing combination strings.
	 * @return Returns the whole output that was generated.
	 */
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
	 * @return Returns the output String.
	 */
	public String getWordFromSwipes(String potentialKeys)
	{
		String output = "";
		
		//if the keys is in the form "_X_" then just add the character.
		if(potentialKeys.length() == 3)
		{
			output = ""+potentialKeys.charAt(1);
			this.inputText.setText(this.inputText.getText().substring(0, this.inputText.getText().length()-1) + output + "_");
			return output;
		}
		
		//Check if there are any words
		PriorityQueue<AlphaNode> queue = this.dictionary.getPotentialWords(potentialKeys);
		if(queue.isEmpty())
		{
			//if not found, then display question marks
			this.inputText.setText(this.inputText.getText().substring(0, this.inputText.getText().length()-1) + " ???" + "_");
			this.wasSwiped = true;
			return output;
		}
		
		this.wasSwiped = true;
		//prepare for new words
		this.clearSuggestionButtons();
		String currentInputText = this.inputText.getText();
		
		//add the most frequent word to the text
		if(currentInputText.length() > 1 && currentInputText.charAt(currentInputText.length()-2) == ' ')
		{
			output = queue.remove().getWord();
		}
		else
		{
			output = " " + queue.remove().getWord();
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
		//remove the cursor
		String currText = this.inputText.getText().substring(0,this.inputText.getText().length()-1);
		if(currText.isEmpty())
		{
			return;
		}
		
		//if the word was swiped, remove the word
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
		}//end if
		//otherwise, remove a character
		else
		{
			this.inputText.setText(currText.substring(0,currText.length()-1) + CURSOR);
		}
		
		//the previous word, no matter if it was swiped or not, cannot be removed as a whole.
		this.wasSwiped = false;
		
		//clear the suggestions buttons
		this.clearSuggestionButtons();
	}//end method()
	
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
		
		//for the Levenshtein Distance
		this.keyboard.switchToWord(suggestion);
	}//end method(String)
	
	/**
	 * Shows the frame.
	 * @param mw The MainWindow to display.
	 */
	public static void showGUI(MainWindow mw)
	{
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
}//end class

/**
 * The suggestions buttons listener.
 * @author Nathan Ong and Jose Michael Joseph
 */
class SuggestionButtonListener implements ActionListener
{
	private final MainWindow mw;	//the parent main window
	
	/**
	 * The constructor.
	 * @param mw The parent MainWindow.
	 */
	public SuggestionButtonListener(MainWindow mw)
	{
		this.mw = mw;
	}
	
	/**
	 * {@inheritDoc}
	 * Switches the word to the new suggestion.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		this.mw.switchWordToSuggestion(e.getActionCommand());
	}
}//end class

/**
 * The keyboard listener.
 * @author Nathan Ong and Jose Michael Joseph
 */
class KeyboardListener implements KeyListener
{
	private final MainWindow mw;	//the parent main window
	private final Keyboard k;		//the keyboard
	
	/**
	 * The constructor.
	 * @param mw The parent MainWindow.
	 * @param k The Keyboard.
	 */
	public KeyboardListener(MainWindow mw, Keyboard k)
	{
		this.mw = mw;
		this.k = k;
	}
	
	/**
	 * {@inheritDoc}
	 * Only cares about alphabetic characters and the space.
	 */
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
	
	/**
	 * {@inheritDoc}
	 * Cares only about the backspace.
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			this.mw.backspace();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * Unused.
	 */
	@Override
	public void keyReleased(KeyEvent e)
	{
		//empty
	}
}//end class

/**
 * The focus listener.
 * Shows a border on the text area and the keyboard when focused.
 * @author Nathan Ong and Jose Michael Joseph
 */
class TextFocusBorderListener implements FocusListener
{
	private final JTextArea jta;	//The text area.
	private final JPanel keyboard;	//The keyboard.
	
	/**
	 * The constructor.
	 * @param jta The JTextArea input area.
	 * @param keyboard The keyboard.
	 */
	public TextFocusBorderListener(JTextArea jta, JPanel keyboard)
	{
		this.jta = jta;
		this.keyboard = keyboard;
	}
	
	/**
	 * {@inheritDoc}
	 * Focused gained changes the border.
	 */
	@Override
	public void focusGained(FocusEvent e)
	{
		this.jta.setBorder(BorderFactory.createCompoundBorder(MainWindow.EMPTY_BORDER, BorderFactory.createLineBorder(Color.BLACK)));
		this.keyboard.setBorder(BorderFactory.createCompoundBorder(MainWindow.EMPTY_BORDER, BorderFactory.createLineBorder(Color.BLACK)));
	}
	
	/**
	 * {@inheritDoc}
	 * Focused lost changes the border.
	 */
	@Override
	public void focusLost(FocusEvent e)
	{
		this.jta.setBorder(BorderFactory.createCompoundBorder(MainWindow.EMPTY_BORDER, BorderFactory.createEtchedBorder(EtchedBorder.RAISED)));
		this.keyboard.setBorder(MainWindow.EMPTY_BORDER);
	}
}//end class

/**
 * The menu listener for viewing the Levenshtein Distance.
 * @author Nathan Ong and Jose Michael Joseph
 */
class ViewLevenshteinDistanceMenuListener implements ActionListener
{
	private final Keyboard k;	//The Keyboard
	
	/**
	 * The constructor.
	 * @param k The Keyboard.
	 */
	public ViewLevenshteinDistanceMenuListener(Keyboard k)
	{
		this.k = k;
	}
	
	/**
	 * {@inheritDoc}
	 * Shows a popup computing the Levenshtein Distance.
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		JFrame levenshteinPopup = new JFrame("Levenshtein Distance");
		levenshteinPopup.add(new LevenshteinPopup(this.k.getLevenshteinSet()));
		levenshteinPopup.pack();
		levenshteinPopup.setResizable(false);
		levenshteinPopup.setVisible(true);
	}
}//end class
