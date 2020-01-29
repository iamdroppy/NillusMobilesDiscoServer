package net.nillus.mobilesdisco.communication.requests;

import com.blunk.communication.ClientMessage;
import com.blunk.communication.ClientRequestHandler;
import com.blunk.communication.CommunicationHandler;

public class CHAT implements ClientRequestHandler
{
	public void handle(ClientMessage msg, CommunicationHandler comm)
	{
		String text = msg.getBody();
		if(!comm.handleChatCommand(text))
			comm.getSpaceInstance().chat(comm.clientID, text);
	}
}
