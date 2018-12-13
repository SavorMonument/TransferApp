package network.messaging;

import logic.connection.Connection;
import logic.connection.ByteCounter;
import logic.messaging.ConnectionException;
import network.streaming.SocketInputStream;
import window.AppLogger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketStringReceiver extends SocketInputStream implements Connection.StringReceiver
{

	private static final Logger LOGGER = AppLogger.getInstance();

	private BufferedReader inputReader;

	public SocketStringReceiver(Socket socket) throws IOException
	{
		super(socket);

		inputReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
	}

	/**
	 * Waits for a message to be available, then returns it
	 *
	 * @return String
	 * @throws ConnectionException When there is a problem with the underlying stream
	 */
	public String pullLineBlocking() throws ConnectionException
	{
		String message;
		try
		{
			message = inputReader.readLine();
			if (null == message)
				throw new IOException();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket read exception: " + e.getMessage());
			throw new ConnectionException("Error on socket read", getClass().getName(), e);
		}

		return message;
	}

	/**
	 * Checks if there are characters waiting and pulls a line if there are any available
	 *
	 * @return String
	 * @throws ConnectionException When there is a problem with the underlying stream
	 */
	public String pullLine() throws ConnectionException
	{
		String message = "";
		try
		{
			if (inputReader.ready())
			{
				message = inputReader.readLine();
			}

		} catch (NullPointerException e)
		{
			//Underlying socket closed
			throw new ConnectionException("Error on socket read", getClass().getName(), e);
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
