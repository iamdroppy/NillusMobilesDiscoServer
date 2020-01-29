package com.blunk;

import java.util.Date;
import java.text.SimpleDateFormat;

import com.blunk.util.*;
import com.blunk.storage.*;

/**
 * Environment is a central static class that holds environment properties/variables.
 * 
 * @author Nillus / Mike
 */
public class Environment
{
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private static PropertiesBox propBox = new PropertiesBox();
	private static DatabaseProxy databaseProxy;
	
	/**
	 * Initalizes the static Blunk environment by loading up properties from a .properties file at a
	 * given path. (blunk.properties)
	 * 
	 * @param propertiesFile The path to the blunk.properties file as a string.
	 */
	public static boolean init(String propertiesFile)
	{
		System.out.println();
		System.out.println("#########################################");
		System.out.println("##  Blunk: FUSE emulation environment  ##");
		System.out.println("##  Copyright (C) 2009                 ##");
		System.out.println("##  Mike [office.boy] / Nils [nillus]  ##");
		System.out.println("##  SCRIPT-O-MATIC.NET                 ##");
		System.out.println("#########################################");
		System.out.println();
		
		System.out.println("Java executing directory=" + System.getProperty("user.dir"));
		System.out.println();
		
		Log.info("Initializing Blunk environment...");
		
		if (!initPropBox(propertiesFile))
			return false;
		System.out.println();
		
		initLog();
		
		if (!initDatabaseImpl())
			return false;
		System.out.println();
		
		Log.info("Initialized Blunk environment.");
		
		return true;
	}
	
	private static boolean initPropBox(String propertiesFile)
	{
		Log.info("Locating .properties file for Blunk environment...");
		propBox = new PropertiesBox();
		if (!propBox.load(propertiesFile))
		{
			Log.error("Could not load Blunk environment .properties file " + propertiesFile);
			return false;
		}
		Log.info("Initialized properties for Blunk environment, " + propBox.size() + " properties loaded.");
		
		return true;
	}
	
	private static void initLog()
	{
		// Initialize log
		Log.logDebug = (propBox.get("system.log.debug").equals("1"));
		Log.logErrors = (propBox.get("system.log.errors").equals("1"));
		Log.logErrorsToFile = (propBox.get("system.log.errors.save").equals("1"));
		Log.logInfoToFile = (propBox.get("system.log.info.save").equals("1"));
		Log.init();
	}
	
	@SuppressWarnings("unchecked")
	private static boolean initDatabaseImpl()
	{
		// Setup database proxy
		databaseProxy = new DatabaseProxy();
		
		// Eg, 'SQL', 'FS', 'XML'
		String dbImplName = propBox.get("db.impl", "WARNING: NOTDEFINED").toUpperCase();
		String dbImplClassName = "com.blunk.storage." + dbImplName.toLowerCase() + "." + dbImplName + "Database";
		Log.info("Database implementation name: " + dbImplName);
		Log.info("Database classname: " + dbImplClassName);
		
		// Try to get Database implementation class
		Class databaseImplClass = null;
		try
		{
			databaseImplClass = Class.forName(dbImplClassName);
		}
		catch (ClassNotFoundException ex)
		{
			Log.error("The class " + dbImplClassName + " is not found or not a valid com.blunk.storage.Database implementation!");
			return false;
		}
		
		// Get RMI url
		String dbRmiUrl = propBox.get("db.rmiUrl", "WARNING: NOTDEFINED");
		Log.info("Database RMI url: " + dbRmiUrl);
		
		// Determine if to link to internal or remote Database
		if (dbRmiUrl.equals("internal")) // Set up internal database instance
		{
			Database dbInstance;
			try
			{
				dbInstance = (Database)databaseImplClass.newInstance();
			}
			catch (Exception ex)
			{
				Log.error("Could not setup internal Database instance!", ex);
				return false;
			}
			
			databaseProxy.setInternalReference(dbInstance);
		}
		else
		{
			databaseProxy.setRemoteReference(dbRmiUrl);
		}
		
		return true;
	}
	
	public static String getCurrentDateString()
	{
		return dateFormat.format(new Date());
	}
	
	/**
	 * Returns the com.blunk.util.PropertiesBox instance that holds environment variables.
	 */
	public static PropertiesBox getPropBox()
	{
		return propBox;
	}
	
	/**
	 * Returns the Database Proxy, used for
	 * Environment.getDatabaseProxy().getDatabase().query(stuff);
	 * 
	 * @return databaseProxy The instance of DatabaseProxy
	 */
	public static DatabaseProxy getDatabaseProxy()
	{
		return databaseProxy;
	}
	
	public static Database getDatabase()
	{
		return databaseProxy.getDatabase();
	}
}
