package backend.dictionary;

public class AlphaNode
{
	protected final char character;
	protected boolean isWord;
	
	protected final AlphaNode parent;
	protected final AlphaNode[] nextLetters;
	
	protected AlphaNode()
	{
		this.character = '?';
		this.isWord = false;
		this.nextLetters = new AlphaNode[26];
		this.parent = null;
	}
	
	public AlphaNode(char character, AlphaNode parent)
	{
		this(character, false, parent);
	}
	
	public AlphaNode(char character, boolean isWord, AlphaNode parent)
	{
		if(!Character.isAlphabetic(character) || parent == null)
		{
			throw new IllegalArgumentException("Illegal arguments to AlphaNode");
		}
		this.parent = parent;
		this.nextLetters = new AlphaNode[26];
		this.character = Character.toUpperCase(character);
		this.isWord = isWord;
	}
	
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
		return this.nextLetters[index].addSuffix(suffix);
	}
	
	protected boolean addSuffix(String suffix)
	{
		if(suffix.charAt(0) != this.character)
		{
			return false;
		}
		
		suffix = suffix.substring(1, suffix.length());
		
		if(suffix.isEmpty())
		{
			this.isWord = true;
			return true;
		}
		
		int index = suffix.charAt(0) - 'A';
		if(this.nextLetters[index] == null)
		{
			this.nextLetters[index] = new AlphaNode(suffix.charAt(0),this);
		}
		return this.nextLetters[index].addSuffix(suffix);
	}
	
	public AlphaNode getParent()
	{
		return this.parent;
	}
	
	public AlphaNode getChild(char c)
	{
		return this.nextLetters[Character.toUpperCase(c) - 'A'];
	}
	
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
	}
}
