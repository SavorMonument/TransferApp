package filetransfer;

import filesistem.FileInput;
import network.ConnectionResolver;
import network.SocketReceiver;
import network.SocketTransmitter;
import window.AppLogger;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTransmitter extends Thread
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int BUFFER_SIZE = 4096;

	private int socketBufferSize;
	private SocketTransmitter socketTransmitter;
	private SocketReceiver socketReceiver;

	private String socketURL;
	private int transmittingPort;
	private String filePath;

	public FileTransmitter(String filePath, String URL, int transmittingPort)
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
		int bytesSentSinceLastSignal = 0;

		int bytesRead = BUFFER_SIZE;
		while (bytesRead == BUFFER_SIZE)
		{
			if (bytesSentSinceLastSignal + BUFFER_SIZE < socketBufferSize)
			{
				bytesRead = fileInput.read(buffer, BUFFER_SIZE);
				socketTransmitter.transmitBytes(buffer, bytesRead);
				bytesSentSinceLastSignal += BUFFER_SIZE;
			}
			if (socketReceiver.available() > 0)
			{
				socketReceiver.read();
				bytesSentSinceLastSignal = 0;
			}
		}
	}

	private void attemptConnection()
	{
		ConnectionResolver resolver = new ConnectionResolver(new ConnectionResolver.ConnectionEvent()
		{
			@Override
			public void connectionEstablished(Socket socket, SocketTransmitter socketTransmitter, SocketReceiver socketReceiver)
			{
				FileTransmitter.this.socketTransmitter = socketTransmitter;
				FileTransmitter.this.socketReceiver = socketReceiver;
				try
				{
					socketBufferSize = socket.getSendBufferSize();
				} catch (SocketException e)
				{
					e.printStackTrace();
				}
			}
		});
		resolver.attemptConnection(socketURL, transmittingPort, transmittingPort);
	}
}
