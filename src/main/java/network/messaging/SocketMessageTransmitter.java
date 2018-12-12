package network.messaging;

import logic.api.Connection;
import logic.connection.ByteCounter;
import logic.messaging.NetworkMessage;
import logic.messaging.ConnectionException;
import network.streaming.SocketOutputStream;
import window.AppLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketMessageTransmitter extends SocketOutputStream implements Connection.MessageTransmitter
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private BufferedWriter outputWriter;

	public SocketMessageTransmitter(Socket socket)
	{
		super(socket);
		this.outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
	}

	public void transmitMessage(NetworkMessage networkMessage) throws ConnectionException
	{
		StringBuilder message = new StringBuilder();
		message.append(networkMessage.getType().toString())
				.append("\n")
				.append(networkMessage.getMessage())
				.append("\n");

		try
		{
			outputWriter.write(message.toString());
			outputWriter.flush();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket write exception: " + e.getMessage());
			throw new ConnectionException("Error on socket write", getClass().getName(), e);
		}
	}

	@Override
	public void registerBytesCounter(ByteCounter bytesCounter)
	{
		super.registerBytesCounter(bytesCounter);
	}
}
