package net.nillus.mobilesdisco.communication.requests;

import com.blunk.communication.ClientMessage;
import com.blunk.communication.ClientRequestHandler;
import com.blunk.communication.CommunicationHandler;

public class GOAWAY implements ClientRequestHandler
{
	public void handle(ClientMessage msg, CommunicationHandler comm)
	{
		comm.quitSpace("leave via door");
	}
}
