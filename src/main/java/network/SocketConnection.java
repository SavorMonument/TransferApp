package network;

import window.AppLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketConnection
{
	private static final Logger LOGGER = AppLogger.getInstance();
	Socket socket;

	public SocketConnection()
	{
	}

	public boolean establishConnection(String url, int port)
	{
		LOGGER.log(Level.INFO, "Received connection establish request on URL: " + url);
		boolean successful = false;
		try
		{
			socket = new Socket(url, port);
			successful = true;
			LOGGER.log(Level.INFO, String.format("Connection successful on URL: %s", url));
		} catch (IOException e)
		{
			LOGGER.log(Level.INFO, String.format("Connection unsuccessful on URL: %s\n%s", url, e.getMessage()));
		}

		return successful;
	}

	public boolean isConnected()
	{
		return null != socket && !socket.isClosed();
	}

	public boolean closeConnection()
	{
		boolean successful = false;
		if (isConnected())
		{
			LOGGER.log(Level.INFO, "Trying to close connection to: " + socket.getInetAddress());
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

	public void setSocket(Socket socket)
	{
		this.socket = socket;
	}

	public boolean sendBytes(byte[] bytes)
	{
		boolean successful = false;
		try
		{
			LOGGER.log(Level.INFO, "Trying to send bytes to socket: " + socket.getInetAddress());
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(bytes);
			successful = true;
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, String.format("Connection closing unsuccessful on URL: %s\n%s",
					socket.getInetAddress(), e.getMessage()));
		}

		return successful;
	}
}
