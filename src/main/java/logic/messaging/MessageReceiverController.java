package logic.messaging;

import com.sun.istack.internal.NotNull;
import filesistem.FileInput;
import filetransfer.FileTransmitter;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import logic.api.BusinessEvents;
import logic.ConnectCloseEvent;
import logic.api.Connection;
import logic.api.Connection.MessageReceiver;
import window.AppLogger;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageReceiverController
{
	private static final Logger LOGGER = AppLogger.getInstance();
	//TODO: Have this passed trough the main socket(so you can have multiple file transferring at the sam time)

	private Connection mainConnection;
	private Connection fileConnection;

	private MessageReceiver messageReceiver;
	private BusinessEvents businessEvents;
	private ConnectCloseEvent connectEvent;

	private MessageReceiverThread listener;


	public MessageReceiverController(@NotNull Connection mainConnection,
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

		this.messageReceiver = mainConnection.getMessageReceiver();
		this.businessEvents = businessEvents;
		this.connectEvent = connectEvent;

		fileConnection.getMessageTransmitter().registerBytesCounter(
				new ByteCounter(businessEvents::printUploadSpeed, 500));
	}

	private void checkMessages()
	{
		NetworkMessage networkMessage = null;
		try
		{
			networkMessage = messageReceiver.pullMessageBlocking();
		} catch (IOException e)
		{
			stopListening();
			connectEvent.disconnect(e.getMessage());
//			e.printStackTrace();
		}

		if (null != networkMessage)
		{
			switch (networkMessage.getType())
			{
				case UPDATE_FILE_LIST:
				{
					LOGGER.log(Level.ALL, "Received remote file list update: " + networkMessage.getMessage());
					businessEvents.printMessageOnDisplay("Updating file list");
					String message = networkMessage.getMessage();
					Set<FileInformation> remoteFiles = (Set<FileInformation>) NetworkMessage.collectionDecoder(message);

					businessEvents.updateRemoteFileList(remoteFiles);
				}
				break;
				case SEND_FILE:
				{
					String fileName = networkMessage.getMessage();
					String filePath = businessEvents.getLocalFilePath(fileName);

					LOGGER.log(Level.FINE, String.format("Received file transfer request\n " +
									"Starting file transmitter with file: %s to address: %s, port%d",
							filePath, fileConnection.getRemoteAddress(), fileConnection.getRemotePort()));

					businessEvents.printMessageOnDisplay("Started a file transmission: " + fileName);

					initiateFileTransfer(filePath);
				}
				break;
				case DISCONNECT:
				{
					stopListening();
					connectEvent.disconnect("Disconnect received");
				}
				break;
			}
		}
	}

	private void initiateFileTransfer(String filePath)
	{
		new Thread(() ->
		{
			FileInput fileInput = new FileInput(filePath);
			boolean successful = new FileTransmitter(
					(TransferOutput) fileConnection.getMessageTransmitter(),
					(TransferInput) fileConnection.getMessageReceiver(),
					fileInput).transfer();
			fileInput.close();
			LOGGER.log(Level.ALL, "File transmission " + (successful ? "successful." : "unsuccessful"));

			if (successful)
				businessEvents.printMessageOnDisplay("File transfer successful: " + filePath);
			else
				businessEvents.printMessageOnDisplay("File transfer unsuccessful: " + filePath);

		}).start();
	}


	public void startListening()
	{
		listener = new MessageReceiverThread();

		listener.setDaemon(true);
		listener.start();
	}

	public void stopListening()
	{
		listener.interrupt();
	}

	class MessageReceiverThread extends Thread
	{
		@Override
		public void run()
		{
			LOGGER.log(Level.ALL, "Started listening for messages on the socket");
			while (!isInterrupted())
			{
				checkMessages();
			}
		}
	}
}
