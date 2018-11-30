package network.FileTransfer;

import network.Connection;
import network.ConnectionListener;
import network.ConnectionResolver;

import java.net.Socket;

public class FileSocketReceiver
{

	private ConnectionResolver connectionResolver = new ConnectionResolver();
	private Socket fileSocket;

	public boolean isConnected()
	{
		return null != fileSocket && !fileSocket.isClosed() && fileSocket.isConnected();
	}





}
