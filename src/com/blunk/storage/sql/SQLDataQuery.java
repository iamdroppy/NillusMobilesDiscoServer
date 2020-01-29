package com.blunk.storage.sql;

import java.sql.Connection;
import com.blunk.storage.*;

/**
 * SQLDataQuery executes against a SQL database over a JDBC connection and optionally returns the
 * result.
 * 
 * @author Nillus
 */
public interface SQLDataQuery extends DataQuery
{
	/**
	 * Executes this SQLDataQuery against a java.sql.Connection and returns the results.
	 * 
	 * @param conn The open java.sql.Connection (JDBC) to use when executing.
	 * @return The results of the query as a DataQueryResults<?> object.
	 * @throws DatabaseException When whatever error occurs.
	 */
	public DataQueryResults<?> query(Connection conn) throws DatabaseException;
	
	/**
	 * Executes this SQLDataQuery against a java.sql.Connection.
	 * 
	 * @param conn The open java.sql.Connection (JDBC) to use when executing.
	 * @throws DatabaseException When whatever error occurs.
	 */
	public void execute(Connection conn) throws DatabaseException;
}
