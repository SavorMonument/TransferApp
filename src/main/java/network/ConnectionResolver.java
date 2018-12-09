package network;

import logic.Connection;
import window.AppLogger;

import java.io.IOException;
import java.net.*;
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

	public void attemptConnection(InetAddress address, int port)
	{
		LOGGER.log(Level.ALL, String.format("Attempting connection to URL: %s, port: %d",
				address, port));
		Socket socket = new Socket();
		try
		{
//			socket.connect(new InetSocketAddress(address, port), 5000);

			socket = new Socket(address, port);
			LOGGER.log(Level.ALL, String.format("Connection successful to URL: %s, port: %d",
					address, port));
			connectionEvent.connectionAttemptSuccessful(new NetworkConnection(socket, new SocketMessageTransmitter(socket), new SocketMessageReceiver(socket)));
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, String.format("Connection unsuccessful to URL: %s, port: %s\n%s",
					address.getHostAddress(), port, e.getMessage()));
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

	/**
	 * Listens for a connection on the calling thread with a timeout
	 * <p>
	 * Calls connectionEstablished event on connection
	 */
	public void startListeningBlocking(int port, long timeOutMillis)
	{
		assert timeOutMillis > 0 : "Negative timeOut";
		assert port >= 0 && port < (Math.pow(2, 16)) : "Invalid port number";

		Thread countingThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(timeOutMillis);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				if (isListening())
					stopListening();
			}
		});

		countingThread.setDaemon(true);
		countingThread.start();
		startListeningBlocking(port);
	}

	/**
	 * Listens for a connection on the calling thread
	 * <p>
	 * Calls connectionEstablished event on connection
	 */
	public void startListeningBlocking(int localPort)
	{
		assert !isListening();

		connectionListener = new ConnectionListener(localPort);
		connectionListener.run();
	}

	/**
	 * Issues a new thread and listens for a connection on it with a timeout
	 * <p>
	 * Calls connectionEstablished event on connection
	 */
	public void startListening(int port, long timeOutMillis)
	{
		assert timeOutMillis > 0 : "Negative timeOut";
		assert port >= 0 && port < ((Short.MAX_VALUE * 2) + 2) : "Invalid port number";

		Thread countingThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				startListening(port);
				try
				{
					Thread.sleep(timeOutMillis);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				if (isListening())
					stopListening();
			}
		});

		countingThread.setDaemon(true);
		countingThread.start();
	}

	/**
	 * Issues a new thread and listens for a connection on it
	 * <p>
	 * Calls connectionEstablished event on connection
	 */
	public void startListening(int localPort)
	{
		assert !isListening();

		connectionListener = new ConnectionListener(localPort);
		connectionListener.start();
	}

	public interface ConnectionEvent
	{
		void connectionAttemptSuccessful(Connection connection);

		void connectionReceivedOnListener(Connection connection);
	}

	private class ConnectionListener extends Thread
	{
		private ServerSocket serverSocket;
		private int port;

		public ConnectionListener(int port)
		{
			setDaemon(true);
			this.port = port;
		}

		@Override
		public void run()
		{
			try
			{
				serverSocket = new ServerSocket(port);

				System.out.println("Started listening on: " + InetAddress.getLocalHost() + ":" + port);
				Socket socket = serverSocket.accept();
				serverSocket.close();
				LOGGER.log(Level.ALL, String.format("Connection successful from URL: %s, port: %d, to URL: %s, port: %d",
						socket.getLocalAddress(), socket.getLocalPort(), socket.getInetAddress(), socket.getPort()));
				connectionEvent.connectionReceivedOnListener(new NetworkConnection(socket, new SocketMessageTransmitter(socket), new SocketMessageReceiver(socket)));
			} catch (IOException e)
			{
				LOGGER.log(Level.WARNING, String.format("Socket stopped from listening on port: %d\n%s",
						port, e.getMessage()));
//				e.printStackTrace();
			}
		}
	}
}