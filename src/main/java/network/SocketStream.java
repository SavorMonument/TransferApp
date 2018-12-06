package network;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
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

	public InetAddress getSocketIPAddress()
	{
		return socket.getInetAddress();
	}

	@Override
	public void close()
	{
		try
		{
			System.out.println( String.format("Closing socket on port: %d to port: %d",
					socket.getLocalPort(), socket.getPort()));
			socket.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
