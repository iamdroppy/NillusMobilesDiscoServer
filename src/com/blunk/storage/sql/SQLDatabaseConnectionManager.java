package com.blunk.storage.sql;

import java.io.*;
import java.util.*;
import java.sql.*;

import com.blunk.Log;

/**
 * SQLDatabaseConnectionManager manages connections to a SQL database. This class is officially
 * ripped from FUSELight for the hell of it.
 * 
 * @author Aapo Kyrola, Nillus
 */
public class SQLDatabaseConnectionManager
{
	private Object waitLock = new Object();
	
	private final String epURL;
	private final String epUsername;
	private final String epPassword;
	
	private int maxConnections;
	private int createdConnections;
	private Vector<Connection> availableConnections;
	
	/**
	 * Constructs a SQLDatabaseConnectionManager that will manage connections for a SQLDatabase
	 * specified in a properties file.
	 */
	public SQLDatabaseConnectionManager(String propsPath)
	{
		// Load properties
		Properties dbProps = new Properties();
		try
		{
			dbProps.load(new FileInputStream(propsPath));
		}
		catch (Exception ex)
		{
			Log.error("Could not load DatabaseConnectionManager properties file!", ex);
		}
		
		// Set endpoint details
		epURL = dbProps.getProperty("db.jdbc.url");
		epUsername = dbProps.getProperty("db.uid");
		epPassword = dbProps.getProperty("db.pwd");
		
		// Try load & register JDBC driver
		String driverClass = dbProps.getProperty("db.jdbc.driver");
		if (driverClass == null)
			driverClass = "";
		try
		{
			Driver connectionDriver = (Driver)Class.forName(driverClass).newInstance();
			DriverManager.registerDriver(connectionDriver);
		}
		catch (Exception ex)
		{
			Log.error("Could not load and/or register database connection driver '" + driverClass + "'", ex);
			return;
		}
		
		// Parse max connections
		try
		{
			maxConnections = Integer.parseInt(dbProps.getProperty("db.maxconnections"));
			if (maxConnections < 0)
				maxConnections = 0;
		}
		catch (NumberFormatException ex)
		{
			maxConnections = 0;
		}
		
		availableConnections = new Vector<Connection>(maxConnections);
	}
	
	/**
	 * Tries to return a java.sql.Connection that is ready for use. Always return this Connection to
	 * this manager again, or it will not get recycled!
	 */
	public Connection getConnection()
	{
		Connection conn = null;
		synchronized (waitLock)
		{
			// Any available connections?
			if (availableConnections.size() > 0)
			{
				conn = availableConnections.remove(availableConnections.size() - 1);
				if (connectionIsUseable(conn) == false) // Is this connection
				// worth anything?
				{
					createdConnections--; // Allow creating a new one
					conn = getConnection(); // Recursive
				}
			}
			// Can we create new connection?
			else if (maxConnections == 0 || createdConnections < maxConnections)
			{
				conn = createConnection();
				createdConnections++;
			}
			
			// No available connection? (or could not create new one)
			if (conn == null)
			{
				// Let's wait for a returning connection!
				try
				{
					waitLock.wait(5 * 1000); // Wait 5 seconds for teh poke
					
					// If execution gets here, there has been no poking
				}
				catch (InterruptedException ex)
				{
					conn = getConnection(); // Yay! Let's try to recycle the
					// returned connection!
				}
			}
		}
		
		if (conn == null) // Could do some starvation healing system here!
		{
			Log.error("Could not get a SQLDatabase connection!");
		}
		
		return conn;
	}
	
	/**
	 * Returns a java.sql.Connection to the pool again, so it can be recycled in other requests.
	 * 
	 * @param conn The java.sql.Connection to return to the pool.
	 */
	public void releaseConnection(Connection conn)
	{
		if (conn != null)
		{
			synchronized (waitLock)
			{
				availableConnections.add(conn);
				waitLock.notifyAll(); // Poke waiting connection getters
			}
		}
	}
	
	/**
	 * Tries to create a new java.sql.Connection to the database endpoint and returns it.
	 * 
	 * @return A new java.sql.Connection that is opened and ready for usage. Can be null if creating
	 * failed!
	 */
	private Connection createConnection()
	{
		Connection conn = null;
		
		try
		{
			conn = DriverManager.getConnection(epURL, epUsername, epPassword);
		}
		catch (SQLException ex)
		{
			Log.error("Failed to create a new SQL database Connection for " + epURL, ex);
		}
		
		return conn;
	}
	
	/**
	 * Checks if a given java.sql.Connection can be reused by another connection request.
	 * 
	 * @param conn The java.sql.Connection to check.
	 * @return True if the connection can be reused, false if it sucks ass.
	 */
	private boolean connectionIsUseable(Connection conn)
	{
		try
		{
			// Other checks here
			return conn.isClosed();
		}
		catch (SQLException ex)
		{
			return false;
		}
	}
}
