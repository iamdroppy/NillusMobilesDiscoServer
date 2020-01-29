package com.blunk.communication;

public class ServerMessage
{
	private StringBuilder data;
	
	public ServerMessage()
	{
		data = new StringBuilder();
	}
	
	public ServerMessage(int initialCapacity)
	{
		data = new StringBuilder(initialCapacity);
	}
	
	public void clear()
	{
		data.setLength(0);
	}
	
	public void set(String msgType)
	{
		this.clear();
		this.data.append('#');
		this.data.append(msgType);
	}
	
	public void append(String s)
	{
		this.data.append(s);
	}
	
	public void appendArgument(String arg)
	{
		appendArgument(arg, ' ');
	}
	
	public void appendArgument(String arg, char delimiter)
	{
		this.data.append(delimiter);
		this.data.append(arg);
	}
	
	public String getResult()
	{
		data.append("##");
		return data.toString();
	}
}
