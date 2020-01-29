package net.nillus.mobilesdisco.spaces;

import net.nillus.mobilesdisco.MobilesDisco;
import net.nillus.mobilesdisco.users.User;

import com.blunk.communication.CommunicationHandler;
import com.blunk.communication.ServerMessage;

public class SpaceUser
{
	private static final int MAX_SIMULTANEOUS_STATUSES = 5;
	
	public final int clientID;
	public final String name;
	
	public int X;
	public int Y;
	public int Z;
	
	public int goalX;
	public int goalY;
	
	public int rotationHead;
	public int rotationBody;
	
	private String[] statusKeys;
	private String[] statusValues;
	
	public boolean requiresUpdate;
	
	public SpaceUser(int clientID)
	{
		this.clientID = clientID;
		this.statusKeys = new String[MAX_SIMULTANEOUS_STATUSES];
		this.statusValues = new String[MAX_SIMULTANEOUS_STATUSES];
		
		User obj = getUserObject();
		if(obj != null)
			this.name = obj.name;
		else
			this.name = "ERR:NULL";
		
		// No tricks!
		this.goalX = -1;
		this.goalY = -1;
	}
	
	public void addStatus(String key, String value)
	{
		for(int i = 0; i < MAX_SIMULTANEOUS_STATUSES; i++)
		{
			if(this.statusKeys[i] == null)
			{
				this.statusKeys[i] = key;
				this.statusValues[i] = value;
				return;
			}
		}
	}
	public void removeStatus(String key)
	{
		for(int i = 0; i < MAX_SIMULTANEOUS_STATUSES; i++)
		{
			if(this.statusKeys[i] != null && this.statusKeys[i].equals(key))
			{
				this.statusKeys[i] = null;
				this.statusValues[i] = null;
				return;
			}
		}
	}
	
	public void user(ServerMessage msg)
	{
		User obj = getUserObject();
		if(obj == null)
			return;
		
		msg.append("\r");
		msg.append(this.name);
		msg.appendArgument(obj.pants + "," + obj.shirt + "," + obj.head);
		msg.appendArgument(Integer.toString(this.X));
		msg.appendArgument(Integer.toString(this.Y));
		msg.appendArgument(Integer.toString(this.Z));
		msg.appendArgument(obj.customData);
	}
	
	public void status(ServerMessage msg)
	{
		msg.append("\r");
		msg.append(this.name);
		msg.appendArgument(this.X + "," + this.Y + "," + this.Z + "," + this.rotationHead + "," + this.rotationBody);
		msg.append("/");
		
		for(int i = 0; i < MAX_SIMULTANEOUS_STATUSES; i++)
		{
			if(this.statusKeys[i] != null)
			{
				msg.append(this.statusKeys[i]);
				if(this.statusValues[i] != null)
				{
					msg.appendArgument(this.statusValues[i]);
				}
				msg.append("/");
			}
		}
	}

	
	public CommunicationHandler getCommunicator()
	{
		try
		{
			return MobilesDisco.getConnections().getClientConnection(this.clientID).getCommunicator();
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
	public User getUserObject()
	{
		CommunicationHandler comm = getCommunicator();
		if(comm == null)
			return null;
		
		return comm.getUserObject();
	}
}
