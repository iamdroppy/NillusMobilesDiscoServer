package net.nillus.mobilesdisco.communication.requests;

import net.nillus.mobilesdisco.spaces.SpaceUser;

import com.blunk.communication.ClientMessage;
import com.blunk.communication.ClientRequestHandler;
import com.blunk.communication.CommunicationHandler;

public class DROPDRINK implements ClientRequestHandler
{
	public void handle(ClientMessage msg, CommunicationHandler comm)
	{
		SpaceUser usr = comm.getSpaceInstance().getUser(comm.clientID);
		
		if(usr != null)
		{
			usr.removeStatus("carryd");
			usr.requiresUpdate = true;
		}
	}
}
