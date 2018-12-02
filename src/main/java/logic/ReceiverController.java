package logic;

import network.SocketReceiver;
import window.AppLogger;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReceiverController implements Closeable
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private SocketReceiver socketReceiver;
	private UIEventTransmitter eventTransmitter;

	private SocketReceiverListener listener;


	public ReceiverController(Socket socket, UIEventTransmitter eventTransmitter)
	{
		assert null != socket && socket.isConnected(): "Invalid socket for construction";
		assert null != eventTransmitter : "Invalid UIEventTransmitter for construction";

		this.eventTransmitter = eventTransmitter;
		try
		{
			this.socketReceiver = new SocketReceiver(socket);
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Failed to construct a SocketReceiver" + e.getMessage());
//			e.printStackTrace();
		}

		startListening();
	}

	private void messagesPending()
	{
		String message = socketReceiver.getLine();

		switch (message)
		{
			case "UPDATE_FILE_LIST":
			{
				String line = socketReceiver.getLine();
				LOGGER.log(Level.ALL, "Received remote file list update: " + line);

				line = line.substring(1, line.length() - 1);
				eventTransmitter.updateRemoteFileList(Arrays.asList(line.split(", ")));

			}
			break;
			case "SEND_FILE":
			{
				String line = socketReceiver.getLine();
				LOGGER.log(Level.ALL, "Received remote file request: " + line);
			}
			break;
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
			while (!isInterrupted())
			{
				if (socketReceiver.hasMessage())
					messagesPending();

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
