package logic.messaging;

import java.util.*;

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

	public static String collectionCoder(Collection<FileInformation> collection)
	{
		StringBuilder convertedMessage = new StringBuilder("[");


		Iterator<FileInformation> it = collection.iterator();
		while (it.hasNext())
		{
			FileInformation info = it.next();
			convertedMessage
					.append("\"")
					.append(info.name)
					.append("|")
					.append(info.sizeInBytes)
					.append("\"");
			if (it.hasNext())
				convertedMessage.append(" ");
		}
		convertedMessage.append("]");

		return convertedMessage.toString();
	}

	public static Collection<FileInformation> listDecoder(String codedMessage)
	{
		Set<FileInformation> elem = new HashSet<FileInformation>();

		if (!codedMessage.equals("[]"))
		{
			codedMessage = codedMessage.substring(2, codedMessage.length() - 2);
			String[] tokens = codedMessage.split("\" \"");

			for (int i = 0; i < tokens.length; i++)
			{
				String[] info = tokens[i].split("\\|");
				elem.add(new FileInformation(info[0], Long.valueOf(info[1])));
			}
		}
		return elem;
	}
}
