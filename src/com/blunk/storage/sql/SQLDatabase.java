package com.blunk.storage.sql;

import java.rmi.Naming;
import java.sql.Connection;

import com.blunk.*;
import com.blunk.storage.*;

/**
 * SQLDatabase is a Database that can store SQLDataObjects in a SQL database over JDBC.
 * 
 * @author Nillus
 */
public class SQLDatabase implements Database
{
	/**
	 * The SQLDatabaseConnectionManager that manages connections for the endpoint database.
	 */
	private SQLDatabaseConnectionManager connManager;
	
	/**
	 * TODO: document me!
	 */
	public SQLDatabase()
	{
		String propertiesFile = Environment.getPropBox().get("db.props");
		connManager = new SQLDatabaseConnectionManager(propertiesFile);
		if (connManager == null)
		{
			Log.error("Failed to create SQLDatabaseConnection manager!");
		}
	}
	
	@Override
	public void insert(DataObject obj)
	{
		if (obj == null)
			return;
		
		Connection conn = connManager.getConnection();
		if (conn != null)
		{
			try
			{
				((SQLDataObject)obj).insert(conn);
			}
			catch (Exception ex)
			{
				Log.error("Failed to insert new SQLDataObject into SQLDatabase!", ex);
			}
			finally
			{
				connManager.releaseConnection(conn);
			}
		}
	}
	
	@Override
	public void delete(DataObject obj)
	{
		if (obj == null)
			return;
		
		Connection conn = connManager.getConnection();
		if (conn != null)
		{
			try
			{
				((SQLDataObject)obj).delete(conn);
			}
			catch (Exception ex)
			{
				Log.error("Failed to delete existing SQLDataObject from SQLDatabase!", ex);
			}
			finally
			{
				connManager.releaseConnection(conn);
			}
		}
	}
	
	@Override
	public void update(DataObject obj)
	{
		if (obj == null)
			return;
		
		Connection conn = connManager.getConnection();
		if (conn != null)
		{
			try
			{
				((SQLDataObject)obj).insert(conn);
			}
			catch (Exception ex)
			{
				Log.error("Failed to update existing SQLDataObject to SQLDatabase!", ex);
			}
			finally
			{
				connManager.releaseConnection(conn);
			}
		}
	}
	
	@Override
	public void execute(DataQuery dbQuery)
	{
		if (dbQuery == null)
			return;
		
		Connection conn = connManager.getConnection();
		if (conn != null)
		{
			try
			{
				((SQLDataQuery)dbQuery).execute(conn);
			}
			catch (Exception ex)
			{
				Log.error("Failed to execute SQLDataQuery against SQLDatabase!", ex);
			}
			finally
			{
				connManager.releaseConnection(conn);
			}
		}
	}
	
	@Override
	public DataQueryResults<?> query(DataQuery dbQuery)
	{
		if (dbQuery == null)
			return null;
		
		DataQueryResults<?> results = null;
		Connection conn = connManager.getConnection();
		if (conn != null)
		{
			try
			{
				results = ((SQLDataQuery)dbQuery).query(conn);
			}
			catch (Exception ex)
			{
				Log.error("Failed to execute SQLDataQuery against SQLDatabase!", ex);
			}
			finally
			{
				connManager.releaseConnection(conn);
			}
		}
		
		return results;
	}
	
	/**
	 * Initializes the Blunk environment, starts up a remote SQLDatabase instance and binds it to a
	 * RMI url.
	 * 
	 * @param args arg0 = Path to blunk.properties (environment). arg1 = RMI url
	 */
	public static void main(String[] args)
	{
		try
		{
			// Initialize Environment with blunk.properties (arg0)
			Environment.init(args[0]);
			
			// Now create new instance!
			SQLDatabase instance = new SQLDatabase();
			
			// And now bind the new instance of SQLDatabase to the RMI uri
			// (arg1)
			Naming.bind(args[1], instance);
			
			// We are up and running!
			Log.info("SQLDatabase instance up and running and bound to " + args[2]);
		}
		catch (Exception ex)
		{
			Log.error("Failed to start new SQLDatabase instance!", ex);
			Log.info("Start this class with 'java com.blunk.storage.sql.SQLDatabase [blunk.properties] [RMI url]");
		}
	}
}
