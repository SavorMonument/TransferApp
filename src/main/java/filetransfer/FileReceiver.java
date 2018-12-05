package filetransfer;

import com.sun.istack.internal.NotNull;
import filesistem.FileOutput;
import network.ConnectionResolver;
import network.SocketReceiver;
import network.SocketTransmitter;
import window.AppLogger;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileReceiver extends Thread
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int CONNECTION_TIMEOUT_MILLIS = 10_000;
	private static final int BUFFER_SIZE = 4096;

	private TransferFileOutput fileOutput;
	private TransferInput socketReceiver;
	private TransferOutput socketTransmitter;

	public FileReceiver(@NotNull TransferInput socketReceiver,@NotNull TransferOutput socketTransmitter,
					 	@NotNull TransferFileOutput fileOutput)
	{
		this.fileOutput = fileOutput;
		this.socketReceiver = socketReceiver;
		this.socketTransmitter = socketTransmitter;

		setDaemon(true);
	}

	@Override
	public void run()
	{
//		awaitConnection();
		if (null != socketReceiver)
		{
			try
			{
				fileOutput.createTempFile();
				receiveBytesAndWriteToFile(fileOutput);
				fileOutput.finishFile();

				LOGGER.log(Level.ALL, "File receiving done");
			} catch (IOException | InterruptedException e)
			{
				e.printStackTrace();
			} finally
			{
				socketReceiver.close();
			}
		} else
		{
			LOGGER.log(Level.WARNING, "Connection timeout");
		}

	}

	private void receiveBytesAndWriteToFile(TransferFileOutput fileOutput) throws IOException, InterruptedException
	{
		byte[] buffer = new byte[BUFFER_SIZE];

		//TODO: Better way to determine when a file is done or not, can't rely on a timer
		DeltaTime lastSuccessfulTransmission = new DeltaTime();
		while (lastSuccessfulTransmission.getElapsedTimeMillis() < CONNECTION_TIMEOUT_MILLIS)
		{
			if (socketReceiver.available() == 0)
			{
				//Send a byte to let the transmitter know it can transmit
				socketTransmitter.transmitByte(1);
				Thread.sleep(100);
			} else
			{
				while (socketReceiver.available() >= BUFFER_SIZE)
				{
					int amountRead = socketReceiver.read(buffer);
					fileOutput.writeToFile(buffer, amountRead);
				}
				lastSuccessfulTransmission = new DeltaTime();
			}
		}
		//Write the last bit that is smaller than BUFFER_SIZE
		if (socketReceiver.available() > 0)
		{
			int amountRead = socketReceiver.read(buffer);
			fileOutput.writeToFile(buffer, amountRead);
		}
	}

//	private void awaitConnection()
//	{
//		ConnectionResolver resolver = new ConnectionResolver(new ConnectionResolver.ConnectionEvent()
//		{
//
//			@Override
//			public void connectionEstablished(Socket socket, SocketTransmitter socketTransmitter, SocketReceiver socketReceiver)
//			{
//				FileReceiver.this.socketReceiver = socketReceiver;
//				FileReceiver.this.socketTransmitter = socketTransmitter;
//			}
//		});
//
//		resolver.startListeningBlocking(listeningPort, CONNECTION_TIMEOUT_MILLIS);
//	}
}
