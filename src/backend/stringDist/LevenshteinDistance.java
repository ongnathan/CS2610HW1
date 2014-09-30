package backend.stringDist;

import backend.dictionary.Dictionary;

/**
 * 
 * @author Nathan Ong and Jose Michael Joseph
 *
 */
public class LevenshteinDistance
{
	/**
	 * The character to denote insertions.
	 */
	public static final char INSERTION = '+';
	
	/**
	 * The character to denote deletions.
	 */
	public static final char DELETION = '-';
	
	/**
	 * The character to denote that no change was made.
	 */
	public static final char SAME = '=';
	
	/**
	 * Empty constructor.
	 * Do not use.
	 */
	public LevenshteinDistance()
	{
		
	}
	
	/**
	 * Returns the whole keyboard input based on the combination String.
	 * @param comboString The String representing the combination of keys in the Dictionary format.
	 * @return Returns a String representing a list of keys that was passed on the keyboard.
	 */
	public static String getRealInputFromCombo(String comboString)
	{
		//empty check
		if(comboString.isEmpty())
		{
			return "";
		}
		
		//recreating input string
		String[] comboSplit = comboString.split(""+Dictionary.DELIMITER);
		String input = comboSplit[0];
		int i = 0;
		if (comboSplit[0].isEmpty())
		{
			input += comboSplit[1];
			i++;
		}
		for (i++; i < comboSplit.length; i++)
		{
			comboSplit[i] = comboSplit[i].substring(1);
			input += comboSplit[i];
		}
		return input;
	}//end method(String)
	
	/**
	 * Computes the Levensthein Distance of the input characters and the final output.
	 * @param comboString The combination String.
	 * @param finalString The final String.
	 * @return Returns the String of the Levenshtein Distance computation.
	 */
	public static String levenshteinDistance(String comboString, String finalString)
	{
		if(comboString.isEmpty() || finalString.isEmpty())
		{
			return "";
		}
		
		//validity check
		for(int i = 1; i < finalString.length(); i++)
		{
			if(!comboString.contains(finalString.substring(i-1,i)))
			{
				if(finalString.charAt(i-1) == ' ')
				{
					continue;
				}
				throw new IllegalArgumentException("Error, \'" + finalString.substring(i-1,i) + "\' is not in the comboString.");
			}
		}
		
		//recreate input string
		String input = getRealInputFromCombo(comboString);
		
		//We know the order of both strings are kept.
		//The Levenshtein Distance is only calculated by insertions (for doubles) or deletions, since these are the only operations done on the input String.
		int finalStringCounter = 0;
		String levenshtein = "";
		
		//A space insertion
		if(finalString.charAt(0) == ' ')
		{
			levenshtein += INSERTION+" ";
			finalStringCounter++;
		}
		
		//Check all values in the String and figure out insertions and deletions.
		for(int i = 0; i < input.length(); i++)
		{
			char fromInput = input.charAt(i);
			char fromFinal = finalString.charAt(finalStringCounter);
			if(fromInput == fromFinal)
			{
				levenshtein += ""+SAME+fromInput;
				finalStringCounter++;
				//check for doubles
				if(finalStringCounter >= finalString.length())
				{
					break;
				}
				if(fromInput == finalString.charAt(finalStringCounter))
				{
					levenshtein += ""+INSERTION+fromInput;
					finalStringCounter++;
					if(finalStringCounter >= finalString.length())
					{
						break;
					}
				}
				continue;
			}//end if
			levenshtein += ""+DELETION+fromInput;
		}//end for
		
		return levenshtein;
	}//end method(String,String)
	
	/**
	 * Determines the number of operations based on the Levenshtein String. 
	 * @param levenshteinString The Levenshtein String as formatted by this class.
	 * @return Returns the number of operations that it took to change the String.
	 */
	public static int calculateLevenshteinDistance(String levenshteinString)
	{
		int distance = 0;
		for(int i = 0; i < levenshteinString.length(); i++)
		{
			char c = levenshteinString.charAt(i);
			if(c == INSERTION || c == DELETION)
			{
				distance++;
			}
		}
		return distance;
	}
}//end class
