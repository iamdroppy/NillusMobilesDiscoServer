package com.blunk;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.*;

/**
 * Log is a static class for printing all kinds of information to the standard output stream.
 * 
 * @author Nillus & Mike
 */
public class Log
{
	private static SimpleDateFormat formatter = new SimpleDateFormat();
	private static FileWriter fileWriter;
	private static BufferedWriter fileOut;
	
	// Default log settings
	public static boolean logInfoToFile = false;
	public static boolean logErrors = true;
	public static boolean logErrorsToFile = false;
	public static boolean logDebug = false;
	
	/**
	 * Init Logging - Constructs the class and sets up the Writer for output to file.
	 */
	public static void init()
	{
		SimpleDateFormat pFormatter = new SimpleDateFormat("dd-MM-yyyy");
		String tFileName = pFormatter.format(new Date()) + ".log";
		
		try
		{
			fileWriter = new FileWriter(tFileName);
			fileOut = new BufferedWriter(fileWriter);
			
			fileOut.write("Server logging started @ " + getNow() + "\r\n");
			fileOut.flush();
		}
		catch (Exception ex)
		{
			logInfoToFile = false;
			logErrorsToFile = false;
			
			error("Could not set file output stream (logging) to '" + tFileName + "'!", ex);
		}
	}
	
	/**
	 * Returns the current date and time as a string.
	 */
	private static String getNow()
	{
		return formatter.format(new Date());
	}
	
	/**
	 * Appends a String to the current file output stream.
	 * 
	 * @param str The String to append.
	 */
	private static void appendStringToFile(String str)
	{
		try
		{
			fileOut.write(str);
			fileOut.flush();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private static String stackTraceToString(Exception ex)
	{
		StringWriter sw = new StringWriter();
		if (ex != null)
		{
			ex.printStackTrace(new PrintWriter(sw));
		}
		return sw.toString();
	}
	
	/**
	 * Prints the current date and time and a given string to the standard output stream.
	 * 
	 * @param eventMsg The text to print.
	 * @throws IOException
	 */
	public static void info(String eventMsg)
	{
		System.out.println(getNow() + " -- " + eventMsg);
		
		if (logInfoToFile)
		{
			appendStringToFile(getNow() + " -- " + eventMsg + "\r\n");
		}
	}
	
	/**
	 * Prints the current date and time and a given error message to the standard output stream.
	 * 
	 * @param eventMsg The 'human' description of the error to print.
	 */
	public static void error(String eventMsg)
	{
		error(eventMsg, null);
	}
	
	/**
	 * Prints the current date and time, a given error message and the string representation of a
	 * given exception to the standard output stream.
	 * 
	 * @param eventMsg The 'human' description of the error to print.
	 * @param ex The Exception object that contains all information about the exception.
	 */
	public static void error(String eventMsg, Exception ex)
	{
		System.out.println(getNow() + " -- ## ERROR: " + eventMsg);
		if (ex != null)
			ex.printStackTrace();
		
		if (logErrorsToFile)
		{
			appendStringToFile(getNow());
			if (ex != null)
				appendStringToFile(stackTraceToString(ex) + "\r\n");
		}
	}
	
	public static void debug(Object obj)
	{
		if (logDebug && obj != null)
			System.out.println(getNow() + " -- ## DEBUG: " + obj.toString());
	}
}
