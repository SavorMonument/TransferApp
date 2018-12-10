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
	private Connection fileReceivingConnection;

	private MessageReceiver messageReceiver;
	private BusinessEvents businessEvents;
	private ConnectCloseEvent connectEvent;

	private SocketReceiverListener listener;


	public MessageReceiverController(@NotNull Connection mainConnection, @NotNull Connection fileReceivingConnection, @NotNull BusinessEvents businessEvents, @NotNull ConnectCloseEvent connectEvent)
	{
		assert null != mainConnection && mainConnection.isConnected() : "Invalid main connection";
		assert null != fileReceivingConnection && fileReceivingConnection.isConnected() : "Invalid main connection";
		assert null != businessEvents : "Invalid BusinessEvents";
		assert null != connectEvent : "Need an actual event handler";

		this.mainConnection = mainConnection;
		this.fileReceivingConnection = fileReceivingConnection;

		this.messageReceiver = mainConnection.getMessageReceiver();
		this.businessEvents = businessEvents;
		this.connectEvent = connectEvent;
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

					//The files come as a string of comma separated values so this splits and parses them to a list
					//ex. [a, b, c]
					String message = networkMessage.getMessage();
					Collection<FileInformation> files = NetworkMessage.listDecoder(message);

					Set<String> formattedFileSet = new HashSet<>();
					for(FileInformation f: files)
					{
						formattedFileSet.add(String.format("%s : %d kiB", f.name, f.sizeInBytes / 1024));
					}

					businessEvents.updateRemoteFileList(formattedFileSet);

				}
				break;
				case SEND_FILE:
				{
					String fileName = networkMessage.getMessage();
					String filePath = businessEvents.getLocalFilePath(fileName);

					LOGGER.log(Level.FINE, String.format("Received file transfer request\n " +
									"Starting file transmitter with file: %s to address: %s, port%d",
							filePath, fileReceivingConnection.getRemoteAddress(), fileReceivingConnection.getRemotePort()));

					new Thread(() ->
					{
						boolean successful = new FileTransmitter(
								(TransferOutput) fileReceivingConnection.getMessageTransmitter(),
								(TransferInput) fileReceivingConnection.getMessageReceiver(),
								new FileInput(filePath)).transfer();

						LOGGER.log(Level.ALL, "File transmission " + (successful ? "successful." : "unsuccessful"));

						if (successful)
						{
							//TODO: Notify UI
						}
					});
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

	public void startListening()
	{
		listener = new SocketReceiverListener();

		listener.setDaemon(true);
		listener.start();
	}

	public void stopListening()
	{
		listener.interrupt();
	}

	class SocketReceiverListener extends Thread
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
