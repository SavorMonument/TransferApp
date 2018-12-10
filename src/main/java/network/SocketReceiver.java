package network;

import filetransfer.api.TransferInput;
import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class SocketReceiver extends SocketStream implements TransferInput
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private BytesCounter bytesCounter;
	protected BufferedInputStream inputStream;

	public SocketReceiver(Socket socket) throws IOException
	{
		super(socket);

		inputStream = new BufferedInputStream(socket.getInputStream());
	}

	public void registerBytesCounter(BytesCounter bytesCounter)
	{
		this.bytesCounter = bytesCounter;
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
		System.out.println("Buffer: " + socket.getReceiveBufferSize() + "  " + b.length);
		int amount = inputStream.read(b);

		if (null != bytesCounter)
			bytesCounter.addToCount(amount);

		return amount;
	}

	public int read() throws IOException
	{
		if (null != bytesCounter)
			bytesCounter.addToCount(1);

		return inputStream.read();
	}

	public int available() throws IOException
	{
		return inputStream.available();
	}

	public void skip(long n) throws IOException
	{
		inputStream.skip(n);
	}
}
