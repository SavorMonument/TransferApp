package network;

import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketTransmitter extends OutputStream
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private BufferedOutputStream output;

	public SocketTransmitter(OutputStream stream) throws IOException
	{
		output = new BufferedOutputStream(stream);
	}

	public void transmitBytes(byte[] bytes)
	{
		try
		{
			output.write(bytes);
			output.flush();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Could not send message to output stream");
		}
	}

	@Override
	public void write(int b)
	{
		try
		{
			output.write(b);
			output.flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
