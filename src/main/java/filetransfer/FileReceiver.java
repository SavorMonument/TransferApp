package filetransfer;

import com.sun.istack.internal.NotNull;
import filesistem.FileException;
import filetransfer.api.*;
import logic.messaging.ConnectionException;
import window.AppLogger;

import java.util.logging.Logger;

public class FileReceiver
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int CONNECTION_TIMEOUT_MILLIS = 10_000;
	private static final int ERROR_CODE = -1;
	private static final int START_CODE = 1;
	private static final int CHUNK_SIZE = 8192;

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

	public void transfer() throws ConnectionException, FileException
	{
		input.skip(input.available());
		fileOutput.open();
		try
		{
			output.transmitByte(START_CODE);
			receiveBytesAndWriteToFile();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (FileException e)
		{
			output.transmitByte(ERROR_CODE);
			input.skip(input.available());
			throw e;
		}
		System.out.println(input.available());
	}

	private void receiveBytesAndWriteToFile() throws ConnectionException, FileException, InterruptedException
	{
		byte[] buffer = new byte[CHUNK_SIZE];
		long bytesLeftToReceive = fileSizeBytes;

		int available;
		DeltaTime dt = new DeltaTime();
		while (bytesLeftToReceive > 0 && hasTime(dt))
		{
			if ((available = input.available()) > 0)
			{
				bytesLeftToReceive -= transferChunkOfData(buffer, Math.min(available, CHUNK_SIZE));
				dt.reset();
			} else
				Thread.sleep(100);
		}
		if (bytesLeftToReceive != 0)
			throw new ConnectionException("Did not receive full file, bytes missing: " + bytesLeftToReceive);
	}

	private int transferChunkOfData(byte[] buffer, int maxAmountToRead) throws ConnectionException, FileException
	{
		int amountRead = input.read(buffer, maxAmountToRead);
		fileOutput.writeToFile(buffer, amountRead);

		return amountRead;
	}

	private boolean hasTime(DeltaTime dt)
	{
		return dt.getElapsedTimeMillis() <= CONNECTION_TIMEOUT_MILLIS;
	}

}