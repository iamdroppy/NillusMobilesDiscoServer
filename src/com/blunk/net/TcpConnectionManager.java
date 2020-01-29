package com.blunk.net;

/**
 * TcpConnectionManager is an object that manages instantiated TcpConnections.
 * 
 * @author Nillus
 */
public interface TcpConnectionManager
{
	public TcpConnection createNewConnection();
	
	public void freeConnection(int connectionID);
	
	public void newConnectionAccepted(TcpConnection conn);
	
	public int getActiveConnectionsAmount();
	
	public int getMaxConnections();
}
