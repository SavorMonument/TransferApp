package network;

import window.AppLogger;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketSender
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private Socket socket;
	private BufferedOutputStream output;

	public SocketSender(Socket socket) throws IOException
	{
		assert null != socket && socket.isConnected() : "Need valid socket";

		this.socket = socket;

		output = new BufferedOutputStream(socket.getOutputStream());
	}

	public void transmitMessage(String message)
	{
		try
		{
			output.write(message.getBytes());
			output.flush();
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Could not send message to remote socket: " + message);
		}
	}
}
