package net.nillus.mobilesdisco.communication.requests;

import com.blunk.communication.*;
import net.nillus.mobilesdisco.MobilesDisco;
import net.nillus.mobilesdisco.users.User;

public class REGISTER implements ClientRequestHandler
{
	public void handle(ClientMessage msg, CommunicationHandler comm)
	{
		// REGISTER <name> <password> <email> <pants>,<shirt>,<head> <conntype> x <sex> <spam me yes/no> <customData>
		
		try
		{
			User usr = (User)MobilesDisco.getDataObjectFactory().newObject("User");
			
			usr.name = msg.nextArgument();
			usr.password = msg.nextArgument();
			usr.email = msg.nextArgument();
		
			String[] figureData = msg.nextArgument().split(",");
			usr.pants = Integer.parseInt(figureData[0]);
			usr.shirt = Integer.parseInt(figureData[1]);
			usr.head = Integer.parseInt(figureData[2]);
		
			usr.connectionType = msg.nextArgument();
			msg.nextArgument(); // 'x'
			usr.sex = (msg.nextArgument().equals("Male") ? 'M' : 'F');
			msg.nextArgument(); // Spam me yes/no
			usr.age = Integer.parseInt(msg.nextArgument());
			
			usr.customData = msg.getRemainingBody();
			
			// Try to register
			MobilesDisco.getUserRegister().registerUser(usr);
			
			// And try to login
			comm.Login(usr.name, usr.password);
		}
		catch(Exception ex)
		{
			comm.response.set("ERROR");
			comm.response.appendArgument("Bad register data!");
			comm.sendResponse();
		}
	}
}
