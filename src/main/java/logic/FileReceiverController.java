package logic;

import filesistem.FileOutput;
import network.ConnectionResolver;
import network.SocketReceiver;
import window.AppLogger;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileReceiverController extends Thread
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int CONNECTION_TIMEOUT_MILLIS = 10_000;
	private static final int BUFFER_SIZE = 4096;

	private Socket mainSocket;

	private int listeningPort;
	private String downloadPath;
	private String fileName;

	public FileReceiverController(String downloadPath, String fileName, int listeningPort)
	{
		this.downloadPath = downloadPath;
		this.fileName = fileName;
		this.listeningPort = listeningPort;
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

		resolver.startListening(listeningPort, CONNECTION_TIMEOUT_MILLIS);
		try
		{
			Thread.sleep(100);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		resolver.joinListener(this);

		if (null != mainSocket)
		{
			FileOutput fileOutput = new FileOutput(fileName, downloadPath);
			fileOutput.createTempFile();

			try
			{
				SocketReceiver receiver = new SocketReceiver(mainSocket.getInputStream());
				byte[] buffer = new byte[BUFFER_SIZE];

				DeltaTime dt;
				while (receiver.available() > 0)
				{
					dt = new DeltaTime();
					while (receiver.available() < BUFFER_SIZE && dt.getElapsedTimeMillis() < CONNECTION_TIMEOUT_MILLIS)
					{
						Thread.sleep(1000);
					}
					int amountRead = receiver.read(buffer);
					fileOutput.writeToFile(buffer, amountRead);

					//TODO: Make stopping solution better can't rely on the networks mercy
					if (amountRead < BUFFER_SIZE)
						break;
				}
				fileOutput.finishFile();
				LOGGER.log(Level.ALL, "File receiving done");
			} catch (IOException e)
			{
				e.printStackTrace();
				fileOutput.abort();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
				fileOutput.abort();
			}finally
			{
				fileOutput.close();
				try
				{
					mainSocket.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		} else
		{
			LOGGER.log(Level.WARNING, "Connection timeout");
		}

	}
}
