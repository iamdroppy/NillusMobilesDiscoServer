package net.nillus.mobilesdisco.communication.requests;

import com.blunk.communication.ClientMessage;
import com.blunk.communication.ClientRequestHandler;
import com.blunk.communication.CommunicationHandler;

public class WHISPER implements ClientRequestHandler
{
	public void handle(ClientMessage msg, CommunicationHandler comm)
	{
		String user = msg.nextArgument();
		String text = msg.getRemainingBody();
		
		comm.getSpaceInstance().whisper(comm.clientID, user, text);
	}
}
