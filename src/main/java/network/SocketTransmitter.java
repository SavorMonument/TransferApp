package network;

import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketTransmitter extends SocketStream
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private BytesCounter bytesCounter;
	protected OutputStream outputStream;

	public SocketTransmitter(Socket socket)
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

	public void registerBytesCounter(BytesCounter bytesCounter)
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
