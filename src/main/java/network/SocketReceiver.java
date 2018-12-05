package network;

import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketReceiver extends SocketStream
{
	private static final Logger LOGGER = AppLogger.getInstance();

	protected BufferedInputStream inputStream;

	public SocketReceiver(Socket socket)
	{
		super(socket);
		try
		{
			inputStream = new BufferedInputStream(socket.getInputStream());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public boolean hasBytes()
	{
		try
		{
			return inputStream.available() > 0;
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket inputStream stream problem " + e.getMessage());
//			e.printStackTrace();
		}
		return false;
	}

	public int read(byte[] b, int off, int len) throws IOException
	{
		return inputStream.read(b, off, len);
	}

	public int read(byte[] b) throws IOException
	{
		return inputStream.read(b);
	}


	public int read() throws IOException
	{
		return inputStream.read();
	}

	public int available() throws IOException
	{
		return inputStream.available();
	}
}
