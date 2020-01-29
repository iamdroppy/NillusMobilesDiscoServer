package net.nillus.mobilesdisco.spaces;

import net.nillus.mobilesdisco.MobilesDisco;

import com.blunk.Environment;
import com.blunk.Log;
import com.blunk.storage.DataQueryResults;

public class SpaceManager
{
	private SpaceInstance[] spaces;
	
	public SpaceManager()
	{
		this.spaces = new SpaceInstance[0];
	}
	
	public void initSpaces()
	{
		Log.info("Initializing spaces from database...");
		
		SpacesLoader loader = (SpacesLoader) MobilesDisco.getDataQueryFactory().newQuery("SpacesLoader");
		DataQueryResults<SpaceData> result = (DataQueryResults<SpaceData>) Environment.getDatabase().query(loader);
		
		this.spaces = new SpaceInstance[result.size()];
		for(int i = 0; i < this.spaces.length; i++)
		{
			SpaceInstance space = new SpaceInstance(result.get(i));
			space.start();
			this.spaces[i] = space;
			
			Log.info("Initialized space " + space.getSpaceData().ID + " [" + space.getSpaceData().name + "]");
		}
		
		Log.info("Initialized " + this.spaces.length + " spaces.");
	}
	
	public SpaceInstance getSpace(int spaceID)
	{
		for(int i = 0; i < this.spaces.length; i++)
		{
			if(spaces[i] != null && spaces[i].getSpaceData().ID == spaceID)
				return spaces[i];
		}
		
		return null;
	}
	
	public int resolveSpaceIdByName(String name)
	{
		name = name.toLowerCase();
		for(SpaceInstance space : this.spaces)
		{
			String spacename = space.getSpaceData().name.toLowerCase();
			if(spacename.equals(name))
				return space.getSpaceData().ID;
		}
		
		return -1;
	}
	
	public void writeLoadLog()
	{
		
	}
	
	public SpaceInstance[] getSpaces()
	{
		return this.spaces;
	}
}
