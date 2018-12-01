package network;

import window.AppLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketReceiver extends Thread
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private SocketReceivingEvents events;
	private Socket socket;
	private BufferedReader input;

	public SocketReceiver(Socket socket, SocketReceivingEvents events)
	{
		assert null != socket : "Need valid socket";

		this.socket = socket;
		try
		{
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "Receiving Socket stream problem");
			e.printStackTrace();
		}

		this.events = events;
		setDaemon(true);
	}

	@Override
	public void run()
	{
		LOGGER.log(Level.FINEST, "Started listening for messages");

		while (true)
		{
			try
			{
				String line;
				if (input.ready())
				{
					System.out.println("Here " + input.ready());

					line = input.readLine();
					LOGGER.log(Level.FINEST, "Received socket message: " + line);

					if (line.equals("SEND_FILE"))
					{
						String filename = input.readLine();
						events.uploadFile(filename);

					} else if (line.equals("UPDATE_FILE_LIST"))
					{
						line = input.readLine();
						line = line.substring(1, line.length() - 1);
						events.updateRemoteFileList(Arrays.asList(line.split(", ")));
					}
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
