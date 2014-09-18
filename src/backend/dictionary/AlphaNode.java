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
	
	public boolean isWord(String word)
	{
		//FIXME
		return false;
	}
	
	public boolean addWord(String word)
	{
		word = word.trim().toUpperCase();
		if(word.length() == 0)
		{
			return false;
		}
		return this.addSuffix(word);
	}
	
	private boolean addSuffix(String suffix)
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
}
