package net.nillus.mobilesdisco.net;

import com.blunk.net.TcpConnection;
import com.blunk.net.TcpConnectionManager;
import com.blunk.communication.*;

public class DiscoTcpConnection extends TcpConnection
{
	private CommunicationHandler commHandler;
	private boolean statusOK = false;
	
	public DiscoTcpConnection(int connectionID, int clientID, TcpConnectionManager connMgr)
	{
		super(connectionID, clientID, connMgr);
		this.commHandler = new CommunicationHandler(this);
	}
	
	public CommunicationHandler getCommunicator()
	{
		return this.commHandler;
	}
	
	@Override
	protected void handleConnectionShutdown()
	{
		commHandler.quitSpace("disconnect");
	}
	
	@Override
	protected void newData(char[] data)
	{
		// Try to parse ClientMessage object
		ClientMessage msg = ClientMessage.parse(data);
		if (msg != null)
		{
			if(msg.getType().equals("STATUSOK"))
				this.statusOK = true;
			else
			{
				this.commHandler.handleClientMessage(msg);
			}
		}
	}

	public boolean statusOK()
	{
		if(this.statusOK)
		{
			this.statusOK = false;
			return true;
		}
		
		return true;
	}
}
