package net.nillus.mobilesdisco.users.sql;

import java.sql.Connection;
import java.sql.ResultSet;

import net.nillus.mobilesdisco.users.User;
import net.nillus.mobilesdisco.users.UserLoader;

import com.blunk.Log;
import com.blunk.storage.DataQueryResults;
import com.blunk.storage.DatabaseException;
import com.blunk.storage.sql.SQLDataQuery;
import com.mysql.jdbc.PreparedStatement;

public class SQLUserLoader implements UserLoader, SQLDataQuery {

	private String name;
	public void byName(String name)
	{
		this.name = name;
	}

	@Override
	public void execute(Connection conn) throws DatabaseException
	{
		
	}

	@Override
	public DataQueryResults<User> query(Connection conn) throws DatabaseException
	{
		try
		{
		// Prepare and run the query
		java.sql.PreparedStatement command = conn.prepareStatement("SELECT * FROM users WHERE name = ? LIMIT 1;");
		command.setString(1, this.name);
		ResultSet dbResult = command.executeQuery();
		
		// Evaluate the result
		if(dbResult.next())
		{
			User usr = new SQLUser();
			usr.name = dbResult.getString("name");
			usr.password = dbResult.getString("password");
			usr.email = dbResult.getString("email");
			usr.age = dbResult.getInt("age");
			usr.pants = dbResult.getInt("pants");
			usr.shirt = dbResult.getInt("shirt");
			usr.head = dbResult.getInt("head");
			usr.connectionType = dbResult.getString("connection");
			usr.sex = (dbResult.getString("sex") == "M" ? 'M' : 'F');
			usr.customData = dbResult.getString("customData");
			usr.registered = dbResult.getString("registered");
			
			DataQueryResults<User> result = new DataQueryResults<User>(1);
			result.add(usr);
			
			return result;
		}
	}
	catch(Exception ex)
	{
		Log.error("Error in SQLUserLoader.query", ex);
		throw new DatabaseException(ex.getMessage());
	}
	
	return null;
}
}
