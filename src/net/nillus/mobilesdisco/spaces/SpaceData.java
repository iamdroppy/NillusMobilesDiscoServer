package net.nillus.mobilesdisco.spaces;

public class SpaceData
{
	public int ID;
	public String name;
	
	public int decorID;
	public int maxUsers = 100;
	
	public int doorX;
	public int doorY;
	public int doorZ;
	
	public String heightmap;
	
	public SpaceData()
	{
		
	}
	
	public SpaceData(int ID, String name, int decorID, int maxUsers, int doorX, int doorY, int doorZ, String heightmap)
	{
		this.ID = ID;
		this.name = name;
		this.decorID = decorID;
		this.maxUsers = maxUsers;
		this.doorX = doorX;
		this.doorY = doorY;
		this.doorZ = doorZ;
		this.heightmap = heightmap;
	}
}
