package network.streaming;

import filetransfer.api.TransferException;
import filetransfer.api.TransferOutput;
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

	public void transmitBytes(byte[] bytes) throws TransferException
	{
		transmitBytes(bytes, bytes.length);
	}

	public void transmitBytes(byte[] bytes, int numOfBytes) throws TransferException
	{
		try
		{
			outputStream.write(bytes, 0, numOfBytes);
//			outputStream.flush();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket write exception: " + e.getMessage());
			throw new TransferException("Error on socket write", getClass().getName(), e);
		}

		if (null != bytesCounter)
		{
			bytesCounter.addToCount(numOfBytes);
		}
	}

	public void flush() throws TransferException
	{
		try
		{
			outputStream.flush();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket flush exception: " + e.getMessage());
			throw new TransferException("Error on socket flush", getClass().getName(), e);
		}
	}

	public void transmitByte(int b) throws TransferException
	{
		try
		{
			outputStream.write(b);
			outputStream.flush();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket write exception: " + e.getMessage());
			throw new TransferException("Error on socket write", getClass().getName(), e);
		}
		if (null != bytesCounter)
		{
			bytesCounter.addToCount(1);
		}
	}
}
