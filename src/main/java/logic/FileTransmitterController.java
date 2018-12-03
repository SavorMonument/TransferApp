package logic;

import filesistem.FileInput;
import network.ConnectionResolver;
import network.SocketTransmitter;
import window.AppLogger;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTransmitterController extends Thread
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int BUFFER_SIZE = 4096;

	private Socket mainSocket;

	private String socketURL;
	private int transmittingPort;
	private String filePath;
	private String fileName;

	public FileTransmitterController(String filePath, String fileName, String URL,  int transmittingPort)
	{
		this.filePath = filePath;
		this.fileName = fileName;
		this.socketURL = URL;
		this.transmittingPort = transmittingPort;

		setDaemon(true);
	}

	@Override
	public void run()
	{
		ConnectionResolver resolver = new ConnectionResolver(new ConnectionResolver.ConnectionEvent()
		{
			@Override
			public void connectionEstablished(Socket socket)
			{
				mainSocket = socket;
			}
		});

		resolver.attemptConnection(socketURL, transmittingPort);

		if (null != mainSocket){

			FileInput fileInput = new FileInput(fileName, filePath);

			if (fileInput.open())
			{
				byte[] buffer = new byte[BUFFER_SIZE];

				try
				{
					SocketTransmitter transmitter = new SocketTransmitter(mainSocket.getOutputStream());

					int bytesRead = BUFFER_SIZE;
					while (bytesRead == BUFFER_SIZE)
					{
						bytesRead = fileInput.read(buffer, BUFFER_SIZE);
						transmitter.transmitBytes(buffer);
					}

				} catch (IOException e)
				{
					LOGGER.log(Level.WARNING, "File transmitting failed " + e.getMessage());
//					e.printStackTrace();
				}finally
				{
					fileInput.close();
					try
					{
						mainSocket.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}else
			{
				LOGGER.log(Level.WARNING,
						String.format("Couldn't find file name: %s, path: %s", fileName, filePath));
			}
		}else
		{
			LOGGER.log(Level.WARNING,
					String.format("Couldn't connect to socket url: %s, port: %d", socketURL, transmittingPort));
		}
	}
}
