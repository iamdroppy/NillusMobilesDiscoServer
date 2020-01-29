package net.nillus.mobilesdisco;

import java.net.InetAddress;

import net.nillus.mobilesdisco.net.DiscoTcpConnectionManager;
import net.nillus.mobilesdisco.spaces.SpaceManager;
import net.nillus.mobilesdisco.users.UserRegister;

import com.blunk.*;
import com.blunk.util.*;
import com.blunk.net.*;
import com.blunk.storage.*;

public class MobilesDisco
{
	private static PropertiesBox propBox;
	private static DataObjectFactory dataObjFactory;
	private static DataQueryFactory dataQryFactory;
	
	private static DiscoTcpConnectionManager connectionManager;
	private static TcpConnectionListener connectionListener;
	
	private static UserRegister userRegister;
	private static SpaceManager spaceManager;
	
	public static void main(String[] args)
	{
		//arg0 = blunk.properties
		//arg1 = mobilesdisco.properties
		
		if(args.length < 2)
		{
			Log.error("Invalid command line arguments!");
			return;
		}
		
		if(!Environment.init(args[0]))
				return;
		
		System.out.println();
		System.out.println("#########################################");
		System.out.println("##  Mobiles Disco server emulator      ##");
		System.out.println("##  Copyright (C) 2009                 ##");
		System.out.println("##  Nils [nillus]                      ##");
		System.out.println("##  NILLUS.NET                         ##");
		System.out.println("#########################################");
		System.out.println();
		
		if(!initPropBox(args[1]))
			return;
		System.out.println();
		
		if(!initDataObjects())
			return;
		System.out.println();
		
		if(!initDataQueries())
			return;
		System.out.println();
		
		if(!initConnections())
			return;
		System.out.println();
		
		userRegister = new UserRegister();
		spaceManager = new SpaceManager();
		spaceManager.initSpaces();
		
		Log.info("Initialized MobilesDisco server, up and running!");
	}
	
	private static boolean initPropBox(String propertiesFile)
	{
		Log.info("Locating .properties file for Mobiles Disco...");
		propBox = new PropertiesBox();
		if (!propBox.load(propertiesFile))
		{
			Log.error("Could not load Mobiles Disco .properties file " + propertiesFile);
			return false;
		}
		Log.info("Initialized properties for Mobiles Disco, " + propBox.size() + " properties loaded.");
		
		return true;
	}
	
	private static boolean initDataObjects()
	{
		int maxObjects = propBox.getInt("db.objects.max", 25);
		String dbImplName = Environment.getPropBox().get("db.impl", "NOTDEFINED");
		dataObjFactory = new DataObjectFactory(dbImplName, maxObjects);
		
		Log.info("Registering DataObject classes...");
		
		int classID = 0;
		int registeredCounter = 0;
		String objName = null;
		
		while ((objName = propBox.get("db.object[" + classID++ + "]")) != null)
		{
			if (dataObjFactory.registerObjectClass(objName))
				registeredCounter++; // OK!
		}
		
		Log.info("Registered " + registeredCounter + " DataObject classes.");
		
		return true;
	}
	
	private static boolean initDataQueries()
	{
		int maxQueries = propBox.getInt("db.queries.max", 25);
		String dbImplName = Environment.getPropBox().get("db.impl", "NOTDEFINED");
		dataQryFactory = new DataQueryFactory(dbImplName, maxQueries);
		
		Log.info("Registering DataQuery classes...");
		
		int classID = 0;
		int registeredCounter = 0;
		String qryName = null;
		
		while ((qryName = propBox.get("db.query[" + classID++ + "]")) != null)
		{
			if (dataQryFactory.registerQueryClass(qryName))
				registeredCounter++; // OK!
		}
		
		Log.info("Registered " + registeredCounter + " DataQuery classes.");
		
		return true;
	}
	private static boolean initConnections()
	{
		// Create connections manager
		int maxConnections = propBox.getInt("net.maxconnections", 255);
		connectionManager = new DiscoTcpConnectionManager(maxConnections);
		
		// Hook connections listener
		int netInfoListenerPort = propBox.getInt("net.port", 30000);
		
		try
		{
			InetAddress netInfoListenerAddress = InetAddress.getByName(propBox.get("net.info.host", "localhost"));
			connectionListener = new TcpConnectionListener(netInfoListenerPort, netInfoListenerAddress, connectionManager);
			
			connectionListener.start();
			
			return true;
		}
		catch (Exception ex)
		{
			
		}
		
		return false;
	}
	
	public static PropertiesBox getPropBox()
	{
		return propBox;
	}
	public static DataObjectFactory getDataObjectFactory()
	{
		return dataObjFactory;
	}
	public static DataQueryFactory getDataQueryFactory()
	{
		return dataQryFactory;
	}
	public static DiscoTcpConnectionManager getConnections()
	{
		return connectionManager;
	}
	public static UserRegister getUserRegister()
	{
		return userRegister;
	}
	public static SpaceManager getSpaceInstances()
	{
		return spaceManager;
	}
}
