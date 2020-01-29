package com.blunk.communication;

import com.blunk.Log;
import com.blunk.util.CharArrayUtil;

/**
 * ClientMessage represents a network message sent from the Habbo client to the server.
 * ClientMessage provides methods for identifying the message and reading it's body.
 * 
 * @author Nillus
 */
public class ClientMessage
{
	private final String type;
	private final char[] body;
	private int bodyCursor;
	
	/**
	 * Constructs a ClientMessage object that represents a received message.
	 * 
	 * @param type The type of the message that was sent, eg, VERSIONCHECK, GET_CREDITS, STATUSOK
	 * etc.
	 * @param body The array of characters representing the body of the message.
	 */
	public ClientMessage(String type, char[] body)
	{
		// Set type
		if (type == null)
			type = "";
		this.type = type;
		
		// Set body
		if (body == null)
			body = new char[0];
		this.body = body;
		
		this.bodyCursor = 0; // ohrly
	}
	
	/**
	 * Resets the messagebody cursor, so the message can be read from the start again.
	 */
	public void reset()
	{
		bodyCursor = 0;
	}
	
	/**
	 * Returns the type of this message. (eg, VERSIONCHECK, GET_CREDITS, STATUSOK etc.)
	 */
	public String getType()
	{
		return this.type;
	}
	
	/**
	 * Returns the total body of this message as a string.
	 */
	public String getBody()
	{
		return new String(this.body);
	}
	
	/**
	 * Returns the remaining body of this message as a string. An empty string is returned if there
	 * is no body (anymore).
	 */
	public String getRemainingBody()
	{
		int remaining = this.remainingBodyLength();
		String s;
		
		if (remaining > 0)
		{
			s = String.valueOf(this.body, this.bodyCursor, remaining);
			this.bodyCursor += remaining;
		}
		else
		{
			s = "";
		}
		
		return s;
	}
	
	/**
	 * Finds the index of the next occurrence of a given character in the character array of the
	 * remaining message body. -1 is returned if the char is not found in the remaining message
	 * body.
	 * 
	 * @param c The character to find the index of.
	 */
	private int remainingBodyIndexOf(char c)
	{
		for (int i = this.bodyCursor; i < body.length; i++)
		{
			if (body[i] == c)
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Gets the next argument in the message body as a string. (ending with a whitespace character)
	 * 
	 * @see nextArgument(char delimiter)
	 */
	public String nextArgument()
	{
		return nextArgument(' ');
	}
	
	/**
	 * Gets the next argument in the message body as a string.
	 * 
	 * @param delimiter The character to read 'to'. TODO: fix this crappy doc
	 */
	public String nextArgument(char delimiter)
	{
		int remaining = (this.body.length - this.bodyCursor);
		if (remaining <= 0)
			return "";
		else
		{
			// "arg1 arg2 arg3"
			int delimiterIndex = this.remainingBodyIndexOf(delimiter);
			if (delimiterIndex == -1) // All remaining data!
			{
				return this.getRemainingBody();
			}
			else
			{
				String arg = new String(this.body, this.bodyCursor, (delimiterIndex - this.bodyCursor));
				
				this.bodyCursor += arg.length() + 1;
				return arg;
			}
		}
	}
	
	/**
	 * Returns the total, original length of the message body.
	 */
	public int bodyLength()
	{
		return this.body.length;
	}
	
	/**
	 * Returns the length of the remaining message body.
	 */
	public int remainingBodyLength()
	{
		return this.body.length - this.bodyCursor;
	}
	
	/**
	 * Returns the string representation of this ClientMessage.
	 */
	public String toString()
	{
		if (bodyLength() > 0)
			return getType() + " " + getBody();
		
		return getType();
	}
	
	/**
	 * Attempts to parse a ClientMessage object from a given array of unicode characters.
	 * 
	 * @param data The input array of characters.
	 * @return The ClientMessage object if parsing succeeded. Null is returned if parsing fails for
	 * whatever reason.
	 */
	public static ClientMessage parse(char[] data)
	{
		try
		{
			// Find first index of whitespace in data
			int indexOfWhiteSpace = CharArrayUtil.indexOfChar(data, 0, data.length, ' ');
			if (indexOfWhiteSpace != -1) // This ClientMessage has body
			{
				String msgType = new String(data, 0, indexOfWhiteSpace);
				char[] msgBody = CharArrayUtil.chompArray(data, indexOfWhiteSpace + 1, data.length);
				
				return new ClientMessage(msgType, msgBody);
			}
			else
			{
				// This ClientMessage has no body, stackData = msgtype
				return new ClientMessage(new String(data), null);
			}
		}
		catch (Exception ex)
		{
			Log.error("Failed to parse ClientMessage object", ex);
			return null;
		}
	}
}