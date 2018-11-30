package network.FileTransfer;

import network.ConnectionListener;
import network.ConnectionResolver;

import java.io.File;
import java.net.Socket;

public class FileSocketSender
{
	private ConnectionResolver connectionResolver = new ConnectionResolver();
	private Socket fileSocket;

	public boolean isConnected()
	{
		return null != fileSocket && !fileSocket.isClosed() && fileSocket.isConnected();
	}

	public void connect(String URL, int port)
	{
		connectionResolver.establishConnection(URL, port);
	}

	public void transferFile(String path)
	{
		transferFile(new File(path));
	}

	public void transferFile(File file)
	{

	}

	public void awaitConnection(String URL, int port)
	{
		connectionResolver.startListening(new ConnectionListener.ConnectionReceivedEvent()
		{
			public void receivedConnection(Socket socket)
			{
				fileSocket = socket;
			}
		}, port);
	}
}
