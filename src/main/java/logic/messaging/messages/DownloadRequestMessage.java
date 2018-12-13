package logic.messaging.messages;

import filesistem.FileException;
import filesistem.FileInput;
import filetransfer.FileTransmitter;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import logic.BusinessEvents;
import logic.connection.Connection;
import logic.messaging.ConnectionException;
import window.AppLogger;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DownloadRequestMessage implements NetworkMessage
{
	private static final Logger LOGGER = AppLogger.getInstance();
	static final String MESSAGE_CODE = "1234567";

	private String fileName;
	private Connection fileConnection;

	public DownloadRequestMessage(String fileName)
	{
		this.fileName = fileName;
	}

	DownloadRequestMessage(String message, Connection fileConnection)
	{
		fileName = message;
		this.fileConnection = fileConnection;
	}


	@Override
	public String getFormattedMessage()
	{
		LOGGER.log(Level.FINE, "Sending file download request: " + fileName);

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder
				.append(MESSAGE_CODE)
				.append(CODE_DELIMITER)
				.append(fileName)
				.append('\n');

		return stringBuilder.toString();
	}

	@Override
	public void doAction(BusinessEvents businessEvents) throws ConnectionException
	{
		String filePath = businessEvents.getLocalFilePath(fileName);

		initiateFileTransfer(businessEvents, filePath);
	}

	private void initiateFileTransfer(BusinessEvents businessEvents, String filePath) throws ConnectionException
	{
		FileInput fileInput = new FileInput(filePath);
		FileTransmitter fileTransmitter = new FileTransmitter(
				(TransferOutput) fileConnection.getMessageTransmitter(),
				(TransferInput) fileConnection.getMessageReceiver(),
				fileInput);
		try
		{
			LOGGER.log(Level.FINE, String.format("Starting file transmitter with file: %s to address: %s, port%d",
					filePath, fileConnection.getRemoteAddress(), fileConnection.getRemotePort()));
			businessEvents.printMessageOnDisplay("Attempting file upload");

			fileTransmitter.transfer();

			businessEvents.printMessageOnDisplay("File upload finished");
			LOGGER.log(Level.FINE, "File upload finished");
		} catch (FileNotFoundException | FileException e)
		{
			LOGGER.log(Level.WARNING, "Input file problem: " + e.getMessage());
			businessEvents.printMessageOnDisplay("File error");
		} finally
		{
			fileInput.close();
		}
	}
}
