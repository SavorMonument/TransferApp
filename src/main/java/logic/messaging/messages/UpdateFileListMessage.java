package logic.messaging.messages;

import logic.messaging.FileInformation;

import java.util.*;
import java.util.logging.Level;

public class UpdateFileListMessage extends NetworkMessage
{
	private static final String MESSAGE_CODE = "1234567";

	public UpdateFileListMessage(Collection<FileInformation> files)
	{
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder
				.append(MESSAGE_CODE)
				.append(' ')
				.append(collectionCoder(files))
				.append('\n');

		message = stringBuilder.toString();
	}

	@Override
	void doAction()
	{

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

	public static Collection<FileInformation> collectionDecoder(String codedMessage)
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
