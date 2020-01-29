package net.nillus.mobilesdisco.users;

import com.blunk.storage.DataQuery;

public interface UserLoader extends DataQuery
{
	public void byName(String name);
}
