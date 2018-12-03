package network;

import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketReceiver extends InputStream
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private BufferedInputStream input;

	public SocketReceiver(InputStream stream) throws IOException
	{
		input = new BufferedInputStream(stream);
	}

	public boolean hasBytes()
	{
		try
		{
			return input.available() > 0;
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket input stream problem " + e.getMessage());
//			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		return input.read(b, off, len);
	}

	@Override
	public int read(byte[] b) throws IOException
	{
		return input.read(b);
	}


	@Override
	public int read() throws IOException
	{
		return input.read();
	}

	@Override
	public int available() throws IOException
	{
		return input.available();
	}

	@Override
	public void close()
	{
		try
		{
			input.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
