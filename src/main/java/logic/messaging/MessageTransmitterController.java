package logic.messaging;

import com.sun.istack.internal.NotNull;
import filesistem.FileOutput;
import filetransfer.FileReceiver;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import logic.api.BusinessEvents;
import logic.ConnectCloseEvent;
import logic.api.Connection;
import logic.api.Connection.MessageTransmitter;
import window.AppLogger;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageTransmitterController
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private Connection mainConnection;
	private Connection fileConnection;

	private MessageTransmitter messageTransmitter;
	private ConnectCloseEvent connectEvent;

	private BusinessEvents businessEvents;

	public MessageTransmitterController(@NotNull Connection mainConnection,
										@NotNull Connection fileConnection,
										@NotNull BusinessEvents businessEvents,
										@NotNull ConnectCloseEvent connectEvent)
	{
		assert null != mainConnection && mainConnection.isConnected() : "Invalid main connection";
		assert null != fileConnection && fileConnection.isConnected() : "Invalid main connection";
		assert null != businessEvents : "Invalid BusinessEvents";
		assert null != connectEvent : "Need an actual event handler";

		this.mainConnection = mainConnection;
		this.fileConnection = fileConnection;

		this.messageTransmitter = mainConnection.getMessageTransmitter();
		this.connectEvent = connectEvent;

		this.businessEvents = businessEvents;

		fileConnection.getMessageReceiver().registerBytesCounter(
				new ByteCounter(businessEvents::printDownloadSpeed, 500));
	}

	public void updateAvailableFileList(Set<File> files)
	{
		LOGGER.log(Level.FINE, "Sending file update..." + files.toString());

		Set<FileInformation> localFileInfo = getListOfFileInformation(files);

		NetworkMessage networkMessage = new NetworkMessage(NetworkMessage.MessageType.UPDATE_FILE_LIST,
				NetworkMessage.collectionCoder(localFileInfo));

		try
		{
			messageTransmitter.transmitMessage(networkMessage);
		} catch (IOException e)
		{
			connectEvent.disconnect(e.getMessage());
		}

	}

	private Set<FileInformation> getListOfFileInformation(Set<File> files)
	{
		Set<FileInformation> fileInfo = new HashSet<>();

		for (File file : files)
		{
			fileInfo.add(new FileInformation(file.getName(), file.length()));
		}

		return fileInfo;
	}

	public void requestFileForDownload(FileInformation fileInformation, String downloadPath)
	{
		LOGGER.log(Level.FINE, "Sending file download request: " + fileInformation);

		try
		{
			//Transmit message to remote to tell it to upload the file
			NetworkMessage networkMessage = new NetworkMessage(NetworkMessage.MessageType.SEND_FILE, fileInformation.name);
			messageTransmitter.transmitMessage(networkMessage);
		} catch (IOException e)
		{
			connectEvent.disconnect(e.getMessage());
		}

		LOGGER.log(Level.FINE, String.format("Starting file receiver with file: %s from address: %s, port%d",
				fileInformation.name, fileConnection.getRemoteAddress(), fileConnection.getRemotePort()));

		businessEvents.printMessageOnDisplay("Attempting file download");
		businessEvents.setDownloadState(true);
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				FileOutput fileOutput = new FileOutput(fileInformation.name, downloadPath);
				boolean successful =
						new FileReceiver((TransferInput) fileConnection.getMessageReceiver(),
								(TransferOutput) fileConnection.getMessageTransmitter(),
								fileOutput, fileInformation.sizeInBytes).transfer();
				fileOutput.close();
				LOGGER.log(Level.ALL, "File transmission " + (successful ? "successful." : "unsuccessful"));
				if (successful)
				{
					businessEvents.printMessageOnDisplay("File downloaded successfully");
					//TODO: notify UI
				}else
					businessEvents.printMessageOnDisplay("Error while downloading");
				businessEvents.setDownloadState(false);
			}
		}).start();
	}

	public void transmitDisconnectMessage()
	{
		try
		{
			NetworkMessage networkMessage = new NetworkMessage(NetworkMessage.MessageType.DISCONNECT, "");
			messageTransmitter.transmitMessage(networkMessage);
		} catch (IOException e)
		{
			connectEvent.disconnect(e.getMessage());
		}
	}
}
