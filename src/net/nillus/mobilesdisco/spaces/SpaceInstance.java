package net.nillus.mobilesdisco.spaces;

import net.nillus.mobilesdisco.MobilesDisco;

import com.blunk.Log;
import com.blunk.communication.CommunicationHandler;
import com.blunk.communication.ServerMessage;

public class SpaceInstance implements Runnable
{
	private final static int FRAMETIME = 470;
	
	private SpaceData data;
	private SpaceUser[] users;
	private SpaceObject[] objects;
	private Thread worker;
	
	private int[][] map;
	private boolean[][] mapUsers;
	
	public SpaceInstance(SpaceData data)
	{
		this.data = data;
		this.users = new SpaceUser[data.maxUsers];
		this.objects = new SpaceObject[0];
		this.worker = new Thread(this, "Space " + this.data.name  + " [" + this.data.ID + "]");
		
		String[] axes = data.heightmap.split("\r");
		int maxX = axes[0].length();
		int maxY = axes.length - 1;
		
		this.map = new int[maxX][maxY];
		this.mapUsers = new boolean[maxX][maxY];
		
		for(int y = 0; y < maxY; y++)
		{
			for(int x = 0; x < maxX; x++)
			{
				char tile = axes[y].charAt(x);
				if(!Character.isDigit(tile))
				{
					map[x][y] = -1; // Not passable
				}
				else
				{
					
					map[x][y] = Integer.parseInt(Character.toString(tile)); // Passable at $height
				}
			}
		}
	}
	
	public void start()
	{
		this.worker.start();
	}
	public void stop()
	{
		this.worker.interrupt();
	}
	
	public void broadcastMessage(ServerMessage msg)
	{
		broadcastData(msg.getResult());
	}
	
	public void broadcastData(String data)
	{
		for(SpaceUser usr : users)
		{
			if(usr != null)
				usr.getCommunicator().getConnection().sendChars(data);
		}
	}
	
	public void chat(int clientID, String text)
	{
		SpaceUser usr = getUser(clientID);
		if(usr != null)
		{
			ServerMessage msg = new ServerMessage();
			msg.set("CHAT");
			msg.appendArgument("\r");
			msg.append(usr.name);
			msg.appendArgument(text);
			
			this.broadcastMessage(msg);
		}
	}
	
	public void shout(int clientID, String text)
	{
		SpaceUser usr = getUser(clientID);
		if(usr != null)
		{
			ServerMessage msg = new ServerMessage();
			msg.set("SHOUT");
			msg.appendArgument("\r");
			msg.append(usr.name);
			msg.appendArgument(text);
			
			this.broadcastMessage(msg);
		}
	}
	
	public void whisper(int clientID, String receiver, String text)
	{
		SpaceUser usr = getUser(clientID);
		SpaceUser usr2 = getUser(receiver);
		
		if(usr != null && usr2 != null)
		{
			ServerMessage msg = new ServerMessage();
			msg.set("WHISPER");
			msg.appendArgument("\r");
			msg.append(usr.name);
			msg.appendArgument(text);
			
			usr2.getCommunicator().sendMessage(msg);
		}
	}
	
	public void lookTo(int clientID, int tileX, int tileY)
	{
		SpaceUser usr = this.getUser(clientID);
		if(usr != null)
		{
			usr.rotationBody = Pathfinder.calculateHumanRotation(usr.X, usr.Y, tileX, tileY);
			usr.rotationHead = usr.rotationBody;
			usr.requiresUpdate = true;
		}
	}
	
	public void intercom(String text)
	{
		ServerMessage msg = new ServerMessage();
		msg.set("WHISPER");
		msg.appendArgument("\r");
		msg.append("Intercom");
		msg.appendArgument(text);
		
		this.broadcastMessage(msg);
	}
	
