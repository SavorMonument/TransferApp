package network.connection;

import com.sun.istack.internal.NotNull;
import logic.connection.Connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class NetworkConnection extends Connection
{
	private Socket socket;

	public NetworkConnection(@NotNull Socket socket, @NotNull Connection.MessageTransmitter messageTransmitter, @NotNull StringReceiver messageReceiver)
	{
		super(messageTransmitter, messageReceiver);

		this.socket = socket;
	}

	public boolean isConnected()
	{
		return socket.isConnected();
	}

	public InetAddress getLocalAddress()
	{
		return socket.getLocalAddress();
	}

	public InetAddress getRemoteAddress()
	{
		return socket.getInetAddress();
	}

	public int getRemotePort()
	{
		return socket.getPort();
	}

	public int getLocalPort()
	{
		return socket.getLocalPort();
	}

	public void close()
	{
		try
		{
			socket.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
