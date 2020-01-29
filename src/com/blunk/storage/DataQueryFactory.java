package com.blunk.storage;

import com.blunk.Log;

/**
 * DataQueryFactory can register Database implementations of DataQuery's and craft new instances of
 * them. Each DataQuery has it's own implementation for the Database implementation that is loaded
 * in the Blunk environment.
 * 
 * @author Nils
 */
public class DataQueryFactory
{
	private final String impName; // eg, SQL, FS, XML
	
	private final Class<DataQuery>[] qrys;
	private final String[] qrySuperClassNames;
	
	/**
	 * Constructs the DataQueryFactory.
	 * 
	 * @param impName The name of the implementation of the database, eg, SQL, FS, XML, etc.
	 * @param maxQueries The max amount of DataQuery classes this factory can maintain.
	 */
	@SuppressWarnings("unchecked")
	public DataQueryFactory(String impName, int maxQueries)
	{
		this.impName = impName;
		
		this.qrys = new Class[maxQueries];
		this.qrySuperClassNames = new String[maxQueries];
	}
	
	/**
	 * Attempts to register a DataQuery of this database implementation, by constructing the
	 * classname to the implementation.
	 * 
	 * @param superClassName The full name of the superclass (non-implementation) to register the
	 * implementation for. Eg, net.nillus.UserFinder.
	 * @return True if registering succeeded, false otherwise.
	 */
	@SuppressWarnings("unchecked")
	public boolean registerQueryClass(String superClassName)
	{
		Class<DataQuery> superClass = null;
		Class implClass = null;
		
		try
		{
			// Try to create class instance of the superclass
			superClass = (Class<DataQuery>)Class.forName(superClassName);
			
			// Now construct the implementation class name
			String implClassName = superClass.getPackage().getName() + "." + this.impName.toLowerCase() + "." + this.impName + superClass.getSimpleName();
			
			// And finally try to get the Class of the implementation class
			implClass = Class.forName(implClassName);
		}
		catch (Exception ex)
		{
			Log.error("Could not load " + this.impName + "DataQuery implementation of DataQuery " + superClassName);
			return false;
		}
		
		// And now sink the implemented class and it's superclass name
		for (int i = 0; i < qrys.length; i++)
		{
			if (qrys[i] == null)
			{
				qrys[i] = implClass;
				qrySuperClassNames[i] = superClass.getSimpleName();
				
				Log.info("Registered DataQuery " + superClassName + " [" + this.impName + "DataObject " + implClass.getName() + "]");
				return true;
			}
		}
		
		Log.error("Could not sink new DataQuery implementation class, already " + this.qrys.length + " classes registered! Consider setting a higher 'maxQueries'!");
		return false;
	}
	
	/**
	 * Tries to instantiate a new DataQuery for the implementation used by this factory, by looking
	 * up the registered classes.
	 * 
	 * @param objName The simple class name (!) to instantiate a new implementation instance of. Eg,
	 * 'UserFinder'.
	 * @return The DataQuery if succeeded, null otherwise.
	 */
	public DataQuery newQuery(String objName)
	{
		try
		{
			for (int i = 0; i < qrys.length; i++)
			{
				if (qrys[i] == null)
					break; // This slot is empty, and everything after it aswell
				else
				{
					if (qrySuperClassNames[i].equals(objName)) // Is this the class we are searching for?
						return (DataQuery)qrys[i].newInstance();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		// Uh oh... 
		Log.error("Could not create new instance of " + this.impName + "DataQuery '" + objName + "', ensure that the DataQuery '" + objName + "' is registered!");
		return null;
	}
}
