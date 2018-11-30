package network;

import window.AppLogger;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionResolver
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private ConnectionListener connectionListener;


	public Socket establishConnection(String url, int port)
	{
		LOGGER.log(Level.INFO, "Received Connection establish request on URL: " + url);
		Socket socket = null;
		try
		{
			socket = new Socket(url, port);
			LOGGER.log(Level.INFO, String.format("Connection successful on URL: %s", url));
		} catch (IOException e)
		{
			LOGGER.log(Level.INFO, String.format("Connection unsuccessful on URL: %s\n%s", url, e.getMessage()));
		}

		return socket;
	}

	public boolean isListening()
	{
		return null != connectionListener && connectionListener.isAlive();
	}

	public void stopListening()
	{
		connectionListener.interrupt();
	}

	public void startListening(ConnectionListener.ConnectionReceivedEvent networkListener, int port)
	{
		assert !isListening();

		connectionListener = new ConnectionListener(networkListener, port);
		connectionListener.start();
	}
}
