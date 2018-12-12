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
			System.out.println("Buffer left: " + input.available());
			input.skip(input.available());
			fileOutput.open();
			receiveBytesAndWriteToFile();
			System.out.println("Buffer left: " + input.available());
			LOGGER.log(Level.FINE, "File receiving done");
		} catch (IOException | InterruptedException e)
		{
			successful = false;
			fileOutput.abort();
			System.out.println(e.getMessage());
//			e.printStackTrace();
		}

		return successful;
	}

	private void receiveBytesAndWriteToFile() throws IOException, InterruptedException
	{
		int maxBufferSize = (int) (input.getBufferSize() * 0.75);

		byte[] buffer = new byte[BUFFER_SIZE];
		long bytesLeftToReceive = fileSizeBytes;

		DeltaTime timeout = new DeltaTime();

		while (hasTime(timeout) && bytesLeftToReceive > 0)
		{
//			System.out.println("Top available: " + input.available() + " left time: " + timeout.getElapsedTimeMillis());

			if (input.available() >= BUFFER_SIZE)
			{
				while (input.available() >= BUFFER_SIZE)
				{
//					System.out.println("Transferring: " + BUFFER_SIZE);
					bytesLeftToReceive -= transferChunkOfData(buffer, BUFFER_SIZE);
				}
				timeout.reset();
			}else if (input.available() == bytesLeftToReceive)
			{
//				System.out.println("Transferring: " + input.available());
				bytesLeftToReceive -= transferChunkOfData(buffer, input.available());
			} else
			{
				//Send the buffer size to remote and wait for more bytes to arrive
				int available = input.available();
				sendFreeBufferSizeToRemote(maxBufferSize - available);
//				System.out.println("Sent " + (maxBufferSize - available));
				while (available == input.available() && hasTime(timeout))
					Thread.sleep(100);
			}
		}

		if (bytesLeftToReceive != 0)
			throw new IOException("Did not receive full file, bytes missing: " + bytesLeftToReceive);
	}

	private int transferChunkOfData(byte[] buffer, int amount) throws IOException
	{
		int amountRead = input.read(buffer, amount);
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
