package net.nillus.mobilesdisco.users;

import net.nillus.mobilesdisco.MobilesDisco;

import com.blunk.Environment;
import com.blunk.Log;
import com.blunk.storage.DataQueryResults;

public class UserRegister
{
	public void registerUser(User usr)
	{
		usr.registered = Environment.getCurrentDateString();
		
		Log.info("REGISTER user '" + usr.name + "'");
		Environment.getDatabase().insert(usr);
	}
	
	public User loadUser(String name)
	{
		// Prepare the query
		UserLoader loader = (UserLoader) MobilesDisco.getDataQueryFactory().newQuery("UserLoader");
		loader.byName(name);
		
		// Run the query
		DataQueryResults<?> result = Environment.getDatabase().query(loader);
		
		// If result == null, then return null, else: return the user object
		return result != null ? (User)result.get(0) : null;
	}
}
