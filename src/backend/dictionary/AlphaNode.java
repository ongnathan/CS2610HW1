package backend.dictionary;

public class AlphaNode implements Comparable<AlphaNode>
{
	protected final char character;
	protected boolean isWord;
	protected double usage;
	protected String word;
	
	protected final AlphaNode parent;
	protected final AlphaNode[] nextLetters;
	
	protected AlphaNode()
	{
		this.character = '?';
		this.isWord = false;
		this.nextLetters = new AlphaNode[26];
		this.parent = null;
		this.usage = Double.NaN;
		this.word = null;
	}
	
	public AlphaNode(char character, AlphaNode parent)
	{
		this(character, false, Double.NaN, null, parent);
	}
	
	public AlphaNode(char character, boolean isWord, double usage, String word, AlphaNode parent)
	{
		if(!Character.isAlphabetic(character) || parent == null)
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
	}
	
	protected boolean addSuffix(String suffix, double usage, String wholeWord)
	{
		if(suffix.charAt(0) != this.character)
		{
			return false;
		}
		
		suffix = suffix.substring(1, suffix.length());
		
		if(suffix.isEmpty())
		{
			this.isWord = true;
			this.usage = usage;
			this.word = wholeWord;
			return true;
		}
		
		int index = suffix.charAt(0) - 'A';
		if(this.nextLetters[index] == null)
		{
			this.nextLetters[index] = new AlphaNode(suffix.charAt(0),this);
		}
		return this.nextLetters[index].addSuffix(suffix, usage, wholeWord);
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
	
	public AlphaNode getParent()
	{
		return this.parent;
	}
	
	public AlphaNode getChild(char c)
	{
		return this.nextLetters[Character.toUpperCase(c) - 'A'];
	}
	
	public boolean isWord()
	{
		return this.isWord;
	}
	
	public double getUsage()
	{
		return this.usage;
	}
	
	public String getWord()
	{
		return this.word;
	}
	
	//Their priority
	@Override
	public int compareTo(AlphaNode o)
	{
		if(this.usage == o.usage)
		{
			return 0;
		}
		return this.usage > o.usage ? 1 : -1;
	}
}
