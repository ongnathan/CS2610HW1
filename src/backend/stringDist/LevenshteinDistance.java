package backend.stringDist;

import backend.dictionary.Dictionary;

public class LevenshteinDistance
{
	public static final String INSERTION = "+";
	public static final String DELETION = "-";
	public static final String SAME = "=";
	
	//do not use
	public LevenshteinDistance()
	{
		
	}
	
	public static String getRealInputFromCombo(String comboString)
	{
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
	}
	
	public static String levenshteinDistance(String comboString, String finalString)
	{
		if(comboString.isEmpty())
		{
			return "";
		}
		//validity check
		for(int i = 1; i < finalString.length(); i++)
		{
			if(!comboString.contains(finalString.substring(i-1,i)))
			{
				if(finalString.substring(i-1,i).equals(" "))
				{
					continue;
				}
				throw new IllegalArgumentException("Error, \'" + finalString.substring(i-1,i) + "\' is not in the comboString.");
			}
		}
		
		//recreate input string
		String input = getRealInputFromCombo(comboString);
		
		//we know the order of both strings are kept.  The Levenshtein Distance is only calculated by deletions, or insertions for doubles.
		int finalStringCounter = 0;
		String levenshtein = "";
		if(finalString.charAt(0) == ' ')
		{
			levenshtein += INSERTION+" ";
			finalStringCounter++;
		}
		
		for(int i = 0; i < input.length(); i++)
		{
			char fromInput = input.charAt(i);
			char fromFinal = finalString.charAt(finalStringCounter);
			if(fromInput == fromFinal)
			{
				levenshtein += SAME+fromInput;
				finalStringCounter++;
				//check for doubles
				if(finalStringCounter >= finalString.length())
				{
					break;
				}
				if(fromInput == finalString.charAt(finalStringCounter))
				{
					levenshtein += INSERTION+fromInput;
					finalStringCounter++;
					if(finalStringCounter >= finalString.length())
					{
						break;
					}
				}
				continue;
			}
			levenshtein += DELETION+fromInput;
		}
		
		return levenshtein;
	}
	
	public static int calculateLevenshteinDistance(String levenshteinString)
	{
		int distance = 0;
		for(int i = 0; i < levenshteinString.length(); i++)
		{
			char c = levenshteinString.charAt(i);
			if(c == '+' || c == '-')
			{
				distance++;
			}
		}
		return distance;
	}
}
