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
		return super.getNode(str);
	}
	
	public boolean addWord(String word)
	{
		word = word.trim().toUpperCase();
		if(word.length() == 0)
		{
			return false;
		}
		return super.addSuffix(word);
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
