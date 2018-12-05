package network;

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

	public void attemptConnection(InetAddress address, int port, int localPort)
	{
		LOGGER.log(Level.INFO, String.format("Attempting connection to URL: %s, port: %d",
				address.getHostAddress(), port));
		Socket socket;
		try
		{
			socket = new Socket(address, port, null, localPort);
			LOGGER.log(Level.ALL, String.format("Connection successful to URL: %s, port: %d",
					address.getHostAddress(), port));
			callEvent(socket);
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, String.format("Connection unsuccessful to URL: %s\n%s",
					address.getHostAddress(), e.getMessage()));
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
	 *
	 * Calls connectionEstablished event on connection
	 */
	public void startListeningBlocking(int port, long timeOutMillis)
	{
		assert timeOutMillis > 0 : "Negative timeOut";
		assert port >=0 && port < ((Short.MAX_VALUE * 2) + 2) : "Invalid port number";

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
	 *
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
	 *
	 * Calls connectionEstablished event on connection
	 */
	public void startListening(int port, long timeOutMillis)
	{
		assert timeOutMillis > 0 : "Negative timeOut";
		assert port >=0 && port < ((Short.MAX_VALUE * 2) + 2) : "Invalid port number";

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
	 *
	 * Calls connectionEstablished event on connection
	 */
	public void startListening(int localPort)
	{
		assert !isListening();

		connectionListener = new ConnectionListener(localPort);
		connectionListener.start();
	}

	private void callEvent(Socket socket)
	{
		connectionEvent.connectionEstablished(socket);
		connectionEvent.connectionEstablished(socket, new SocketTransmitter(socket), new SocketReceiver(socket));
		connectionEvent.connectionEstablished(socket, new SocketMessageTransmitter(socket), new SocketMessageReceiver(socket));
	}

	public static abstract class ConnectionEvent
	{
		public void connectionEstablished(Socket socket){}
		public void connectionEstablished(Socket socket, SocketTransmitter socketTransmitter, SocketReceiver socketReceiver){}
		public void connectionEstablished(Socket socket, SocketMessageTransmitter messageTransmitter, SocketMessageReceiver messageReceiver){}
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
				LOGGER.log(Level.ALL, String.format("Connection successful to URL: %s, port: %d",
						socket.getInetAddress(), socket.getPort()));
				callEvent(socket);
			} catch (IOException e)
			{
				LOGGER.log(Level.WARNING, "Socket stopped from listening\n" + e.getMessage());
//				e.printStackTrace();
			}
		}
	}
}