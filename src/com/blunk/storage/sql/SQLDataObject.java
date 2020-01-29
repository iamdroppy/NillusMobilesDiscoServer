package com.blunk.storage.sql;

import java.sql.Connection;
import com.blunk.storage.DataObject;
import com.blunk.storage.DatabaseException;

/**
 * SQLDataObject is a DataObject that is stored in a SQL database. SQLDataObject is inserteable,
 * deleteable and updateable.
 * 
 * @author Nillus
 * @see DataObject
 */
public interface SQLDataObject extends DataObject
{
	/**
	 * Inserts a new SQLDataObject in the SQLDatabase.
	 * 
	 * @param conn The open java.sql.Connection (JDBC) to use when executing.
	 * @throws DatabaseException When whatever error occurs.
	 */
	public void insert(Connection conn) throws DatabaseException;
	
	/**
	 * Deletes an existing SQLDataObject from the SQLDatabase.
	 * 
	 * @param conn The open java.sql.Connection (JDBC) to use when executing.
	 * @throws DatabaseException When whatever error occurs.
	 */
	public void delete(Connection conn) throws DatabaseException;
	
	/**
	 * Fully updates an existing SQLDataObject in the SQLDatabase.
	 * 
	 * @param conn The open java.sql.Connection (JDBC) to use when executing.
	 * @throws DatabaseException When whatever error occurs.
	 */
	public void update(Connection conn) throws DatabaseException;
}