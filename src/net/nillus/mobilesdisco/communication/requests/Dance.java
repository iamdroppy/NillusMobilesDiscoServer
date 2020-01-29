package net.nillus.mobilesdisco.communication.requests;

import net.nillus.mobilesdisco.spaces.SpaceUser;

import com.blunk.communication.ClientMessage;
import com.blunk.communication.ClientRequestHandler;
import com.blunk.communication.CommunicationHandler;

public class Dance implements ClientRequestHandler
{
	public void handle(ClientMessage msg, CommunicationHandler comm)
	{
		SpaceUser usr = comm.getSpaceInstance().getUser(comm.clientID);
		
		if(usr != null)
		{
			usr.removeStatus("dance");
			usr.addStatus("dance", null);
			usr.requiresUpdate = true;
		}
	}
}
