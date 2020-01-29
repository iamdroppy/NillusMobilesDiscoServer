package net.nillus.mobilesdisco.spaces;

import com.blunk.communication.ServerMessage;

public class SpaceObject
{
	public String sprite;
	
	public int X;
	public int Y;
	public int Z;
	public int rotation;
	
	public boolean isSeat;
	
	public void object(ServerMessage msg)
	{
		msg.append("\r");
		msg.append(this.sprite);
		msg.appendArgument(Integer.toString(this.X));
		msg.appendArgument(Integer.toString(this.Y));
		msg.appendArgument(Integer.toString(this.rotation));
		msg.appendArgument(Integer.toString(this.Z));
	}
}
