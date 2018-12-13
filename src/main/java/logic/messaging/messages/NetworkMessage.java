package logic.messaging.messages;

import logic.BusinessEvents;
import logic.messaging.ConnectionException;

public interface NetworkMessage
{
	String CODE_DELIMITER = "<D:::::D>";

	/**
	 * This is the message that is transmitted through  the network
	 * If you pass this string to the MessageFactory it's going to construct
	 * the same type of object
	 */
	String getFormattedMessage();

	/**
	 * Does what each specific message is supposed to do
	 */
	void doAction(BusinessEvents businessEvents) throws ConnectionException;
}
