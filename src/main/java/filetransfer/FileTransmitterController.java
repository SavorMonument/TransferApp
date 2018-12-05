package filetransfer;

import filesistem.FileInput;
import network.ConnectionResolver;
import network.SocketReceiver;
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

	private SocketTransmitter socketTransmitter;

	private String socketURL;
	private int transmittingPort;
	private String filePath;

	public FileTransmitterController(String filePath, String URL,  int transmittingPort)
	{
		this.filePath = filePath;
		this.socketURL = URL;
		this.transmittingPort = transmittingPort;

		setDaemon(true);
	}

	@Override
	public void run()
	{
		attemptConnection();

		if (null != socketTransmitter){

				try(FileInput fileInput = new FileInput(filePath))
				{
					fileInput.open();
					readBytesAndTransmitThemOverSocket(fileInput);
					LOGGER.log(Level.ALL, "File transmission done");
				} catch (IOException e)
				{
					LOGGER.log(Level.WARNING, "File transmitting failed " + e.getMessage());
//					e.printStackTrace();
				}finally
				{
					socketTransmitter.close();
				}
		}else
		{
			LOGGER.log(Level.WARNING,
					String.format("Couldn't connect to socket url: %s, port: %d", socketURL, transmittingPort));
		}
	}

	private void readBytesAndTransmitThemOverSocket(FileInput fileInput) throws IOException
	{
		byte[] buffer = new byte[BUFFER_SIZE];

		int bytesRead = BUFFER_SIZE;
		while (bytesRead == BUFFER_SIZE)
		{
			bytesRead = fileInput.read(buffer, BUFFER_SIZE);
			socketTransmitter.transmitBytes(buffer, bytesRead);
		}
	}

	private void attemptConnection()
	{
		try
		{
			Thread.sleep(2000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		ConnectionResolver resolver = new ConnectionResolver(new ConnectionResolver.ConnectionEvent()
		{
			@Override
			public void connectionEstablished(Socket socket, SocketTransmitter socketTransmitter, SocketReceiver socketReceiver)
			{
				FileTransmitterController.this.socketTransmitter = socketTransmitter;
			}
		});
		resolver.attemptConnection(socketURL, transmittingPort, transmittingPort);
	}
}
