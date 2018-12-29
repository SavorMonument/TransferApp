package logic.messaging.messages;

import com.google.gson.Gson;
import model.FileInfo;
import window.AppLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TransferRequestMessage implements NetworkMessage
{
	private static final Logger LOGGER = AppLogger.getInstance();
	public static final String MESSAGE_ID = "1234567";

	private FileInfo fileInfo;

	public TransferRequestMessage(FileInfo fileInfo)
	{
		this.fileInfo = fileInfo;
	}

	TransferRequestMessage(String message)
	{
		String temp = message;

		fileInfo = new Gson().fromJson(message, FileInfo.class);
	}

	@Override
	public String getFormattedMessage()
	{
		LOGGER.log(Level.FINE, "Sending file download request: " + fileInfo.getName());

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder
				.append(MESSAGE_ID)
				.append(CODE_DELIMITER)
				.append(new Gson().toJson(fileInfo, FileInfo.class))
				.append('\n');
		return stringBuilder.toString();
	}

	@Override
	public String getMessageID()
	{
		return MESSAGE_ID;
	}

	public FileInfo getFileInfo()
	{
		return fileInfo;
	}
}
