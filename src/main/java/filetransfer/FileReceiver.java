package filetransfer;

import com.sun.istack.internal.NotNull;
import filetransfer.api.TransferFileOutput;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import window.AppLogger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileReceiver
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int CONNECTION_TIMEOUT_MILLIS = 10_000;
	private static final int BUFFER_SIZE = 8192;

	private TransferFileOutput fileOutput;
	private TransferInput input;
	private TransferOutput output;

	private long fileSizeBytes;


	public FileReceiver(@NotNull TransferInput socketReceiver, @NotNull TransferOutput socketTransmitter,
						@NotNull TransferFileOutput fileOutput, long fileSizeBytes)
	{
		assert null != socketReceiver : "Invalid socket receiver";
		assert null != socketTransmitter : "Invalid socket transmitter";
		assert null != fileOutput : "Invalid fileOutput";
		assert fileSizeBytes > 0 : "Invalid file size";

		this.fileOutput = fileOutput;
		this.input = socketReceiver;
		this.output = socketTransmitter;

		this.fileSizeBytes = fileSizeBytes;
	}

	public boolean transfer()
	{
		boolean successful = true;

		try
		{
			fileOutput.open();
			receiveBytesAndWriteToFile();

			LOGGER.log(Level.FINE, "File receiving done");
		} catch (IOException | InterruptedException e)
		{
			successful = false;
			fileOutput.abort();
			e.printStackTrace();
		}

		return successful;
	}

	private void receiveBytesAndWriteToFile() throws IOException, InterruptedException
	{
		int maxBufferSize = (int) (input.getBufferSize() * 0.75);
		int minBufferSize = (int) (input.getBufferSize() * 0.25);

		byte[] buffer = new byte[BUFFER_SIZE];
		long bytesLeftToReceive = fileSizeBytes;

		DeltaTime timeout = new DeltaTime();

		while (hasTime(timeout) && bytesLeftToReceive > 0)
		{
			if (input.available() < minBufferSize && input.available() > maxBufferSize)
			{
				//Send the buffer size over and wait for response
				System.out.println("Sending: " + maxBufferSize + input.available());
				sendFreeBufferSizeToRemote(maxBufferSize - input.available());
				while (input.available() == 0 && hasTime(timeout))
					Thread.sleep(100);
			}
			System.out.println(input.available());
			if (input.available() >= BUFFER_SIZE)
			{
				while (input.available() >= BUFFER_SIZE)
				{
					bytesLeftToReceive -= transferChunkOfData(buffer);
				}
				timeout.reset();
			}else if (input.available() == bytesLeftToReceive)
			{
				bytesLeftToReceive -= transferChunkOfData(buffer);
			}
		}

		if (bytesLeftToReceive != 0)
			throw new IOException("Did not receive full file, bytes missing:" + bytesLeftToReceive);
	}

	private int transferChunkOfData(byte[] buffer) throws IOException
	{
		int amountRead = input.read(buffer);
		fileOutput.writeToFile(buffer, amountRead);

		return amountRead;
	}

	private boolean hasTime(DeltaTime dt)
	{
		return dt.getElapsedTimeMillis() <= CONNECTION_TIMEOUT_MILLIS;
	}

	private void sendFreeBufferSizeToRemote(int freeBufferSize) throws IOException
	{
		byte[] valueInBytes = ByteBuffer.allocate(Integer.BYTES).putInt(freeBufferSize).array();
		output.transmitBytes(valueInBytes, Integer.BYTES);
	}
}