	public boolean joinUser(int clientID)
	{
		// Can this user fit in?
		if(this.userAmount() >= this.data.maxUsers)
			return false;
		
		// Used alot!
		ServerMessage msg = new ServerMessage();
		
		// Create SpaceUser object and locate user in door
		SpaceUser usr = new SpaceUser(clientID);
		usr.X = this.data.doorX;
		usr.Y = this.data.doorY;
		usr.Z = this.data.doorZ;
		
		// Broadcoast this user to all current users
		msg.set("USERS");
		usr.user(msg);
		this.broadcastMessage(msg);
		
		// Sink new user object
		for(int i = 0; i < this.users.length; i++)
		{
			if(this.users[i] == null)
			{
				this.users[i] = usr;
				break;
			}
		}
		
		// Send heightmap plaintext string
		msg.set("HEIGHTMAP");
		msg.appendArgument(this.data.heightmap);
		usr.getCommunicator().sendMessage(msg);
		
		// Build object list and send to this user
		msg.set("OBJECTS");
		msg.appendArgument(Integer.toString(this.data.decorID));
		for(SpaceObject obj : this.objects)
		{
			if(obj != null)
			{
				obj.object(msg);
			}
		}
		usr.getCommunicator().sendMessage(msg);
		
		// Used for STATUS message
		ServerMessage msg2 = new ServerMessage();
		
		// Build full userlist and send to this user
		msg.set("USERS");
		msg2.set("STATUS");
		
		// Define user 'Intercom' (invisible)
		msg.append("\r");
		msg.append("Intercom");
		msg.appendArgument("1,1,1");
		msg.appendArgument("999");
		msg.appendArgument("999");
		msg.appendArgument("999");
		msg.appendArgument("");
		
		for(SpaceUser user : this.users)
		{
			if(user != null)
			{
				user.user(msg);
				user.status(msg2);
			}
		}
		usr.getCommunicator().sendMessage(msg);
		usr.getCommunicator().sendMessage(msg2);
		
		this.intercom("User '" + usr.name + "' has joined space '" + this.data.name + "'");
		return true;
	}
	public void quitUser(int clientID, String reason)
	{
		SpaceUser usr = this.getUser(clientID);
		if(usr == null)
			return;
		
		// Move user to tile 999,999
		ServerMessage msg = new ServerMessage();
		msg.set("STATUS");
		usr.X = 999;
		usr.Y = 999;
		usr.status(msg);
		this.broadcastMessage(msg);
		msg = null;
		
		// Intercom
		if(reason == null)
			reason = "disconnect";
		this.intercom(usr.name + " has left '" + this.data.name + "' (reason: " + reason + ")");
		usr = null;
		
		// Remove SpaceUser
		synchronized(users)
		{
			for(int i = 0; i < users.length; i++)
			{
				if(users[i] != null && users[i].clientID == clientID)
				{
					users[i] = null;
				}
			}
		}
	}
	
	public void run()
	{
		while(true)
		{
			ServerMessage statusUpdates = new ServerMessage();
			statusUpdates.set("STATUS");
			boolean sendStatuses = false;
			
			synchronized(users)
			{
				for(SpaceUser usr : users)
				{
					if(usr != null)
					{
						if(usr.requiresUpdate)
						{
							Tile nextTile = null;
							usr.removeStatus("mv");
							if(usr.goalX != -1)
							{
								// Still on the move?
								if(usr.X != usr.goalX || usr.Y != usr.goalY)
								{
									nextTile = Pathfinder.calculateNextTile(usr.X, usr.Y, usr.goalX, usr.goalY);
									if(nextTile.X != -1 && this.map[nextTile.X][nextTile.Y] != -1 && this.mapUsers[nextTile.X][nextTile.Y] == false)
									{
										usr.rotationBody = Pathfinder.calculateHumanRotation(usr.X, usr.Y, nextTile.X, nextTile.Y);
										usr.rotationHead = usr.rotationBody;
										
										usr.addStatus("mv", nextTile.X + "," + nextTile.Y + "," + this.map[nextTile.X][nextTile.Y]);
									}
									else
									{
										usr.goalX = -1;
										usr.goalY = -1;
										nextTile = null;
									}
								}
								else
								{
									usr.goalX = -1;
									usr.goalY = -1;
								}
							}
							
							usr.status(statusUpdates);
							sendStatuses = true;
							
							if(nextTile == null)
								usr.requiresUpdate = false;
							else
							{
								this.mapUsers[usr.X][usr.Y] = false;
								usr.X = nextTile.X;
								usr.Y = nextTile.Y;
								usr.Z = this.map[nextTile.X][nextTile.Y];
								this.mapUsers[usr.X][usr.Y] = true;
							}
						}
					}
				}
			}
			
			// Broadcast the statuses?
			if(sendStatuses)
			{
				this.broadcastMessage(statusUpdates);
			}
			
			// And sleep for a while
			try
			{
				Thread.sleep(FRAMETIME);
			}
			catch(Exception ex)
			{
				Log.error("Worker thread of space " + data.ID + " stopped running!");
				return;
			}
		}
	}
	
	
	public SpaceData getSpaceData()
	{
		return data;
	}
	
	public SpaceUser getUser(String name)
	{
		for(int i = 0; i < users.length; i++)
		{
			if(users[i] != null && users[i].name.equals(name))
				return users[i];
		}
		
		return null;
	}
	
	public SpaceUser getUser(int clientID)
	{
		for(int i = 0; i < users.length; i++)
		{
			if(users[i] != null && users[i].clientID == clientID)
				return users[i];
		}
		
		return null;
	}
	
	public int userAmount()
	{
		int n = 0;
		synchronized(this.users)
		{
			for(int i = 0; i < users.length; i++)
			{
				if(users[i] != null)
					n++;
			}
		}
		
		return n;
	}
}
