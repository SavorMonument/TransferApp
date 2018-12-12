package network.messaging;

import logic.api.Connection;
import logic.connection.ByteCounter;
import logic.messaging.NetworkMessage;
import logic.messaging.NetworkMessage.MessageType;
import logic.messaging.ConnectionException;
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

	/**
	 * Waits for a message to be available, then returns it
	 *
	 * @return A network message
	 * @throws ConnectionException When there is a problem with the underlying stream
	 */
	public NetworkMessage pullMessageBlocking() throws ConnectionException
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
				//Underlying socket closed
				throw new ConnectionException("Error on socket read", getClass().getName(), e);
			}
			catch (IllegalArgumentException e)
			{
				//Invalid message
				LOGGER.log(Level.WARNING, "Received invalid message");
			} catch (IOException e)
			{
				LOGGER.log(Level.WARNING, "Socket read exception: " + e.getMessage());
				throw new ConnectionException("Error on socket read", getClass().getName(), e);
			}
		}
		return message;
	}

	/**
	 * Checks if there are messages waiting and pulls one if there is one available
	 *
	 * @return A network message if there is one
	 * @throws ConnectionException When there is a problem with the underlying stream
	 */
	public NetworkMessage pullMessage() throws ConnectionException
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

		} catch (NullPointerException e)
		{
			//Underlying socket closed
			throw new ConnectionException("Error on socket read", getClass().getName(), e);
		}
		catch (IllegalArgumentException e)
		{
			//Invalid message
			LOGGER.log(Level.WARNING, "Received invalid message");
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket read exception: " + e.getMessage());
			throw new ConnectionException("Error on socket read", getClass().getName(), e);
		}

		return message;
	}

	@Override
	public void registerBytesCounter(ByteCounter bytesCounter)
	{
		super.registerBytesCounter(bytesCounter);
	}
}
