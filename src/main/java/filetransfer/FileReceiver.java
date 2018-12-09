package filetransfer;

import com.sun.istack.internal.NotNull;
import window.AppLogger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileReceiver extends Thread
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int CONNECTION_TIMEOUT_MILLIS = 5_000;
	private static final int BUFFER_SIZE = 8192;

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
		if (null != socketReceiver)
		{
			try
			{
				fileOutput.createTempFile();
				receiveBytesAndWriteToFile(fileOutput);
				fileOutput.finishFile();

				LOGGER.log(Level.ALL, "File receiving done");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		} else
		{
			LOGGER.log(Level.WARNING, "Connection timeout");
		}

	}

	private void receiveBytesAndWriteToFile(TransferFileOutput fileOutput) throws IOException
	{
		int myBufferSize = (int)(socketReceiver.getBufferSize() * 0.75);
		byte[] buffer = new byte[BUFFER_SIZE];

		DeltaTime lastSuccessfulTransmission = new DeltaTime();
		boolean receivedBeforeLastSend = true;

		//TODO: Better way to determine when a file is done or not, can't rely on a timer
		while (lastSuccessfulTransmission.getElapsedTimeMillis() < CONNECTION_TIMEOUT_MILLIS)
		{
			if (receivedBeforeLastSend && socketReceiver.available() < myBufferSize)
			{
				sendFreeBufferSizeToRemote(myBufferSize - socketReceiver.available());
				receivedBeforeLastSend = false;
			}

			if (socketReceiver.available() >= BUFFER_SIZE)
			{
				while (socketReceiver.available() >= BUFFER_SIZE)
				{
					int amountRead = socketReceiver.read(buffer);
					fileOutput.writeToFile(buffer, amountRead);
				}
				lastSuccessfulTransmission = new DeltaTime();
				receivedBeforeLastSend = true;
			}
		}
		//Write the last chunk that is smaller than BUFFER_SIZE(if it exists)
		if (socketReceiver.available() > 0)
		{
			int amountRead = socketReceiver.read(buffer);
			fileOutput.writeToFile(buffer, amountRead);
		}
	}

	private void sendFreeBufferSizeToRemote(int freeBufferSize) throws IOException
	{
		byte[] valueInBytes = ByteBuffer.allocate(Integer.BYTES).putInt(freeBufferSize).array();
		socketTransmitter.transmitBytes(valueInBytes, Integer.BYTES);
	}
}
