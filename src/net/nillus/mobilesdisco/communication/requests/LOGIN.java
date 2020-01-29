package net.nillus.mobilesdisco.communication.requests;

import com.blunk.communication.ClientMessage;
import com.blunk.communication.ClientRequestHandler;
import com.blunk.communication.CommunicationHandler;

public class LOGIN implements ClientRequestHandler
{
	public void handle(ClientMessage msg, CommunicationHandler comm)
	{
		String name = msg.nextArgument();
		String password = msg.nextArgument();
		
		comm.Login(name, password);
	}
}
