package filetransfer;

import com.sun.istack.internal.NotNull;
import filetransfer.api.TransferFileInput;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import window.AppLogger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTransmitter
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int CONNECTION_TIMEOUT_MILLIS = 10_000;
	private static final int BUFFER_SIZE = 8192;

	private TransferOutput socketTransmitter;
	private TransferInput socketReceiver;
	private TransferFileInput fileInput;

	public FileTransmitter(@NotNull TransferOutput socketTransmitter, @NotNull TransferInput socketReceiver,
						   @NotNull TransferFileInput fileInput)
	{
		this.socketTransmitter = socketTransmitter;
		this.socketReceiver = socketReceiver;
		this.fileInput = fileInput;
	}

	public boolean transfer()
	{
		boolean successful = true;
		try
		{
			LOGGER.log(Level.FINE, "File transmission starting");
			fileInput.open();
			readBytesAndTransmitThemOverSocket(fileInput);
			LOGGER.log(Level.FINE, "File transmission done");
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "File transmitting disconnect " + e.getMessage());
			successful = false;
		}

		return successful;
	}

	private void readBytesAndTransmitThemOverSocket(TransferFileInput fileInput) throws IOException
	{
		byte[] buffer = new byte[BUFFER_SIZE];
		int remoteBufferSize = 0;
		boolean hasMore = true;

		DeltaTime timeout = new DeltaTime();
		while (hasMore && hasTime(timeout))
		{
			if (socketReceiver.available() > 0)
			{
				byte[] values = new byte[4];
				socketReceiver.read(values);
				remoteBufferSize = ByteBuffer.wrap(values).getInt();
			}
			System.out.println("--");
			System.out.println(remoteBufferSize);
			System.out.println("--");
			if (remoteBufferSize > BUFFER_SIZE)
			{
				hasMore = sendBytesUpToLimit(fileInput, buffer, remoteBufferSize);
				timeout.reset();
			}

		}
		clearInputBuffer();

		if (hasMore)
		{
			throw new IOException("Could not transmit everything");
		}
	}

	private boolean hasTime(DeltaTime dt)
	{
		return dt.getElapsedTimeMillis() <= CONNECTION_TIMEOUT_MILLIS;
	}

	private boolean sendBytesUpToLimit(TransferFileInput fileInput, byte[] buffer, int limit) throws IOException
	{
		int bytesRead = BUFFER_SIZE;
		while (limit >= BUFFER_SIZE && bytesRead >= BUFFER_SIZE)
		{
			bytesRead = fileInput.read(buffer, BUFFER_SIZE);
			System.out.println("after read");
			socketTransmitter.transmitBytes(buffer, bytesRead);
			limit -= BUFFER_SIZE;
			System.out.println(bytesRead + " : " + limit);
		}

		return bytesRead >= BUFFER_SIZE;
	}

	private void clearInputBuffer()
	{
		try
		{
			socketReceiver.skip(socketReceiver.available());
		} catch (IOException e)
		{
//			e.printStackTrace();
		}
	}
}
