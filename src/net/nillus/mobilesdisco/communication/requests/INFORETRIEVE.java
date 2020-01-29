package net.nillus.mobilesdisco.communication.requests;

import net.nillus.mobilesdisco.MobilesDisco;
import net.nillus.mobilesdisco.users.User;

import com.blunk.communication.ClientMessage;
import com.blunk.communication.ClientRequestHandler;
import com.blunk.communication.CommunicationHandler;

public class INFORETRIEVE implements ClientRequestHandler
{
	public void handle(ClientMessage msg, CommunicationHandler comm)
	{
		String name = msg.nextArgument();
		String password = msg.nextArgument();
		
		User usr = MobilesDisco.getUserRegister().loadUser(name);
		if(usr != null && usr.password.equals(password))
		{
			comm.response.set("INFdO");
			comm.sendResponse();
		}
		else
		{
			comm.response.set("ERROR");
			comm.response.appendArgument("login in");
			comm.sendResponse();
		}
	}
}
