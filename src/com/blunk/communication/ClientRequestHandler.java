package com.blunk.communication;

public interface ClientRequestHandler
{
	public void handle(ClientMessage msg, CommunicationHandler comm);
}
