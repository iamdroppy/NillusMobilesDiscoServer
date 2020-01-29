package com.blunk.storage;

import java.rmi.Remote;

/**
 * Database is a highlevel remote interface to a database that can store DataObjects.
 * 
 * @author Nillus
 * @see DataObject
 * @see CachableDataObject
 */
public interface Database extends Remote
{
	/**
	 * Inserts a new DataObject in the database.
	 * 
	 * @param obj The DataObject to insert.
	 */
	public void insert(DataObject obj);
	
	/**
	 * Deletes an existing DataObject from the database.
	 * 
	 * @param obj The DataObject to delete.
	 */
	public void delete(DataObject obj);
	
	/**
	 * Updates an existing DataObject in the database.
	 * 
	 * @param obj The DataObject to update.
	 */
	public void update(DataObject obj);
	
	/**
	 * Executes one or more queries in a DataQuery against the database and returns the results.
	 * 
	 * @param queryBean The DataQuery object that performs the actual query against the database.
	 * @return A DataQueryResults with the results of the query.
	 */
	public DataQueryResults<?> query(DataQuery queryBean);
	
	/**
	 * Executes one or more queries in a DataQuery against the database.
	 * 
	 * @param queryBean The DataQuery object that performs the actual query against the database.
	 */
	public void execute(DataQuery queryBean);
}
