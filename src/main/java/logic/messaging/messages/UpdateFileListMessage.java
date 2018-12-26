package logic.messaging.messages;

import logic.BusinessEvents;
import window.AppLogger;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateFileListMessage implements NetworkMessage
{
	private static final Logger LOGGER = AppLogger.getInstance();
	static final String MESSAGE_ID = "1234568";

	Collection<FileInfo> filesInformation;

	public UpdateFileListMessage(Collection<FileInfo> files)
	{
		filesInformation = files;
	}

	UpdateFileListMessage(String files)
	{
		filesInformation = collectionDecoder(files);
	}

	@Override
	public String getFormattedMessage()
	{
		LOGGER.log(Level.FINE, "Sending file update..." + filesInformation.toString());

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder
				.append(MESSAGE_ID)
				.append(CODE_DELIMITER)
				.append(collectionCoder(filesInformation))
				.append('\n');

		return stringBuilder.toString();
	}

	@Override
	public void doAction(BusinessEvents businessEvents)
	{
		LOGGER.log(Level.ALL, "Received remote file list update: " + filesInformation.toString());
		businessEvents.printMessageOnDisplay("Updating file list");

		businessEvents.updateRemoteFileList((List<FileInfo>) filesInformation);
	}

	public static String collectionCoder(Collection<FileInfo> collection)
	{
		StringBuilder convertedMessage = new StringBuilder("[");


		Iterator<FileInfo> it = collection.iterator();
		while (it.hasNext())
		{
			FileInfo info = it.next();
			convertedMessage
					.append("\"")
					.append(info.getName())
					.append("|")
					.append(info.getSize())
					.append("\"");
			if (it.hasNext())
				convertedMessage.append(" ");
		}
		convertedMessage.append("]");

		return convertedMessage.toString();
	}

	public static Collection<FileInfo> collectionDecoder(String codedMessage)
	{
		List<FileInfo> elem = new ArrayList<>();

		if (!codedMessage.equals("[]"))
		{
			codedMessage = codedMessage.substring(2, codedMessage.length() - 2);
			String[] tokens = codedMessage.split("\" \"");

			for (int i = 0; i < tokens.length; i++)
			{
				String[] info = tokens[i].split("\\|");
				elem.add(new FileInfo(info[0], Long.valueOf(info[1])));
			}
		}
		return elem;
	}
}
