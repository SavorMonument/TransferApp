package network.streaming;

import filetransfer.api.TransferInput;
import network.ConnectionException;
import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketInputStream extends SocketStream implements TransferInput
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private Counter bytesCounter;
	protected BufferedInputStream inputStream;

	public SocketInputStream(Socket socket) throws IOException
	{
		super(socket);

		inputStream = new BufferedInputStream(socket.getInputStream());
	}

	public void registerBytesCounter(Counter bytesCounter)
	{
		this.bytesCounter = bytesCounter;
	}

	public int read(byte[] b, int len) throws ConnectionException
	{
		int amount;
		try
		{
			amount = inputStream.read(b, 0, len);
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket read exception: " + e.getMessage());
			throw new ConnectionException("Error on socket read", getClass().getName(), e);
		}

		if (null != bytesCounter)
			bytesCounter.addToCount(amount);
		return amount;
	}

	public int read(byte[] b, int off, int len) throws IOException
	{
		int amount = inputStream.read(b, off, len);

		if (null != bytesCounter)
			bytesCounter.addToCount(amount);

		return amount;
	}

	public int read(byte[] b) throws IOException
	{
		int amount = inputStream.read(b);

		if (null != bytesCounter)
			bytesCounter.addToCount(amount);

		return amount;
	}

	public int read() throws ConnectionException
	{
		if (null != bytesCounter)
			bytesCounter.addToCount(1);

		try
		{
			return inputStream.read();
		}catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket read exception: " + e.getMessage());
			throw new ConnectionException("Error on socket read", getClass().getName(), e);
		}
	}

	public int available() throws ConnectionException
	{
		try
		{
			return inputStream.available();
		}catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket exception: " + e.getMessage());
			throw new ConnectionException("Error on socket read", getClass().getName(), e);
		}
	}

	public void skip(long n) throws ConnectionException
	{
		try
		{
			inputStream.skip(n);
		}catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket exception: " + e.getMessage());
			throw new ConnectionException("Error on socket read", getClass().getName(), e);
		}
	}
}
