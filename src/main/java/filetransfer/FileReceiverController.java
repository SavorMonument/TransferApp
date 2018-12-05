package filetransfer;

import filesistem.FileOutput;
import network.ConnectionResolver;
import network.SocketReceiver;
import network.SocketTransmitter;
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

	private SocketReceiver socketReceiver;

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
		awaitConnection();
		if (null != socketReceiver)
		{
			try(FileOutput fileOutput = new FileOutput(fileName, downloadPath))
			{
				fileOutput.createTempFile();
				receiveBytesAndWriteToFile(fileOutput);
				fileOutput.finishFile();

				LOGGER.log(Level.ALL, "File receiving done");
			} catch (IOException | InterruptedException e)
			{
				e.printStackTrace();
			}finally
			{
				socketReceiver.close();
			}
		} else
		{
			LOGGER.log(Level.WARNING, "Connection timeout");
		}

	}

	private void receiveBytesAndWriteToFile(FileOutput fileOutput) throws IOException, InterruptedException
	{
		byte[] buffer = new byte[BUFFER_SIZE];

		DeltaTime dt;
		while (socketReceiver.available() > 0)
		{
			dt = new DeltaTime();
			while (socketReceiver.available() < BUFFER_SIZE && dt.getElapsedTimeMillis() < CONNECTION_TIMEOUT_MILLIS)
			{
				Thread.sleep(1000);
			}
			int amountRead = socketReceiver.read(buffer);
			fileOutput.writeToFile(buffer, amountRead);

			//TODO: Make stopping solution better can't rely on the networks mercy
			if (amountRead < BUFFER_SIZE)
				break;
		}
	}

	private void awaitConnection()
	{
		ConnectionResolver resolver = new ConnectionResolver(new ConnectionResolver.ConnectionEvent()
		{

			@Override
			public void connectionEstablished(Socket socket, SocketTransmitter socketTransmitter, SocketReceiver socketReceiver)
			{
				FileReceiverController.this.socketReceiver = socketReceiver;
			}
		});

		resolver.startListeningBlocking(listeningPort, CONNECTION_TIMEOUT_MILLIS);
	}
}
