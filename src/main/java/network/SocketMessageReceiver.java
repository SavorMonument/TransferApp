package network;

import logic.NetworkMessage;
import logic.NetworkMessage.MessageType;
import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketMessageReceiver extends SocketReceiver
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private BufferedReader inputReader;

	public SocketMessageReceiver(Socket socket)
	{
		super(socket);

		inputReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
	}

	public NetworkMessage pullMessage()
	{
		NetworkMessage message = null;
		try
		{
			String line;
			if (inputReader.ready())
			{
				line = inputReader.readLine();
				MessageType type = MessageType.valueOf(line);
				line = inputReader.readLine();
				message = new NetworkMessage(type, line);
			}

		}catch (IllegalArgumentException e)
		{
			LOGGER.log(Level.WARNING, "Received invalid message");
//			e.printStackTrace();
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
			if (inputReader.ready())
				message = inputReader.readLine();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return message;
	}
}
