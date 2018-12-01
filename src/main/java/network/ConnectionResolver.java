package network;

import window.AppLogger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionResolver
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private ConnectionListener connectionListener;

	private ConnectionEvent connectionEvent;

	public ConnectionResolver(ConnectionEvent connectionEvent)
	{
		this.connectionEvent = connectionEvent;
	}

	public void attemptConnection(String url, int port)
	{
		LOGGER.log(Level.INFO, "Received Connection establish request on URL: " + url);
		Socket socket;
		try
		{
			socket = new Socket(url, port);
			LOGGER.log(Level.INFO, String.format("Connection successful on URL: %s", url));
			connectionEvent.connectionEstablished(socket);
		} catch (IOException e)
		{
			LOGGER.log(Level.INFO, String.format("Connection unsuccessful on URL: %s\n%s", url, e.getMessage()));
		}
	}

	public boolean isListening()
	{
		return null != connectionListener && connectionListener.isAlive();
	}

	public void stopListening()
	{
		assert isListening();

		try
		{
			if (!connectionListener.serverSocket.isClosed())
				connectionListener.serverSocket.close();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Could not close server socket for some reason\n" + e.getMessage());
//			e.printStackTrace();
		}
	}

	public void startListening(int port)
	{
		assert !isListening();

		connectionListener = new ConnectionListener(connectionEvent, port);
		connectionListener.start();
	}

	public interface ConnectionEvent
	{
		void connectionEstablished(Socket socket);
	}

	public static class ConnectionListener extends Thread
	{
		private ServerSocket serverSocket;
		private ConnectionEvent networkListener;
		private int port;

		public ConnectionListener()
		{
		}

		public ConnectionListener(ConnectionEvent networkListener, int port)
		{
			setDaemon(true);
			this.networkListener = networkListener;
			this.port = port;
		}

		@Override
		public void run()
		{
			try
			{
				serverSocket = new ServerSocket(port);
				System.out.println("Started listening on: " + InetAddress.getLocalHost());
				Socket socket = serverSocket.accept();
				networkListener.connectionEstablished(socket);
			} catch (IOException e)
			{
				e.printStackTrace();
			}


		}

	}
}
