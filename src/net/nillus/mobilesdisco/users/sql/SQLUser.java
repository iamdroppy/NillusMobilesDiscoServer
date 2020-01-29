package net.nillus.mobilesdisco.users.sql;

import java.sql.*;

import net.nillus.mobilesdisco.users.User;

import com.blunk.storage.DatabaseException;
import com.blunk.storage.sql.SQLDataObject;

public class SQLUser extends User implements SQLDataObject
{
	@Override
	public void delete(Connection conn) throws DatabaseException
	{
		// Delete
	}

	@Override
	public void insert(Connection conn) throws DatabaseException
	{
		try
		{
			PreparedStatement command = conn.prepareStatement(
					"INSERT INTO users" + 
					"(name,password,email,age,pants,shirt,head,connection,sex,customdata,registered) " +
					"VALUES " +
					"(?,?,?,?,?,?,?,?,?,?,?);");
			this.prepareUserParamsStatement(command);
		
			command.execute();
		}
		catch(Exception ex)
		{
			throw new DatabaseException(ex.getMessage());
		}
	}

	@Override
	public void update(Connection conn) throws DatabaseException 
	{
		
	}

	private void prepareUserParamsStatement(PreparedStatement command) throws Exception
	{
		command.setString(1, super.name);
		command.setString(2, super.password);
		command.setString(3, super.email);
		command.setInt(4, super.age);
		command.setInt(5, super.pants);
		command.setInt(6, super.shirt);
		command.setInt(7, super.head);
		command.setString(8, super.connectionType);
		command.setString(9, Character.toString(super.sex));
		command.setString(10, super.customData);
		command.setString(11, super.registered);
	}
	
	@Override
	public long getCacheKey()
	{
		return 0;
	}
}
