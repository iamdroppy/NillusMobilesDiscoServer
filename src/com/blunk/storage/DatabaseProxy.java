package com.blunk.storage;

import java.rmi.Naming;

import com.blunk.Log;

/**
 * DatabaseProxy provides a reference to a Database instance. This is mostly done over Remote Method
 * Invocation (RMI), but DatabaseProxy can also reference to a Database instance in the same JVM.
 * 
 * @author Nillus
 */
public class DatabaseProxy
{
	/**
	 * The maximum age of a reference to a remote Database instance can have. The reference will be
	 * refetched on the next request for the reference.
	 */
	private final int refetchReferenceSeconds = 60;
	
	/**
	 * The URI that specifies where the DatabaseProxy can locate the Database instance over RMI.
	 * Example: rmi://servicehost/blunkdbserver, where 'servicehost' is the hostname the server is
	 * bound to by Naming.
	 */
	private String rmiURI;
	/**
	 * The reference to the Database instance. This is refetched on a regular basis.
	 */
	private Database reference;
	/**
	 * The time in milliseconds of when the Database reference was renewed for the last time.
	 */
	private long lastReferenceTime;
	
	/**
	 * Marks this DatabaseProxy as a proxy that routes to an internal Database instance.
	 * 
	 * @param db The Database instance that this proxy routes to.
	 */
	public void setInternalReference(Database db)
	{
		reference = db;
		this.rmiURI = null;
	}
	
	/**
	 * Marks this DatabaseProxy as a proxy that routes to a Database instance over RMI.
	 * 
	 * @param rmiURI The RMI URI specifying where the DatabaseProxy can find the Database instance
	 * by using java.rmi.Naming.lookup().
	 */
	public void setRemoteReference(String rmiURI)
	{
		reference = null;
		this.rmiURI = rmiURI;
	}
	
	/**
	 * Returns 'true' if this DatabaseProxy routes to a Database instance in the same JVM.
	 */
	public boolean isInternal()
	{
		return (rmiURI == null && reference != null);
	}
	
	/**
	 * Returns 'true' if this DatabaseProxy routes to a Database instance over RMI.
	 */
	public boolean isRemote()
	{
		return (rmiURI != null);
	}
	
	/**
	 * Returns a reference to the Database instance. It might refetch the reference.
	 * 
	 * @return The Database instance this DatabaseProxy routes to. This can be either an internal
	 * one or one over RMI.
	 */
	public synchronized Database getDatabase()
	{
		if (isRemote() == true) // We might need to refetch the remote reference
		// to the database instance
		{
			// Is it time for a remote refetch?
			if (reference == null || (System.currentTimeMillis() - lastReferenceTime > (refetchReferenceSeconds * 1000)))
			{
				refetchRemoteReference();
			}
		}
		
		return reference;
	}
	
	/**
	 * Attempts to refetch the remote reference to the Database by using java.rmi.lookup().
	 */
	private synchronized void refetchRemoteReference()
	{
		try
		{
			reference = (Database)Naming.lookup(rmiURI);
			lastReferenceTime = System.currentTimeMillis();
		}
		catch (Exception ex)
		{
			Log.error("Failed to refetch remote reference to Database!", ex);
		}
	}
}
