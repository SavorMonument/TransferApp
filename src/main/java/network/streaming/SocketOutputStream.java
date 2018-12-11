package network.streaming;

import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class SocketOutputStream extends SocketStream
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

	public void transmitBytes(byte[] bytes) throws IOException
	{
		transmitBytes(bytes, bytes.length);
	}

	public void transmitBytes(byte[] bytes, int numOfBytes) throws IOException
	{
		outputStream.write(bytes, 0, numOfBytes);
		outputStream.flush();
		if (null != bytesCounter)
		{
			bytesCounter.addToCount(numOfBytes);
		}
	}

	public void transmitByte(int b) throws IOException
	{
		outputStream.write(b);
		outputStream.flush();

		if (null != bytesCounter)
		{
			bytesCounter.addToCount(1);
		}
	}
}
