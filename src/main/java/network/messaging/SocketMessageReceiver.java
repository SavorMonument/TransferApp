package network.messaging;

import logic.api.Connection;
import logic.messaging.ByteCounter;
import logic.messaging.NetworkMessage;
import logic.messaging.NetworkMessage.MessageType;
import network.streaming.SocketInputStream;
import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketMessageReceiver extends SocketInputStream implements Connection.MessageReceiver
{

	private static final Logger LOGGER = AppLogger.getInstance();

	private BufferedReader inputReader;

	public SocketMessageReceiver(Socket socket) throws IOException
	{
		super(socket);

		inputReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
	}

	public NetworkMessage pullMessageBlocking() throws IOException
	{
		NetworkMessage message = null;

		while (null == message)
		{
			try
			{
				String line;
				line = inputReader.readLine();
				MessageType type = MessageType.valueOf(line);
				line = inputReader.readLine();
				message = new NetworkMessage(type, line);

			} catch (NullPointerException e)
			{
				throw new IOException("Error on socket read");
			}
			catch (IllegalArgumentException e)
			{
				LOGGER.log(Level.WARNING, "Received invalid message");
//			e.printStackTrace();
			}
		}
		return message;
	}

	public NetworkMessage pullMessage() throws IOException
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

	@Override
	public void registerBytesCounter(ByteCounter bytesCounter)
	{
		super.registerBytesCounter(bytesCounter);
	}
}
