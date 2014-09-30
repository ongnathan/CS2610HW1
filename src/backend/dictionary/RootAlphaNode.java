/**
 * 
 */
package backend.dictionary;

/**
 * The root node has special properties that requires it to be an extension of AlphaNode.
 * @author Nathan Ong and Jose Joseph
 * @see backend.dictionary.AlphaNode
 */
public class RootAlphaNode extends AlphaNode
{
	/**
	 * The constructor.
	 */
	public RootAlphaNode()
	{
		super();
	}
	
	/**
	 * Adds a word to the root.
	 * @param word The word to add to the tree.
	 * @param usage The frequency of the word.
	 * @return Returns whether or not adding the word was successful.
	 */
	public boolean addWord(String word, double usage)
	{
		word = word.trim().toUpperCase();
		if(word.length() == 0)
		{
			return false;
		}
		int index = word.charAt(0) - 'A';
		if(super.nextLetters[index] == null)
		{
			super.nextLetters[index] = new AlphaNode(word.charAt(0), this);
		}
		return super.nextLetters[index].addSuffix(word, usage, word);
	}
	
	/**
	 * Checks whether or not the given word is part of the tree.
	 * @param word The String to check
	 * @return Returns a boolean representing whether or not the String is part of the dictionary tree.  Returns false if it is not.
	 */
	public boolean isWord(String word)
	{
		word = word.trim().toUpperCase();
		if(word.length() == 0)
		{
			return false;
		}
		return super.isSuffix(word);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AlphaNode getChild(char c)
	{
		if(c == ' ')
		{
			return new AlphaNode(' ', this);
		}
		return super.getChild(c);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AlphaNode getNode(String str)
	{
		str = str.trim().toUpperCase();
		if(str.length() == 0)
		{
			return this;
		}
		int index = str.charAt(0) - 'A';
		if(super.nextLetters[index] == null)
		{
			return null;
		}
		return super.nextLetters[index].getNode(str);
	}
}//end class
