package com.blunk.storage;

import java.util.Vector;

/**
 * DataQueryResult holds the results of the execution of a DataQuery against a database. These
 * results can be of type T only.
 * 
 * @author Nillus
 */
@SuppressWarnings("serial")
public class DataQueryResults<T> extends Vector<T>
{
	/**
	 * Constructs an empty DataQueryQueryResults with an initial capacity.
	 * 
	 * @param initialCapacity The minimum amount of results in the result collection.
	 */
	public DataQueryResults(int initialCapacity)
	{
		super(initialCapacity);
	}
	
	/**
	 * Adds an object to the results collection. This object has to be convertible to type T and
	 * cannot be null.
	 * 
	 * @param obj The object of type T to add to the results collection.
	 * @return True if object was added, false if object was not added.
	 */
	public boolean addResult(T obj)
	{
		if (obj != null)
			return super.add(obj);
		
		return false;
	}
}
