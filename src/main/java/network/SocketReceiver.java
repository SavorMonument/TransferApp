package network;

import filetransfer.TransferInput;
import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketReceiver extends SocketStream implements TransferInput
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
