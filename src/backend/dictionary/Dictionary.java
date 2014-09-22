package backend.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Dictionary
{
	private RootAlphaNode root;
	
	public static final String DELIMITER = "_";
	
	public Dictionary()
	{
		this.root = new RootAlphaNode();
	}
	
	public boolean addWord(String word, double usage)
	{
		return this.root.addWord(word, usage);
	}
	
	public void addWord(String[] words, Double[] usages)
	{
		for(int i = 0; i < words.length; i++)
		{
			this.addWord(words[i], usages[i].doubleValue());
		}
	}
	
	public void addWord(Collection<String> words, Collection<Double> usages)
	{
		this.addWord(words.toArray(new String[0]), usages.toArray(new Double[0]));
	}
	
	/**
	 * String style: "-LETERS-SMORE-"
	 * The letters that are guaranteed to be in the word: "LSE" because they are next to the -.
	 * The String should not contain repeated letters inside each group.
	 * @param letterGroups
	 * @return
	 */
	public PriorityQueue<AlphaNode> getPotentialWords(String letterGroups)
	{
		if(!letterGroups.contains(DELIMITER))
		{
			throw new IllegalArgumentException("Please use the " + DELIMITER + " as a delimiter");
		}
		
		letterGroups = letterGroups.trim().toUpperCase();
		String[] letterPhrasesTemp = letterGroups.split(DELIMITER);
//		System.out.println(letterPhrasesTemp.length);
		String[] letterPhrases = new String[letterPhrasesTemp.length-1]; 
		System.arraycopy(letterPhrasesTemp, 1, letterPhrases, 0, letterPhrasesTemp.length-1);
		for(int i = 0; i < letterPhrases.length-1; i++)
		{
			if(letterPhrases[i].charAt(letterPhrases[i].length()-1) != letterPhrases[i+1].charAt(0))
			{
				throw new IllegalArgumentException("The string does not follow the correct format");
			}
		}
		
		PriorityQueue<AlphaNode> potentialWords = new PriorityQueue<AlphaNode>(100, Collections.reverseOrder());
		
		Queue<AlphaNode> potentialNodes = new LinkedList<AlphaNode>();
		potentialNodes.add(this.root);
		
		for(int i = 0; i < letterPhrases.length; i++)
		{
			int phrasePosition = -1;
			if(letterPhrases.length == 1)
			{
				phrasePosition = 3;
			}
			else if(i == 0)
			{
				phrasePosition = 0;
			}
			else if(i == letterPhrases.length-1)
			{
				phrasePosition = 2;
			}
			else
			{
				phrasePosition = 1;
			}
			String[] potentialStrings = listPotentialString(letterPhrases[i], phrasePosition);
			
			Queue<AlphaNode> newPotentialNodes = new LinkedList<AlphaNode>();
			while(!potentialNodes.isEmpty())
			{
				AlphaNode currentNode = potentialNodes.remove();
				for(String potentialString : potentialStrings)
				{
					AlphaNode potential = currentNode.getNode(potentialString);
					if(potential == null)
					{
						continue;
					}
					newPotentialNodes.add(potential);
					if(i == letterPhrases.length-1 && potential.isWord())
					{
						System.out.println(potential.word + " " + potential.usage);
						potentialWords.add(potential);
					}
				}
			}
			potentialNodes = newPotentialNodes;
		}
		
		return potentialWords;
	}
	
	private static String[] listPotentialString(String str, int position)
	{
		if(str.length() == 2)
		{
			return new String[]{str};
		}
		
		String first = String.valueOf(str.charAt(0));
		String last = String.valueOf(str.charAt(str.length()-1));
		
		String[] possibleList = listAllStrings(str.substring(1,str.length()-1));
		
//		int indexOfMult = ((position == 0 || position == 2) ? 2 : ((position == 1) ? 1 : 4));
		int indexOfMult = (position == 1 || position == 2) ? 2 : 4;
		
		String[] potentials = new String[possibleList.length*indexOfMult];
		
		for(int i = 0; i < possibleList.length; i++)
		{
			String fromPossibleList = possibleList[i];
			switch(position)
			{
//				//Front
//				case 0:
//					potentials[indexOfMult*i] = first + fromPossibleList + last;
//					potentials[indexOfMult*i+1] = first + first + fromPossibleList + last;
//					break;
				//Middle
				case 1:
//					potentials[i] = first + fromPossibleList + last;
//					break;
				//End
				case 2:
					potentials[indexOfMult*i] = first + fromPossibleList + last;
					potentials[indexOfMult*i+1] = first + fromPossibleList + last + last;
					break;
				//Front
				case 0:
				//Both Front and End
				case 3:
					potentials[indexOfMult*i] = first + fromPossibleList + last;
					potentials[indexOfMult*i+1] = first + first + fromPossibleList + last;
					potentials[indexOfMult*i+2] = first + fromPossibleList + last + last;
					potentials[indexOfMult*i+3] = first + first + fromPossibleList + last + last;
			}
		}
		
		return potentials;
	}
	
	private static String[] listAllStrings(String str)
	{
		if(str.length() == 0)
		{
			return new String[]{""};
		}
		if(str.length() == 1)
		{
			return new String[]{"", str, str+str};
		}
		
		String[] list = new String[(int) Math.pow(3, str.length())];
		int index = 0;
		String firstChar = String.valueOf(str.charAt(0));
		String[] subList = listAllStrings(str.substring(1,str.length()));
		for(String subElement : subList)
		{
			list[index] = subElement;
			list[index+1] = firstChar + subElement;
			list[index+2] = firstChar + firstChar + subElement;
			index+=3;
		}
		
		return list;
	}
	
	public static Dictionary importFromTextFile(String filename)
	{
		return importFromTextFile(new File(filename));
	}
	
	public static Dictionary importFromTextFile(File file)
	{
		if(!file.exists() || !file.isFile())
		{
			throw new IllegalArgumentException("Invalid file " + file.getAbsolutePath());
		}
		
		BufferedReader reader;
		Dictionary d = new Dictionary();
		
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while(line != null)
			{
				line = line.trim();
				if(line.isEmpty())
				{
					line = reader.readLine();
					continue;
				}
				
				String[] split = line.split(" ");
				split[1] = split[1].toUpperCase();
				
				if(split[1].matches("[A-Z]+"))
				{
//					System.out.println(split[1]);
					if(!d.addWord(split[1], Double.parseDouble(split[0])))
					{
						reader.close();
						throw new IllegalStateException("PROBLEM HERE");
					}
				}
				
				line = reader.readLine();
			}
			
			reader.close();
		}
		catch(FileNotFoundException e)
		{
			throw new IllegalStateException("Something went wrong");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(NumberFormatException e)
		{
			throw new IllegalArgumentException(e);
		}
		
		return d;
	}
	
	public static void main(String[] args)
	{
		Dictionary d = Dictionary.importFromTextFile("all.num"); //http://www.kilgarriff.co.uk/bnc-readme.html#raw
//		d.addWord("WATER", 15);
//		d.addWord("WAITER", 5);
//		d.addWord("WATTER", 10);
		String test = "_WA_ASDRT_TRE_ER_";
//		String[] testStuff = listPotentialString(test,3);
//		Arrays.sort(testStuff, new DifferentStringComparator());
		PriorityQueue<AlphaNode> testStuff = d.getPotentialWords(test);
		while(!testStuff.isEmpty())
		{
			System.out.println(testStuff.remove().getWord());
		}
	}
}

class DifferentStringComparator implements Comparator<String>
{
	@Override
	public int compare(String o1, String o2)
	{
		if(o1.length() != o2.length())
		{
			return o1.length() - o2.length();
		}
		return o1.compareTo(o2);
	}
}