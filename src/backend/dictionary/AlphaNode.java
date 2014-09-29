package backend.dictionary;

/**
 * The node for the dictionary structure.
 * @author Nathan Ong and Jose Joseph
 */
public class AlphaNode implements Comparable<AlphaNode>
{
	/**
	 * The character that this node represents.
	 */
	public final char character;
	
	/**
	 * The list of children based on their characters, if they have been added.
	 */
	protected final AlphaNode[] nextLetters;
	
	private boolean isWord;			//whether or not this node represents a word
	private double usage;			//the frequency of this word
	private String word;			//the full word that this node represents
	
	private final AlphaNode parent;	//the parent node
	
	/**
	 * The default constructor.
	 * Should only be used by the RootAlphaNode.
	 * @see backend.dictionary.RootAlphaNode
	 */
	protected AlphaNode()
	{
		this.character = '?';
		this.isWord = false;
		this.nextLetters = new AlphaNode[26];
		this.parent = null;
		this.usage = Double.NaN;
		this.word = null;
	}
	
	/**
	 * The constructor.  Takes in a character and the preceding character's node in the word.
	 * @param character The character that this node represents.
	 * @param parent The character that precedes this one.
	 */
	public AlphaNode(char character, AlphaNode parent)
	{
		this(character, false, Double.NaN, null, parent);
	}
	
	/**
	 * The constructor.  Takes in a character, whether or not the node marks a word, its frequency usage as a double, the word that the node represents, and the preceding character's node.
	 * @param character The character that this node represents.
	 * @param isWord Whether or not this node represents the end of a word.
	 * @param usage The frequency of this word.
	 * @param word The word that this node represents the ending character of.
	 * @param parent The character that precedes this one.
	 */
	public AlphaNode(char character, boolean isWord, double usage, String word, AlphaNode parent)
	{
		if(!(Character.isAlphabetic(character) || character == ' ') || parent == null)
		{
			throw new IllegalArgumentException("Illegal arguments to AlphaNode");
		}
		this.parent = parent;
		this.nextLetters = new AlphaNode[26];
		this.character = Character.toUpperCase(character);
		this.isWord = isWord;
		this.usage = usage;
		this.word = word;
	}
	
	/**
	 * Checks to see if the given suffix leads to a word down this branch.
	 * This is a recursive function.
	 * @param suffix The String representing the suffix.
	 * @return Whether or not the given suffix leads to a word.  Returns false if the suffix does not lead to a word.
	 */
	protected boolean isSuffix(String suffix)
	{
		if(suffix.charAt(0) != this.character)
		{
			return false;
		}
		
		suffix = suffix.substring(1, suffix.length());
		
		if(suffix.isEmpty())
		{
			return this.isWord;
		}
		
		int index = suffix.charAt(0) - 'A';
		if(this.nextLetters[index] == null)
		{
			return false;
		}
		return this.nextLetters[index].isSuffix(suffix);
	}//end method(String)
	
	/**
	 * Adds the suffix or changes the status of the suffix to become a word. 
	 * This is a recursive function.
	 * @param suffix The String representing the suffix.
	 * @param usage The frequency of the word.
	 * @param wholeWord the entire word that this suffix comes from.
	 * @return Whether or not adding the suffix was successful.
	 */
	protected boolean addSuffix(String suffix, double usage, String wholeWord)
	{
		if(suffix.charAt(0) != this.character)
		{
			return false;
		}
		
		suffix = suffix.substring(1, suffix.length());
		
		if(suffix.isEmpty())
		{
			if(!this.isWord)
			{
				this.isWord = true;
				this.usage = usage;
				this.word = wholeWord;
			}
			return true;
		}
		
		int index = suffix.charAt(0) - 'A';
		if(this.nextLetters[index] == null)
		{
			this.nextLetters[index] = new AlphaNode(suffix.charAt(0),this);
		}
		return this.nextLetters[index].addSuffix(suffix, usage, wholeWord);
	}//end method(String, double, String)
	
	/**
	 * Retrieves the node representing the leaf or branch of the given string in the dictionary tree.
	 * This function is recursive.
	 * @param str The String to search with.
	 * @return Returns the AlphaNode representing the node of the tree.  Returns null if no node exists.
	 */
	protected AlphaNode getNode(String str)
	{
		if(str.charAt(0) != this.character)
		{
			return null;
		}
		
		str = str.substring(1, str.length());
		
		if(str.isEmpty())
		{
			return this;
		}
		
		int index = str.charAt(0) - 'A';
		if(this.nextLetters[index] == null)
		{
			return null;
		}
		return this.nextLetters[index].getNode(str);
	}//end method(String)
	
	/**
	 * Gets the parent node.
	 * @return Returns the parent node.
	 */
	public AlphaNode getParent()
	{
		return this.parent;
	}
	
	/**
	 * Gets the child node based on the given character, if it exists.
	 * @param c The character to move to.
	 * @return Returns the AlphaNode representing the child with the given character.  Returns null if no such character exists.
	 */
	public AlphaNode getChild(char c)
	{
		return this.nextLetters[Character.toUpperCase(c) - 'A'];
	}
	
	/**
	 * Checks to see if this node represents the end of a word.
	 * @return Returns whether or not this node represents the end of a word.  Returns false if it has not been given
	 */
	public boolean isWord()
	{
		return this.isWord;
	}
	
	/**
	 * Retrieves the frequency of this word.
	 * @return Returns the double representing the frequency of the word.  Returns Double.NaN if this node does not indicate a word, or if frequencies are not used.
	 */
	public double getUsage()
	{
		return this.usage;
	}
	
	/**
	 * Retrieves the word that this node represents.
	 * @return Returns the String representing the word that this node represents.  Returns null if this word does not exist in this dictionary.
	 */
	public String getWord()
	{
		return this.word;
	}
	
	/**
	 * {@inheritDoc}
	 * This method changes the compareTo to allow frequency to determine priority ordering.
	 */
	@Override
	public int compareTo(AlphaNode o)
	{
		if(this.usage == o.usage)
		{
			return 0;
		}
		return this.usage > o.usage ? 1 : -1;
	}
}//end class
