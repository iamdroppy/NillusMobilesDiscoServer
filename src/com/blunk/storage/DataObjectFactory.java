package com.blunk.storage;

import com.blunk.Log;

/**
 * DataObjectFactory can register Database implementations of DataObjects and craft new instances of
 * them. Each DataObject has it's own implementation for the Database implementation that is loaded
 * in the Blunk environment.
 * 
 * @author Nils
 */
public class DataObjectFactory
{
	private final String impName; // eg, SQL, FS, XML
	
	private final Class<DataObject>[] objs;
	private final String[] objSuperClassNames;
	
	/**
	 * Constructs the DataObjectFactory.
	 * 
	 * @param impName The name of the implementation of the database, eg, SQL, FS, XML, etc.
	 * @param maxObjects The max amount of DataObject classes this factory can maintain.
	 */
	@SuppressWarnings("unchecked")
	public DataObjectFactory(String impName, int maxObjects)
	{
		this.impName = impName;
		
		this.objs = new Class[maxObjects];
		this.objSuperClassNames = new String[maxObjects];
	}
	
	/**
	 * Attempts to register a DataObject of this database implementation, by constructing the
	 * classname to the implementation.
	 * 
	 * @param superClassName The full name of the superclass (non-implementation) to register the
	 * implementation for. Eg, net.nillus.User.
	 * @return True if registering succeeded, false otherwise.
	 */
	@SuppressWarnings("unchecked")
	public boolean registerObjectClass(String superClassName)
	{
		Class<DataObject> superClass = null;
		Class implClass = null;
		
		try
		{
			// Try to create class instance of the superclass
			superClass = (Class<DataObject>)Class.forName(superClassName);
			
			// Now construct the implementation class name
			String implClassName = superClass.getPackage().getName() + "." + this.impName.toLowerCase() + "." + this.impName + superClass.getSimpleName();
			
			// And finally try to get the Class of the implementation class
			implClass = Class.forName(implClassName);
		}
		catch (Exception ex)
		{
			Log.error("Could not load " + this.impName + "DataObject implementation of DataObject " + superClassName);
			return false;
		}
		
		// And now sink the implemented class and it's superclass name
		for (int i = 0; i < objs.length; i++)
		{
			if (objs[i] == null)
			{
				objs[i] = implClass;
				objSuperClassNames[i] = superClass.getSimpleName();
				
				Log.info("Registered DataObject " + superClassName + " [" + this.impName + "DataObject " + implClass.getName() + "]");
				return true;
			}
		}
		
		Log.error("Could not sink new DataObject implementation class, already " + this.objs.length + " classes registered! Consider setting a higher 'maxObjects'!");
		return false;
	}
	
	/**
	 * Tries to instantiate a new DataObject for the implementation used by this factory, by looking
	 * up the registered classes.
	 * 
	 * @param objName The simple class name (!) to instantiate a new implementation instance of. Eg,
	 * 'User'.
	 * @return The DataObject if succeeded, null otherwise.
	 */
	public DataObject newObject(String objName)
	{
		try
		{
			for (int i = 0; i < objs.length; i++)
			{
				if (objs[i] == null)
					break; // This slot is empty, and everything after it aswell
				else
				{
					if (objSuperClassNames[i].equals(objName)) // Is this the class we are searching for?
						return (DataObject)objs[i].newInstance();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		// Uh oh... 
		Log.error("Could not create new instance of " + this.impName + "DataObject '" + objName + "', ensure that the DataObject '" + objName + "' is registered!");
		return null;
	}
}
