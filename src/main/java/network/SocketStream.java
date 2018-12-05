package network;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public abstract class SocketStream implements Closeable
{
	protected Socket socket;

	public SocketStream(Socket socket)
	{
		assert null != socket && socket.isConnected() : "Invalid socket";

		this.socket = socket;
	}

	public int getSocketRemotePort()
	{
		return socket.getPort();
	}

	public int getSocketLocalPort()
	{
		return socket.getLocalPort();
	}

	public String getSocketIPAddress()
	{
		return socket.getInetAddress().toString();
	}

	@Override
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
