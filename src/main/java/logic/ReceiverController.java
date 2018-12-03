package logic;

import network.NetworkMessage;
import network.SocketMessageReceiver;
import network.SocketReceiver;
import window.AppLogger;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReceiverController implements Closeable
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private SocketMessageReceiver socketMessageReceiver;
	private BusinessEvents businessEvents;

	private Socket mainSocket;
	private SocketReceiverListener listener;


	public ReceiverController(Socket socket, BusinessEvents eventTransmitter)
	{
		assert null != socket && socket.isConnected(): "Invalid socket for construction";
		assert null != eventTransmitter : "Invalid BusinessEvents for construction";

		this.mainSocket = socket;
		this.businessEvents = eventTransmitter;
		try
		{
			this.socketMessageReceiver = new SocketMessageReceiver(new SocketReceiver(socket.getInputStream()));
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Failed to construct a SocketReceiver" + e.getMessage());
//			e.printStackTrace();
		}

		startListening();
	}

	private void checkMessages()
	{
		NetworkMessage networkMessage = socketMessageReceiver.pullMessage();

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
							filePath, mainSocket.getInetAddress().toString(), mainSocket.getPort() + 1));

					new FileTransmitterController(filePath,
							mainSocket.getInetAddress().toString().substring(1), mainSocket.getPort() + 1).start();
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
