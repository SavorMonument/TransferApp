package network.messaging;

import logic.connection.Connection;
import network.ConnectionException;
import network.streaming.SocketOutputStream;
import window.AppLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketStringTransmitter extends SocketOutputStream implements Connection.StringTransmitter
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private BufferedWriter outputWriter;

	public SocketStringTransmitter(Socket socket)
	{
		super(socket);
		this.outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
	}

	public void transmitString(String message) throws ConnectionException
	{
		try
		{
			outputWriter.write(message);
			outputWriter.flush();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Socket write exception: " + e.getMessage());
			throw new ConnectionException("Error on socket write", getClass().getName(), e);
		}
	}
}
