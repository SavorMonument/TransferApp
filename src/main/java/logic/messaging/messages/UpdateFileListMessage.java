package logic.messaging.messages;

import logic.messaging.messages.NetworkMessage;
import model.FileInfo;
import window.AppLogger;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateFileListMessage implements NetworkMessage
{
	private static final Logger LOGGER = AppLogger.getInstance();
	public static final String MESSAGE_ID = "1234568";

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
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder
				.append(MESSAGE_ID)
				.append(CODE_DELIMITER)
				.append(collectionCoder(filesInformation))
				.append('\n');

		return stringBuilder.toString();
	}

	public String getMessageID()
	{
		return MESSAGE_ID;
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
					.append(info.getSizeInBytes())
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

	public Collection<FileInfo> getFilesInfos()
	{
		return filesInformation;
	}
}
