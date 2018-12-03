package network;

import network.NetworkMessage.MessageType;
import window.AppLogger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketMessageReceiver
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private BufferedReader input;

	public SocketMessageReceiver(SocketReceiver socketReceiver)
	{
		input = new BufferedReader(new InputStreamReader(socketReceiver, StandardCharsets.UTF_8));
	}

	public SocketMessageReceiver(InputStream stream)
	{
		input = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
	}


	public NetworkMessage pullMessage()
	{
		NetworkMessage message = null;
		try
		{
			String line;
			if (input.ready())
			{
				line = input.readLine();
				MessageType type = MessageType.valueOf(line);
				line = input.readLine();
				message = new NetworkMessage(type, line);
			}

		}catch (IllegalArgumentException e)
		{
			LOGGER.log(Level.WARNING, "Received invalid message");
			e.printStackTrace();
			message = null;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return message;
	}

	private String getLine()
	{
		String message = "";
		try
		{
			if (input.ready())
				message = input.readLine();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return message;
	}
}
