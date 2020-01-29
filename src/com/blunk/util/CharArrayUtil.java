package com.blunk.util;

public class CharArrayUtil
{
	public static int indexOfChar(char[] array, int start, int stop, char seek)
	{
		for (int i = start; i < stop; i++)
		{
			if (array[i] == seek)
				return i;
		}
		
		return -1;
	}
	
	public static char[] chompArray(char[] array, int start, int stop)
	{
		char[] newArray = new char[stop - start];
		for (int i = 0, j = start; j < stop; i++, j++)
		{
			newArray[i] = array[j];
		}
		
		return newArray;
	}
}
