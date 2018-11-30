package network;

import network.FileTransfer.FileSocketReceiver;
import network.FileTransfer.FileSocketSender;
import sun.awt.windows.ThemeReader;
import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteSocketConnection implements Connection
{
	private static final int PORT = 50001;
	private static final Logger LOGGER = AppLogger.getInstance();

	private Socket socket;
	private ConnectionResolver connectionResolver;

	private SocketReceiver socketReceiver;
	private SocketSender socketSender;

	private List<String> remoteFiles = new ArrayList<String>();

	private SocketReceivingEvents receivingEvents;
	private ConnectionListener.ConnectionReceivedEvent connectionReceivedEvent;


	public RemoteSocketConnection(SocketReceivingEvents receivingEvents, ConnectionListener.ConnectionReceivedEvent connectionReceivedEvent)
	{
		this.receivingEvents = receivingEvents;
		this.connectionReceivedEvent = connectionReceivedEvent;

		connectionResolver = new ConnectionResolver();
		connectionResolver.startListening(connectionReceivedEvent, PORT);
	}

	public boolean isConnected()
	{
		return null != socket && !socket.isClosed() && socket.isConnected();
	}

	public boolean attemptConnection(String url)
	{
		boolean successful = false;
		if (connectionResolver.isListening())
			connectionResolver.stopListening();

		if (!isConnected())
		{
			Socket tempSocket = connectionResolver.establishConnection(url, PORT);
			if (tempSocket.isConnected())
			{
				socket = tempSocket;
				successful = true;
			} else
			{
				connectionResolver.startListening(connectionReceivedEvent, PORT);
			}
		}
		return successful;
	}

	public void acceptConnection(Socket socket)
	{
		assert null != socket : "Received null socket";

		if (socket.isConnected())
			this.socket = socket;
	}

	private void initializeReciverSender()
	{
		assert socket.isConnected() : "Can't initialize with empty socket";

		socketReceiver = new SocketReceiver(socket, receivingEvents);
		socketSender = new SocketSender(socket);
	}

	public boolean closeConnection()
	{
		assert isConnected();

		boolean successful = false;
		if (isConnected())
		{
			LOGGER.log(Level.INFO, "Trying to close Connection to: " + socket.getInetAddress());
			try
			{
				socket.close();
				LOGGER.log(Level.INFO, String.format("Connection closing successful on URL: %s",
						socket.getInetAddress()));
				socket = null;
				successful = true;
			} catch (IOException e)
			{
				LOGGER.log(Level.INFO, String.format("Connection closing unsuccessful on URL: %s\n%s",
						socket.getInetAddress(), e.getMessage()));
			}
		} else
		{
			LOGGER.log(Level.FINE, "Tried closing unconnected socket");
		}
		return successful;
	}

	public Socket getSocket()
	{
		return socket;
	}
}
