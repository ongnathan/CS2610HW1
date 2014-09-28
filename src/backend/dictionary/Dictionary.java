package backend.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Acts as the dictionary for the program.
 * This dictionary also is able to parse swiped values.
 * @author Nathan Ong and Jose Joseph
 */
public class Dictionary
{
	/**
	 * The delimiter that should be used whenever a swiped String is going to be passed in.
	 */
	public static final char DELIMITER = '_';
	
	private RootAlphaNode root;	//The root node of the dictionary tree
	
	/**
	 * The constructor.
	 */
	public Dictionary()
	{
		this.root = new RootAlphaNode();
	}
	
	/**
	 * Adds a word to the dictionary.
	 * @param word The word.
	 * @param usage The frequency of the word.
	 * @return Returns whether or not adding the word was successful.  Returns false if the word could not be added for some reason.
	 */
	public boolean addWord(String word, double usage)
	{
		return this.root.addWord(word, usage);
	}
	
	/**
	 * Adds an array of words to the dictionary.
	 * @param words The array of words.
	 * @param usages The corresponding array of frequencies that should be the same size as the array of words.
	 */
	public void addWord(String[] words, Double[] usages)
	{
		for(int i = 0; i < words.length; i++)
		{
			this.addWord(words[i], usages[i].doubleValue());
		}
	}
	
	/**
	 * Adds a list of words to the dictionary and their given frequencies.
	 * @param words The list of words.
	 * @param usages The list of frequencies.
	 */
	public void addWord(List<String> words, List<Double> usages)
	{
		this.addWord(words.toArray(new String[0]), usages.toArray(new Double[0]));
	}
	
	/**
	 * Retrieves the potential words given by the swiped String.
	 * The String must be of a particular format, surrounded by the DELIMITER at the very least.
	 * Example: "_WA_ASDRT_TRE_ER_" indicates that the letters in the set {W,A,T,E,R} are in the word in that particular order, and the letters in between indicate letters that may or may not be in the word.
	 * (Note that the delimiter used in this example is "_".)
	 * Ordering is set in that phrases are in given order and the letters within each phrase (those that may or may not be in the word) also retain their order.
	 * The queue returns all words that fit the given requirement in frequency order.
	 * If frequency is not used, then it is returned in the order which they were found in the dictionary.
	 * 
	 * With the given example, the words that the queue should return in the given dictionary is {WATER, WASTER, WATTER, WARTER} in frequency order.
	 * 
	 * The String should not contain repeated letters inside each phrase, nor should it contain a space.  Spaces are word delimiters and should be handled differently
	 * 
	 * @param letterGroups The swiped String.
	 * @return Returns a PriorityQueue with all the words that satisfy the swiped String in highest-frequency first ordering.
	 */
	public PriorityQueue<AlphaNode> getPotentialWords(String letterGroups)
	{
		//Delimiter usage checking
		if(!letterGroups.contains("" + DELIMITER))
		{
			throw new IllegalArgumentException("Please use the " + DELIMITER + " as a delimiter");
		}
		
		//prep the data for analysis
		letterGroups = letterGroups.trim().toUpperCase();
		String[] letterPhrasesTemp = letterGroups.split("" + DELIMITER);
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
		
		//hold the priority queue with the AlphaNodes representing words
		PriorityQueue<AlphaNode> potentialWords = new PriorityQueue<AlphaNode>(100, Collections.reverseOrder());
		
		//holds node positions within the dictionary
		Queue<AlphaNode> potentialNodes = new LinkedList<AlphaNode>();
		potentialNodes.add(this.root);
		
		//go through all of the phrases
		for(int i = 0; i < letterPhrases.length; i++)
		{
			//checks the phrase position and lists the possible strings based on these positions
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
			
			//filling the new temporary dictionary locations by checking all suffixes from previously held dictionary locations
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
//						System.out.println(potential.word + " " + potential.usage);
						potentialWords.add(potential);
					}
				}//end inner for loop
			}//end while loop
			potentialNodes = newPotentialNodes;
		}//end for loop
		
		return potentialWords;
	}
	
	/**
	 * Enumerates all potential strings, where the first and last characters MUST be in the String.
	 * Does not check the dictionary for validity.
	 * Handles double letter repetition as well.
	 * @param str The String to enumerate.
	 * @param position The position where it is found.  0 is the front, 1 is the middle, 2 is the end, and 3 is if there is only one phrase.
	 * @return Returns the list of potential String
	 */
	private static String[] listPotentialString(String str, int position)
	{
		//if there are only two characters in the string, then there can only be one possibility.
		if(str.length() == 1 || str.length() == 2)
		{
			return new String[]{str};
		}
		
		//separate the first and last characters.
		String first = String.valueOf(str.charAt(0));
		String last = String.valueOf(str.charAt(str.length()-1));
		
		//enumerate ALL possible strings without the first and last character
		String[] possibleList = listAllStrings(str.substring(1,str.length()-1));
		
		//determine the return list size
//		int indexOfMult = ((position == 0 || position == 2) ? 2 : ((position == 1) ? 1 : 4));
		int indexOfMult = (position == 1 || position == 2) ? 2 : 4;
		
		String[] potentials = new String[possibleList.length*indexOfMult];
		
		//add the first and last characters and their possible enumerations to the strings that were found without the first and last characters.
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
			}//end switch(position)
		}//end for loop
		
		return potentials;
	}//end static method(String, int)
	
	/**
	 * Enumerates all possible strings with the given characters in the string.
	 * Checks for characters that are not a part of the string and doubled characters.
	 * @param str The String to find all enumerations.
	 * @return Returns an array representing the enumerations of the strings.
	 */
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
	}//end method (String)
	
	/**
	 * Imports a dictionary from the given file name.
	 * @param filename The filename to import from.
	 * @return Returns the Dictionary that was generated by the file.
	 */
	public static Dictionary importFromTextFile(String filename)
	{
		return importFromTextFile(new File(filename));
	}
	
	/**
	 * Imports a dictionary from the given file.
	 * @param file The file to import from.
	 * @return Returns the Dictionary that was generated by the file.
	 */
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
			//prep the first line
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while(line != null)
			{
				//if the line is empty, skip
				line = line.trim();
				if(line.isEmpty())
				{
					line = reader.readLine();
					continue;
				}
				
				//first element is frequency, second element is the word
				String[] split = line.split(" ");
				split[1] = split[1].toUpperCase();
				
				//if there are non-alphabetic characters, ignore them.
				if(split[1].matches("[A-Z]+"))
				{
//					System.out.println(split[1]);
					//otherwise add them to the dictionary
					if(!d.addWord(split[1], Double.parseDouble(split[0])))
					{
						reader.close();
						throw new IllegalStateException("PROBLEM HERE");
					}
				}
				
				line = reader.readLine();
			}//end while
			
			reader.close();
		}//end try
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
	}//end static method(File)
	
	/**
	 * The main tester.
	 * @param args Unused.
	 */
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
}//end class

/**
 * Compares strings by length first before alphabetical order.
 * @author Nathan Ong
 */
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
}//end class