package logic.messaging.messages;

import network.messaging.SocketMessageTransmitter;

public abstract class NetworkMessage
{
	protected String message;

	abstract void doAction();

	public String getMessageAsString()
	{
		return message;
	}


}
