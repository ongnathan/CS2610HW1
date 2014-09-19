package backend.dictionary;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class Dictionary
{
	private RootAlphaNode root;
	
	public Dictionary()
	{
		this.root = new RootAlphaNode();
	}
	
	public boolean addWord(String word)
	{
		return this.root.addWord(word);
	}
	
	public void addWord(String[] words)
	{
		for(String word : words)
		{
			this.addWord(word);
		}
	}
	
	public void addWord(Collection<String> words)
	{
		this.addWord(words.toArray(new String[0]));
	}
	
	/**
	 * String style: "$LETTERS$SMORE$"
	 * The letters that are guaranteed to be in the word: "LSE" because they are next to the $.
	 * @param letterGroups
	 * @return
	 */
	public Collection<String> getPotentialWords(String letterGroups)
	{
		if(!letterGroups.contains("$"))
		{
			throw new IllegalArgumentException("Please use the $ as a delimiter");
		}
		
		letterGroups = letterGroups.trim().toUpperCase();
		String[] letterPhrases = letterGroups.split("$");
		for(int i = 0; i < letterPhrases.length-1; i++)
		{
			if(letterPhrases[i].charAt(letterPhrases[i].length()-1) != letterPhrases[i+1].charAt(0))
			{
				throw new IllegalArgumentException("The string does not follow the correct format");
			}
		}
		
		Queue<AlphaNode> potentialNodes = new LinkedList<AlphaNode>();
		potentialNodes.add(this.root);
		
		for(int i = 0; i < letterPhrases.length; i++)
		{
			
			Queue<AlphaNode> newPotentialNodes = new LinkedList<AlphaNode>();
			for(AlphaNode treeNode : potentialNodes)
			{
				
			}
		}
	}
	
	private static String[] permutePotentialString(String str)
	{
		if(str.length() == 2)
		{
			return new String[]{str};
		}
		String[] potentials = new String[(int) Math.pow(2, str.length()-2)];
		
		char first = str.charAt(0);
		char last = str.charAt(str.length()-1);
		char[] possibleChars = str.substring(1,str.length()-1).toCharArray();
		
		//Possible lengths of strings
		for(int i = 2; i < str.length(); i++)
		{
			for()
		}
	}
}
