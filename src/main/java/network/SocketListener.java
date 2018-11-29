package network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketListener extends Thread
{
	private ServerSocket serverSocket;
	private NetworkListener networkListener;
	private int port;

	public SocketListener()
	{
	}

	public SocketListener(NetworkListener networkListener, int port)
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
}
