package com.blunk.communication;

import net.nillus.mobilesdisco.MobilesDisco;
import net.nillus.mobilesdisco.communication.requests.*;
import net.nillus.mobilesdisco.spaces.SpaceInstance;
import net.nillus.mobilesdisco.spaces.SpaceUser;
import net.nillus.mobilesdisco.users.User;

import com.blunk.Log;

import com.blunk.net.TcpConnection;

public class CommunicationHandler
{
	public final int clientID;
	private final TcpConnection connection;
	private final ClientRequestHandlerManager requestManager;
	
	public ClientMessage request;
	public ServerMessage response;
	public String keyEncrypted;
	
	private User usrObj;
	private int spaceID;
	
	public CommunicationHandler(TcpConnection conn)
	{
		this.clientID = conn.clientID;
		this.connection = conn;
		this.response = new ServerMessage();
		this.requestManager = new ClientRequestHandlerManager(this);
	}
	
	/**
	 * Tries to send the current ServerMessage (response) in the CommunicationHandler to the
	 * underlying TcpConnection.
	 * 
	 * @see sendMessage(ServerMessage)
	 */
	public void sendResponse()
	{
		sendMessage(this.response);
	}
	
	/**
	 * Tries to send a given ServerMessage object to the underlying TcpConnection.
	 * 
	 * @param msg The ServerMessage object representing the message to send.
	 */
	public void sendMessage(ServerMessage msg)
	{
		if (msg != null && connection != null)
			connection.sendChars(msg.getResult());
	}
	
	public void handleClientMessage(ClientMessage msg)
	{
		// Log message
		Log.info(this.connection.toString() + " <-- " + msg.toString());
		
		// Handle message
		requestManager.handleRequest(msg);
	}
	
	public void Login(String name, String password)
	{
		User usr = MobilesDisco.getUserRegister().loadUser(name);
		
		if(usr == null || !usr.password.equals(password))
		{
			response.set("ERROR");
			response.appendArgument("login in");
			sendResponse();
			return;
		}
		
		// Login OK!
		this.usrObj = usr;
		deRegisterPreLoginHandlers();
		registerInSpaceHandlers();
		
		// Join space 'main'
		int spaceID = MobilesDisco.getSpaceInstances().resolveSpaceIdByName("dance");
		this.joinSpace(spaceID);
	}
	
	public boolean joinSpace(int spaceID)
	{
		// Leave current space
		quitSpace("different space");
		
		SpaceInstance space = MobilesDisco.getSpaceInstances().getSpace(spaceID);
		if(space != null && space.joinUser(this.clientID))
		{
			this.spaceID = spaceID;
			this.registerInSpaceHandlers();
			
			return true; // OK!
		}
		else
		{
			this.response.set("ERROR");
			this.response.appendArgument("Could not join space " + spaceID + ", space is probably full, does not exist or you have no access.");
			this.sendResponse();
			
			return false;
		}
	}
	public void quitSpace(String reason)
	{
		try
		{
			this.getSpaceInstance().quitUser(this.clientID, reason);
		}
		catch(Exception ex)
		{
		}
		finally
		{
			this.spaceID = 0;
			this.deRegisterInSpaceHandlers();
		}
	}
	
	public void registerPreLoginHandlers()
	{
		requestManager.registerRequestHandler(new LOGIN());
		requestManager.registerRequestHandler(new REGISTER());
		requestManager.registerRequestHandler(new INFORETRIEVE());
	}
	public void deRegisterPreLoginHandlers()
	{
		requestManager.deRegisterRequestHandler("LOGIN");
		requestManager.deRegisterRequestHandler("REGISTER");
		requestManager.deRegisterRequestHandler("INFORETRIEVE");
	}
	public void registerInSpaceHandlers()
	{
		requestManager.registerRequestHandler(new GOAWAY());
		requestManager.registerRequestHandler(new CHAT());
		requestManager.registerRequestHandler(new SHOUT());
		requestManager.registerRequestHandler(new WHISPER());
		requestManager.registerRequestHandler(new LOOKTO());
		requestManager.registerRequestHandler(new STOP());
		requestManager.registerRequestHandler(new DROPDRINK());
		requestManager.registerRequestHandler(new Move());
		requestManager.registerRequestHandler(new Dance());
	}
	public void deRegisterInSpaceHandlers()
	{
		requestManager.deRegisterRequestHandler("GOAWAY");
		requestManager.deRegisterRequestHandler("CHAT");
		requestManager.deRegisterRequestHandler("SHOUT");
		requestManager.deRegisterRequestHandler("WHISPER");
		requestManager.deRegisterRequestHandler("LOOKTO");
		requestManager.deRegisterRequestHandler("STOP");
		requestManager.deRegisterRequestHandler("DROPDRINK");
		requestManager.deRegisterRequestHandler("Move");
		requestManager.deRegisterRequestHandler("Dance");
	}
	
	public boolean handleChatCommand(String text)
	{
		if(text.length() > 0 && text.charAt(0) == '/')
		{
			int indexOfWhiteSpace = text.indexOf(32);
			String command = null;
			if(indexOfWhiteSpace == -1)
				command = text;
			else
				command = text.substring(0, indexOfWhiteSpace);
			command = command.substring(1);
			
			if(command.equals("help"))
			{
				ServerMessage msg = new ServerMessage();
				msg.set("ERROR");
				
				this.sendMessage(msg);
				
				return true;
			}
			else if(command.equals("spaces"))
			{
				ServerMessage msg = new ServerMessage();
				msg.set("ERROR");
				msg.append(" \r");
				
				SpaceInstance[] spaces = MobilesDisco.getSpaceInstances().getSpaces();
			
				for(SpaceInstance instance : spaces)
				{
					msg.append("Space '" + instance.getSpaceData().name + "'" + "\r");
					msg.append("Users: " + instance.userAmount() + "/" + instance.getSpaceData().maxUsers + "\r");
					msg.append("\r");
				}
				msg.append("Type '/join <SPACENAME>' to join a space.");
				this.sendMessage(msg);
				
				return true;
			}
			else if(command.equals("join"))
			{
				String spaceName = text.substring(indexOfWhiteSpace + 1);
				int spaceID = MobilesDisco.getSpaceInstances().resolveSpaceIdByName(spaceName);
				if(spaceID > 0)
					this.joinSpace(spaceID);
				else
				{
					ServerMessage msg = new ServerMessage();
					msg.set("ERROR");
					msg.appendArgument("Could not resolve space ID of space '" + spaceName + "'!");
					this.sendMessage(msg);
				}
				
				return true;
			}
			else if(command.equals("quit"))
			{
				String reason = null;
				if(indexOfWhiteSpace != -1)
					reason = text.substring(indexOfWhiteSpace + 1);
				
				this.quitSpace(reason);
				
				return true;
			}
		}
		
		return false;
	}
	public ClientRequestHandlerManager getRequestHandlerManager()
	{
		return this.requestManager;
	}
	
	public User getUserObject()
	{
		return this.usrObj;
	}
	public SpaceInstance getSpaceInstance()
	{
		if(this.spaceID == 0)
			return null;
		
		return MobilesDisco.getSpaceInstances().getSpace(this.spaceID);
	}
	public SpaceUser getSpaceUser()
	{
		SpaceInstance instance = this.getSpaceInstance();
		if(instance == null)
			return null;
		
		return instance.getUser(this.clientID);
	}
	public TcpConnection getConnection()
	{
		return this.connection;
	}
}
