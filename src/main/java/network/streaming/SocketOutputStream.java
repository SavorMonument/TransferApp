package network.streaming;

import filetransfer.api.TransferOutput;
import network.ConnectionException;
import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketOutputStream extends SocketStream implements TransferOutput
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private Counter bytesCounter;
	protected OutputStream outputStream;

	public SocketOutputStream(Socket socket)
	{
		super(socket);
		try
		{
			outputStream = socket.getOutputStream();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void registerBytesCounter(Counter bytesCounter)
	{
		this.bytesCounter = bytesCounter;
	}

	public void transmitBytes(byte[] bytes) throws ConnectionException
	{
		transmitBytes(bytes, bytes.length);
	}

	public void transmitBytes(byte[] bytes, int numOfBytes) throws ConnectionException
	{
		try
		{
			outputStream.write(bytes, 0, numOfBytes);
//			outputStream.flush();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket write exception: " + e.getMessage());
			throw new ConnectionException("Error on socket write", getClass().getName(), e);
		}

		if (null != bytesCounter)
		{
			bytesCounter.addToCount(numOfBytes);
		}
	}

	public void flush() throws ConnectionException
	{
		try
		{
			outputStream.flush();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket flush exception: " + e.getMessage());
			throw new ConnectionException("Error on socket flush", getClass().getName(), e);
		}
	}

	public void transmitByte(int b) throws ConnectionException
	{
		try
		{
			outputStream.write(b);
			outputStream.flush();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket write exception: " + e.getMessage());
			throw new ConnectionException("Error on socket write", getClass().getName(), e);
		}
		if (null != bytesCounter)
		{
			bytesCounter.addToCount(1);
		}
	}
}
