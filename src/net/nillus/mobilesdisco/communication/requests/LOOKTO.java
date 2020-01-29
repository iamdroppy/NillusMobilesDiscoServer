package net.nillus.mobilesdisco.communication.requests;

import com.blunk.communication.ClientMessage;
import com.blunk.communication.ClientRequestHandler;
import com.blunk.communication.CommunicationHandler;

public class LOOKTO implements ClientRequestHandler
{
	public void handle(ClientMessage msg, CommunicationHandler comm)
	{
		int tileX = Integer.parseInt(msg.nextArgument());
		int tileY = Integer.parseInt(msg.nextArgument());
			
		comm.getSpaceInstance().lookTo(comm.clientID, tileX, tileY);
	}
}
