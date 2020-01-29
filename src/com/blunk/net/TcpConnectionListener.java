package com.blunk.net;

import java.io.IOException;
import java.net.*;

import com.blunk.Log;

/**
 * TcpConnectionListener listens for incoming TCP network connection requests on a given local IP
 * and port number and requests it's TcpConnectionManager to handle the new connection.
 * TcpConnectionListener runs in it's own thread.
 * 
 * @author Nillus
 */
public class TcpConnectionListener implements Runnable
{
	private final int LISTENER_BACKLOG = 3;
	
	private boolean listens;
	private ServerSocket listener;
	private Thread listenerThread;
	
	private TcpConnectionManager connMgr;
	
	public TcpConnectionListener(int port, InetAddress localAddress, TcpConnectionManager connMgr) throws IOException
	{
		this.connMgr = connMgr;
		
		try
		{
			this.listener = new ServerSocket(port, LISTENER_BACKLOG, localAddress);
			this.listenerThread = new Thread(this);
		}
		catch (Exception ex)
		{
			Log.error("Failed to bind new " + this + " listener ServerSocket to " + localAddress.toString() + ":" + port, ex);
			return;
		}
	}
	
	/**
	 * Starts the internal thread and starts listening for connections.
	 */
	public void start()
	{
		if (!this.listens)
		{
			this.listens = true;
			this.listenerThread.start();
			
			Log.info("Instance of " + this + " started listening on " + this.listener.getLocalSocketAddress().toString());
		}
	}
	
	/**
	 * Interrupts the internal thread and stops listening for connections.
	 */
	public void stop()
	{
		if (this.listens)
		{
			this.listens = false;
			this.listenerThread.interrupt();
			
			Log.info("Instance of " + this + " stopped listening on " + this.listener.getLocalSocketAddress().toString());
		}
	}
	
	public void run()
	{
		while (this.listens)
		{
			try
			{
				// ServerSocket.accept() blocks the calling thread while waiting for connection
				Socket newClient = listener.accept();
				
				// Get a new connection
				TcpConnection conn = connMgr.createNewConnection();
				if (conn == null)
				{
					newClient.close();
					Log.info("New network connection from " + newClient.getRemoteSocketAddress().toString() + " accepted by TcpConnectionListener instance," + "but " + connMgr.toString() + " could not hand out more connections! Connection refused.");
				}
				else
				// Handle this new connection!
				{
					conn.setConnection(newClient);
					connMgr.newConnectionAccepted(conn);
				}
			}
			catch (IOException ex)
			{
				Log.error("Error in " + this + ".run()", ex);
			}
		}
	}
	
	protected void newConnection(TcpConnection conn)
	{
		// Override this
	}
	
	public String toString()
	{
		return this.getClass().getName();
	}
}
