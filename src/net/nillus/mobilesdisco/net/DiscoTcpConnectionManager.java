package net.nillus.mobilesdisco.net;

import java.util.Vector;

import com.blunk.Log;
import com.blunk.net.TcpConnection;
import com.blunk.net.TcpConnectionManager;

import net.nillus.mobilesdisco.communication.requests.*;

/**
 * DiscoTcpConnectionManager is a TcpConnectionManager that manages DiscoTcpConnections and destroys
 * connections that have timed out.
 * 
 * @author Nillus
 */
public class DiscoTcpConnectionManager implements TcpConnectionManager, Runnable
{
	private int clientCounter;
	private DiscoTcpConnection[] connections;
	private Thread worker;
	
	public DiscoTcpConnectionManager(int maxConnections)
	{
		this.connections = new DiscoTcpConnection[maxConnections];
		this.worker = new Thread(this);
		this.worker.setPriority(3);
		
		this.worker.start();
	}
	
	@Override
	public DiscoTcpConnection createNewConnection()
	{
		for (int i = 0; i < connections.length; i++)
		{
			if (connections[i] == null)
			{
				DiscoTcpConnection newConn = new DiscoTcpConnection(i, ++clientCounter, this);
				connections[i] = newConn;
				
				return newConn;
			}
		}
		
		return null;
	}
	
	/**
	 * Starts up the TcpConnection for a InfoConnection by sending HELLO.
	 */
	public void newConnectionAccepted(TcpConnection conn)
	{
		Log.info("Accepted new connection " + conn.toString());
		
		DiscoTcpConnection discoConn = (DiscoTcpConnection)conn;
		
		// Register LOGIN, INFORETRIEVE and REGISTER
		discoConn.getCommunicator().getRequestHandlerManager().registerRequestHandler(new LOGIN());
		discoConn.getCommunicator().getRequestHandlerManager().registerRequestHandler(new INFORETRIEVE());
		discoConn.getCommunicator().getRequestHandlerManager().registerRequestHandler(new REGISTER());
	}
	
	@Override
	public void freeConnection(int connectionID)
	{
		if (connectionID >= 0 && connectionID < connections.length)
		{
			Log.info("Free'd connection slot " + connectionID + " y0!");
			this.connections[connectionID] = null;
		}
	}
	
	public DiscoTcpConnection getConnection(int connectionID)
	{
		return connections[connectionID];
	}
	
	public DiscoTcpConnection getClientConnection(int clientID)
	{
		for (int i = 0; i < connections.length; i++)
		{
			if (connections[i] != null && connections[i].clientID == clientID)
				return connections[i];
		}
		
		return null;
	}
	
	@Override
	public int getActiveConnectionsAmount()
	{
		int n = 0;
		for (int i = 0; i < connections.length; i++)
		{
			if (connections[i] != null)
				n++;
		}
		
		return n;
	}
	
	@Override
	public int getMaxConnections()
	{
		return connections.length;
	}
	
	public void run()
	{
		while (true)
		{
			// Drop connections that haven't sent STATUSOK recently
			this.dropTimedOutConnections();
			
			// Other stuff?
			// Goes here??
			// AMIRITE???
			
			// ZZzzz...
			try
			{
				Thread.sleep(60 * 1000);
			}
			catch (InterruptedException ex)
			{
				return;
			}
		}
	}
	
	private void dropTimedOutConnections()
	{
		long nowTime = System.currentTimeMillis();
		Vector<DiscoTcpConnection> timedOutConnections = new Vector<DiscoTcpConnection>();
		
		synchronized (this.connections)
		{
			// Gather the timed out connections
			for (int i = 0; i < connections.length; i++)
			{
				// Is this connection in use?
				if (connections[i] != null)
				{
					if(!connections[i].statusOK()) // Timed out
					{
						timedOutConnections.add(connections[i]);
					}
				}
			}
			
			// Release the gathered timed out connections
			for (DiscoTcpConnection conn : timedOutConnections)
			{
				conn.release();
			}
		}
	}
}