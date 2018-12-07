package logic;

public class NetworkMessage
{
	private MessageType type;
	private String message;

	public NetworkMessage(MessageType type, String message)
	{
		this.type = type;
		this.message = message;
	}

	public MessageType getType()
	{
		return type;
	}

	public String getMessage()
	{
		return message;
	}

	public enum MessageType
	{
		UPDATE_FILE_LIST,
		SEND_FILE,
		DISCONNECT;
	}

	@Override
	public String toString()
	{
		return type + "\n" + message + "\n";
	}
}
