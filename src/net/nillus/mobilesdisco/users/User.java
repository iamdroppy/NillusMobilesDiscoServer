package net.nillus.mobilesdisco.users;

import com.blunk.storage.DataObject;

public abstract class User implements DataObject
{
	public String name;
	public String password;
	public String email;
	public int age;
	
	public int pants;
	public int shirt;
	public int head;
	
	public String connectionType;
	public char sex;
	public String customData;
	
	public String registered;
}
