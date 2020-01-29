package com.blunk.communication;

import java.util.Hashtable;

import com.blunk.Log;

public class ClientRequestHandlerManager
{
	private CommunicationHandler commHandler;
	private Hashtable<String, ClientRequestHandler> requestHandlers;
	
	public ClientRequestHandlerManager(CommunicationHandler comm)
	{
		this.commHandler = comm;
		this.requestHandlers = new Hashtable<String, ClientRequestHandler>(20);
	}
	
	public void registerRequestHandler(ClientRequestHandler handler)
	{
		this.requestHandlers.put(handler.getClass().getSimpleName(), handler);
	}
	
	public void deRegisterRequestHandler(String msgType)
	{
		this.requestHandlers.remove(msgType);
	}
	
	public void handleRequest(ClientMessage msg)
	{
		invokeHandler(msg.getType(), msg);
	}
	
	/**
	 * Tries to invoke the handle() method on the ClientRequestHandler of a given msg type. No
	 * message is passed to the invoking method!
	 * 
	 * @param msgType The type of the message (eg, GET_CREDITS) to invoke the registered request
	 * handler for.
	 */
	public void callHandler(String msgType)
	{
		invokeHandler(msgType, null);
	}
	
	private void invokeHandler(String msgType, ClientMessage msg)
	{
		ClientRequestHandler handler = this.requestHandlers.get(msgType);
		if (handler != null)
			handler.handle(msg, this.commHandler);
		else
		{
			Log.info("ClientRequestHandlerManager for " + commHandler.getConnection().toString() + " has no client request handler for " + msgType);
		}
	}
	
	public void clear()
	{
		this.requestHandlers.clear();
	}
}
