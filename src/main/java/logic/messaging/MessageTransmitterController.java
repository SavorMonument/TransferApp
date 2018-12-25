package logic.messaging;

import com.sun.istack.internal.NotNull;
import filesistem.FileException;
import filesistem.FileOutput;
import filetransfer.FileReceiver;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import logic.BusinessEvents;
import logic.ConnectCloseEvent;
import logic.connection.Connection;
import logic.connection.Connection.StringTransmitter;
import logic.connection.Connections;
import logic.messaging.messages.NetworkMessage;
import logic.messaging.messages.DownloadRequestMessage;
import logic.messaging.messages.UpdateFileListMessage;
import network.ConnectionException;
import window.AppLogger;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageTransmitterController
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private Connection fileConnection;

	private StringTransmitter messageTransmitter;
	private ConnectCloseEvent connectCloseEvent;

	private BusinessEvents businessEvents;

	public MessageTransmitterController(@NotNull Connections connections,
										@NotNull BusinessEvents businessEvents,
										@NotNull ConnectCloseEvent connectCloseEvent)
	{
		assert null != connections : "Null connections";
		assert null != businessEvents : "Null BusinessEvents";
		assert null != connectCloseEvent : "Null handler";

		this.fileConnection = connections.getFileReceivingConnection();
		this.businessEvents = businessEvents;
		this.connectCloseEvent = connectCloseEvent;

		this.messageTransmitter = connections.getMainConnection().getMessageTransmitter();

	}

	public void updateAvailableFileList(Set<File> files)
	{
		LOGGER.log(Level.FINE, "Sending file update..." + files.toString());

		Set<FileInformation> localFileInfo = transformFilesToFilesInformation(files);

		NetworkMessage networkMessage = new UpdateFileListMessage(localFileInfo);
		String message = networkMessage.getFormattedMessage();
		try
		{
			messageTransmitter.transmitString(message);
		} catch (ConnectionException e)
		{
			LOGGER.log(Level.WARNING, "Exception while transmitting file list: " + e.getMessage());
			connectCloseEvent.disconnect("Connection error, disconnecting...");
		}
	}

	private Set<FileInformation> transformFilesToFilesInformation(Set<File> files)
	{
		Set<FileInformation> fileInfo = new HashSet<>();
		for (File file : files)
		{
			fileInfo.add(new FileInformation(file.getName(), file.length()));
		}

		return fileInfo;
	}

	public void fileDownload(FileInformation fileInformation, String downloadPath)
	{
		FileOutput fileOutput = new FileOutput(fileInformation.name, downloadPath);
		String checkOutput = basicFileCheck(fileOutput, fileInformation);

		if (checkOutput.equals("Successful"))
		{
			LOGGER.log(Level.FINE, "Sending file download request: " + fileInformation);
			//Resolve message
			NetworkMessage networkMessage = new DownloadRequestMessage(fileInformation.name);
			String message = networkMessage.getFormattedMessage();
			try
			{
				//Transmit message to remote to tell it to upload the file
				messageTransmitter.transmitString(message);
			} catch (ConnectionException e)
			{
				LOGGER.log(Level.WARNING, e.getMessage());
				connectCloseEvent.disconnect("Connection error, disconnecting...");
			}
			startFileReceiving(fileInformation, fileOutput);
		} else
		{
			businessEvents.printMessageOnDisplay(checkOutput);
			LOGGER.log(Level.FINE, "Could not request file: " + checkOutput);
		}
	}

	private String basicFileCheck(FileOutput fileOutput, FileInformation fileInformation)
	{
		if (fileOutput.exists())
			return "File already exists";

		if (fileOutput.diskSpaceAtLocation() < fileInformation.sizeInBytes)
			return "Not enough space on device";

		return "Successful";
	}

	private void startFileReceiving(FileInformation fileInformation, FileOutput fileOutput)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				businessEvents.setDownloadingState(true);

//				FileOutput fileOutput = new FileOutput(fileInformation.name, downloadPath);
				FileReceiver fileReceiver = new FileReceiver((TransferInput) fileConnection.getMessageReceiver(),
						(TransferOutput) fileConnection.getMessageTransmitter(),
						fileOutput, fileInformation.sizeInBytes);

				LOGGER.log(Level.FINE, String.format("Starting file receiver with file: %s from address: %s, port%d",
						fileInformation.name, fileConnection.getRemoteAddress(), fileConnection.getRemotePort()));

				try
				{
					businessEvents.printMessageOnDisplay("Attempting file download");

					fileReceiver.transfer();

					businessEvents.printMessageOnDisplay("File downloaded successfully");
				} catch (FileException e)
				{
					fileOutput.abort();
					LOGGER.log(Level.WARNING, e.toString() + e.getMessage());
					businessEvents.printMessageOnDisplay("File error, disconnecting...");
					connectCloseEvent.disconnect(e.getMessage());
				} catch (ConnectionException e)
				{
					fileOutput.abort();
					LOGGER.log(Level.WARNING, e.toString() + e.getMessage());
					businessEvents.printMessageOnDisplay("Connection error, disconnecting...");
					connectCloseEvent.disconnect(e.getMessage());
				} finally
				{
					fileOutput.close();
					businessEvents.setDownloadingState(false);
				}
			}
		}).start();
	}
}
