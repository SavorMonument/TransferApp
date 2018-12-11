package network.streaming;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

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

	public int getBufferSize()
	{
		try
		{
			return socket.getReceiveBufferSize();
		} catch (SocketException e)
		{
//			e.printStackTrace();
		}

		return 0;
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
