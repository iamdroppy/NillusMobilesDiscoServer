package com.blunk.net;

import java.io.*;
import java.net.*;

import com.blunk.Log;

/**
 * TcpConnection is a network connection over TCP that is able to read and write data as a series of
 * characters.
 * 
 * @author Nillus
 */
public class TcpConnection implements Runnable
{
	/**
	 * The ID of this connection, representing the 'connection slot'.
	 */
	public int ID;
	/**
	 * The client ID, this ID is always unique during server runtime.
	 */
	public int clientID;
	
	/**
	 * The instance to the TcpConnectionManager that manages this connection.
	 */
	private TcpConnectionManager connMgr;
	/**
	 * The Thread that calls the 'run' method.
	 */
	private Thread connThread;
	
	/**
	 * The date and time this connection was used by a new connection request represented in
	 * milliseconds.
	 */
	private long inUseSinceMillis;
	private long killAtMillis; // -1: OK, -2: released, > 0: pending kill
	
	/**
	 * As long as this boolean is True, the run() method will keep looping.
	 */
	private boolean isAlive;
	/**
	 * The java.net.Socket representing the network connection between the server and the client.
	 */
	private Socket client;
	private BufferedReader dataIn;
	private PrintWriter dataOut;
	
	/**
	 * Constructs a TcpConnection with a given ID and a reference to a TcpConnectionManager.
	 * 
	 * @param ID The ID of this connection.
	 * @param clientID The client ID, this ID is always unique during server runtime.
	 * @param connMgr A reference to the TcpConnectionManager this TcpConnection belongs to.
	 */
	public TcpConnection(int connectionID, int clientID, TcpConnectionManager connMgr)
	{
		this.ID = connectionID;
		this.clientID = clientID;
		this.connMgr = connMgr;
		this.connThread = new Thread(this, "TcpConnection " + this.ID);
		this.killAtMillis = -1;
	}
	
	/**
	 * Assigns a Socket object to this TcpConnection and sets up the input and output data streams.
	 * 
	 * @param client
	 */
	public void setConnection(Socket client)
	{
		this.inUseSinceMillis = System.currentTimeMillis();
		this.client = client;
		this.isAlive = true;
		
		try
		{
			this.dataIn = new BufferedReader(new InputStreamReader(this.client.getInputStream(), "iso-8859-1"));
			this.dataOut = new PrintWriter(this.client.getOutputStream(), true);
		}
		catch (Exception ex)
		{
			Log.error("Error in handling new socket for connection " + this.ID + "!", ex);
			
			release();
			return;
		}

		// Start the connection worker
		connThread.start();
	}
	
	/**
	 * Disconnects the network connection and frees the connection slot in the TcpConnectionManager.
	 */
	public void release()
	{
		if (this.killAtMillis == -2)
			return; // Already done!
		this.killAtMillis = -2;
		
		// Do project specific stuff
		this.handleConnectionShutdown();
		
		// Stop connection worker
		this.isAlive = false;
		this.connThread.interrupt();
		this.connThread = null;
		
		// Try disconnect connection
		this.inUseSinceMillis = 0;
		if (this.client != null)
		{
			try
			{
				this.client.close();
				this.dataIn.close();
				this.dataOut.close();
			}
			catch (IOException ex)
			{
				Log.error("Error in closing socket of " + this.toString(), ex);
			}
			finally
			{
				this.client = null;
				this.dataIn = null;
				this.dataOut = null;
			}
		}
		
		// Free this connection slot
		this.connMgr.freeConnection(this.ID);
		this.connMgr = null;
	}
	
	/**
	 * Marks this connection for killing & releasing in a given amount of milliseconds.
	 * 
	 * @param millis The amount of milliseconds to wait from NOW before killing the connection.
	 */
	public void killIn(int millis)
	{
		this.killAtMillis = System.currentTimeMillis() + millis;
	}
	
	/**
	 * Sends a String object's characters through the output stream to the client. The connection is
	 * released when this fails for whatever reason.
	 * 
	 * @param s The String to write.
	 */
	public void sendChars(String s)
	{
		Log.info(this.toString() + " --> " + s);
		try
		{
			dataOut.write(s);
			dataOut.flush();
		}
		catch (Exception ex)
		{
			Log.error("Error in sending string to " + this.toString(), ex);
			
			this.release();
		}
	}
	
	/**
	 * Implementation of run in java.lang.Runnable. Do not make calls to this from other statements!
	 */
	public void run()
	{
		while (isAlive)
		{
			// Is this connection marked for killing, and if so, is it time
			if (this.killAtMillis != -1 && (System.currentTimeMillis() >= this.killAtMillis))
			{
				this.release();
				return;
			}
			
			try
			{
				// Read in actual data length
				char[] lengthHeader = new char[4];
				if (dataIn.read(lengthHeader) != 4) // Bad monkey, BAD
				{
					this.release();
					return;
				}
				
				// Pad non-digits
				String lengthStr = "";
				for(int i = 0; i < lengthHeader.length; i++)
				{
					if(Character.isDigit(lengthHeader[i]))
						lengthStr += lengthHeader[i];
				}
				
				// Parse actual data length
				int dataLength = Integer.parseInt(lengthStr);
				
				// Read in actual data
				char[] data = new char[dataLength];
				if (dataIn.read(data) != dataLength)
				{
					// No message broking!
					this.release();
					return;
				}
				
				// Forward actual data
				this.newData(data);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
			// Wait 20ms before attempting to read next data
			try
			{
				Thread.sleep(20);
			}
			catch (InterruptedException ex)
			{
				Log.error(this.toString() + " errored during run()", ex);
			}
		}
	}
	
	/**
	 * Routes the data.
	 * 
	 * @param data
	 */
	protected void newData(char[] data)
	{
		// Override this
	}
	
	protected void handleConnectionShutdown()
	{
		// Override this
	}
	
	/**
	 * Resolves the IP address of this connection and returns it as a string.
	 * 
	 * @return The the 'dots 'n digits' formatted IP address string, or NULL if there is no socket.
	 */
	public String getIpAddress()
	{
		return (this.client != null) ? this.client.getInetAddress().getHostAddress() : "NULL";
	}
	
	public long getInUseSinceTime()
	{
		return this.inUseSinceMillis;
	}
	
	/**
	 * Returns the string representation of this TcpConnection.
	 */
	public String toString()
	{
		return this.getClass().getSimpleName() + " " + this.ID + ", client " + getIpAddress() + " [" + this.clientID + "]";
	}
}
