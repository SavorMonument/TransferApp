package network;

import filetransfer.TransferOutput;
import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketTransmitter extends SocketStream implements TransferOutput
{
	private static final Logger LOGGER = AppLogger.getInstance();

	protected BufferedOutputStream outputStream;

	public SocketTransmitter(Socket socket)
	{
		super(socket);
		try
		{
			outputStream = new BufferedOutputStream(socket.getOutputStream());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void transmitBytes(byte[] bytes)
	{
		transmitBytes(bytes, bytes.length);
	}

	public void transmitBytes(byte[] bytes, int numOfBytes)
	{
		try
		{
			outputStream.write(bytes, 0, numOfBytes);
			outputStream.flush();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Could not send message to outputStream " + e.getMessage());
		}
	}

	public void transmitByte(int b)
	{
		try
		{
			outputStream.write(b);
			outputStream.flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
