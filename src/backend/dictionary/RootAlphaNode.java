/**
 * 
 */
package backend.dictionary;

/**
 * @author Nathan Ong
 *
 */
public class RootAlphaNode extends AlphaNode
{
	public RootAlphaNode()
	{
		super();
	}
	
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
	
	public boolean isWord(String word)
	{
		word = word.trim().toUpperCase();
		if(word.length() == 0)
		{
			return false;
		}
		return super.isSuffix(word);
	}
}
