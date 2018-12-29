package logic.messaging.messages;

public interface NetworkMessage
{
	String CODE_DELIMITER = "<D:::::D>";

	/**
	 * This is the message that is transmitted through  the network
	 * If you pass this string to the MessageFactory it's going to construct
	 * the same type of object
	 */
	String getFormattedMessage();

	String getMessageID();
}
