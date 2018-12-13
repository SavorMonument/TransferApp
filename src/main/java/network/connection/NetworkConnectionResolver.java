package network.connection;

import logic.connection.Connection;
import logic.connection.ConnectionResolver;
import network.messaging.SocketStringReceiver;
import network.messaging.SocketStringTransmitter;
import window.AppLogger;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkConnectionResolver implements ConnectionResolver
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private ServerSocket serverSocket;
	private int currentPort = 49821;

	public NetworkConnectionResolver()
	{
	}

	public Connection attemptNextConnection(InetAddress remoteAddress) throws IOException
	{
		return attemptNextConnection(remoteAddress, 10_000);
	}

	public Connection attemptNextConnection(InetAddress remoteAddress, int timeOut) throws IOException
	{
		LOGGER.log(Level.ALL, String.format("Attempting connection to URL: %s, port: %d",
				remoteAddress, currentPort));
		Socket socket = new Socket();
		Connection connection;
		try
		{
			socket.connect(new InetSocketAddress(remoteAddress, currentPort), timeOut);
			connection = new NetworkConnection
					(socket, new SocketStringTransmitter(socket), new SocketStringReceiver(socket));

			LOGGER.log(Level.ALL, String.format("Connection successful to URL: %s, port: %d",
					remoteAddress, currentPort));
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, String.format("Connection unsuccessful to URL: %s, port: %s\n%s",
					remoteAddress, currentPort, e.getMessage()));
			throw e;
		}
		currentPort++;
		return connection;
	}

	public Connection listenNextConnection() throws IOException
	{
		return listenNextConnection(0);
	}

	public Connection listenNextConnection(int timeOut) throws IOException
	{
		Connection connection;
		try
		{
			serverSocket = new ServerSocket(currentPort);
			serverSocket.setSoTimeout(timeOut);

			System.out.println("Started listening on: " + InetAddress.getLocalHost() + ":" + currentPort);
			Socket socket = serverSocket.accept();
			serverSocket.close();
			connection = new NetworkConnection
					(socket, new SocketStringTransmitter(socket), new SocketStringReceiver(socket));
			LOGGER.log(Level.ALL, String.format("Connection successful from URL: %s, port: %d, to URL: %s, port: %d",
					socket.getLocalAddress(), socket.getLocalPort(), socket.getInetAddress(), socket.getPort()));
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, String.format("Socket stopped from listening on port: %d\n%s",
					currentPort, e.getMessage()));
			throw e;
		}
		currentPort++;
		return connection;
	}

	public void stopListening()
	{
		try
		{
			serverSocket.close();
		} catch (IOException e)
		{
			LOGGER.log(Level.FINE,"Server socket closed");
		}
	}
}