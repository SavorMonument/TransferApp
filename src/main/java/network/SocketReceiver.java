package network;

import window.AppLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketReceiver
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private BufferedReader input;

	public SocketReceiver(Socket socket) throws IOException
	{
		assert null != socket && socket.isConnected() : "Need valid socket";

		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public boolean hasMessage()
	{
		try
		{
			return input.ready();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket input stream problem " + e.getMessage());
//			e.printStackTrace();
		}
		return false;
	}

	public String getLine()
	{
		if (hasMessage())
		{
			try
			{
				return input.readLine();
			} catch (IOException e)
			{
				LOGGER.log(Level.WARNING, "Socket input stream problem " + e.getMessage());
//				e.printStackTrace();
			}
		}else
			LOGGER.log(Level.WARNING, "Called get line with no message pending");

		return null;
	}
}
