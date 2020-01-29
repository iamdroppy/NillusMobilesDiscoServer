package net.nillus.mobilesdisco.spaces.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import net.nillus.mobilesdisco.spaces.SpaceData;
import net.nillus.mobilesdisco.spaces.SpacesLoader;

import com.blunk.Log;
import com.blunk.storage.DataQueryResults;
import com.blunk.storage.DatabaseException;
import com.blunk.storage.sql.SQLDataQuery;

public class SQLSpacesLoader implements SpacesLoader, SQLDataQuery {

	@Override
	public void execute(Connection conn) throws DatabaseException
	{

	}

	@Override
	public DataQueryResults<SpaceData> query(Connection conn) throws DatabaseException
	{
		DataQueryResults<SpaceData> spaces = new DataQueryResults<SpaceData>(5);
		
		try
		{
		Statement command = conn.createStatement();
		ResultSet dbResult = command.executeQuery("SELECT * FROM spaces ORDER BY id ASC");
		
		while(dbResult.next())
		{
			try
			{
				SpaceData space = new SpaceData();
				space.ID = dbResult.getInt("id");
				space.name = dbResult.getString("name");
				space.maxUsers = dbResult.getInt("maxusers");
				space.decorID = dbResult.getInt("decorid");
				space.doorX = dbResult.getInt("doorx");
				space.doorY = dbResult.getInt("doory");
				space.doorZ = dbResult.getInt("doorz");
				space.heightmap = dbResult.getString("heightmap").replace("|", "\r");
				
				spaces.add(space);
			}
			catch(Exception ex)
			{
				
			}
		}
		}
		catch(Exception ex)
		{
			Log.error("Error in SQLSpacesLoader.query", ex);
			throw new DatabaseException(ex.toString());
		}
		
		return spaces;
	}
}
