package logic;

import filetransfer.FileTransmitterController;
import network.SocketMessageReceiver;
import window.AppLogger;

import java.io.Closeable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReceiverController implements Closeable
{
	private static final Logger LOGGER = AppLogger.getInstance();
	//TODO: Have this passed trough the main socket(so you can have multiple file transferring at the sam time)
	private static final int FILE_PORT = 59_901;

	private SocketMessageReceiver messageReceiver;
	private BusinessEvents businessEvents;

	private SocketReceiverListener listener;


	public ReceiverController(SocketMessageReceiver messageReceiver, BusinessEvents eventTransmitter)
	{
		assert null != eventTransmitter : "Invalid BusinessEvents for construction";

		this.businessEvents = eventTransmitter;
		this.messageReceiver = messageReceiver;

		startListening();
	}

	private void checkMessages()
	{
		NetworkMessage networkMessage = messageReceiver.pullMessage();

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
					message = message.substring(1, message.length() - 1);
					businessEvents.updateRemoteFileList(Arrays.asList(message.split(", ")));

				}
				break;
				case SEND_FILE:
				{
					String fileName = networkMessage.getMessage();
					String filePath = businessEvents.getLocalFilePath(fileName);

					LOGGER.log(Level.FINE, String.format("Received file transfer request\n " +
							"Starting file transmitter with file: %s on address: %s, port%d",
							filePath, messageReceiver.getSocketIPAddress(), FILE_PORT));

					new FileTransmitterController(filePath,
							messageReceiver.getSocketIPAddress(), FILE_PORT).start();
				}
				break;
			}
		}
	}

	private void startListening()
	{
		listener = new SocketReceiverListener();

		listener.setDaemon(true);
		listener.start();
	}

	@Override
	public void close()
	{
		stopListening();
	}

	private void stopListening()
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
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
