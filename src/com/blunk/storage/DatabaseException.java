package com.blunk.storage;

/**
 * An exception that is thrown when a database-related error occurs.
 * 
 * @author Nillus
 */
@SuppressWarnings("serial")
public class DatabaseException extends Exception
{
	/**
	 * Constructs a DatabaseException and sets a given message.
	 * 
	 * @param message The message string to throw with the DatabaseException, typically informing
	 * why (LOLWUT) the exception was thrown.
	 */
	public DatabaseException(String message)
	{
		super(message);
	}
}
