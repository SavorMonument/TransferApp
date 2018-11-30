package network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener extends Thread
{
	private ServerSocket serverSocket;
	private ConnectionReceivedEvent networkListener;
	private int port;

	public ConnectionListener()
	{
	}

	public ConnectionListener(ConnectionReceivedEvent networkListener, int port)
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
			networkListener.receivedConnection(socket);
		} catch (IOException e)
		{
			e.printStackTrace();
		}


	}

	public interface ConnectionReceivedEvent
	{
		abstract void receivedConnection(Socket socket);
	}
}
