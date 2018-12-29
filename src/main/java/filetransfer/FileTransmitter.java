package filetransfer;

import org.jetbrains.annotations.NotNull;
import filesistem.FileException;
import filetransfer.api.*;
import network.ConnectionException;
import window.AppLogger;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTransmitter
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int CONNECTION_TIMEOUT_MILLIS = 10_000;

	private static final int START_CODE = 1;
	private static final int ERROR_CODE = -1;
	private static final int CHUNK_SIZE = 8192;

	private TransferOutput transferOutput;
	private TransferInput transferInput;
	private TransferFileInput fileInput;

	public FileTransmitter(@NotNull TransferOutput transferOutput, @NotNull TransferInput transferInput,
						   @NotNull TransferFileInput fileInput)
	{
		this.transferOutput = transferOutput;
		this.transferInput = transferInput;
		this.fileInput = fileInput;
	}

	public void transfer() throws FileException, ConnectionException, FileNotFoundException
	{
		LOGGER.log(Level.FINE, "File transmission starting...");

		try
		{
			fileInput.open();
			if (listenForStartCode())
			{
				readBytesAndTransmitThemOverSocket();
			}
		} finally
		{
			LOGGER.log(Level.FINE, "File transmission done.");
		}
	}

	private boolean listenForStartCode() throws ConnectionException
	{
		DeltaTime dt = new DeltaTime();
		while (hasTime(dt))
		{
			if (transferInput.available() > 0)
			{
				byte value = (byte) transferInput.read();
				if (value == START_CODE)
				{
					return true;
				} else if (value == ERROR_CODE)
				{
					return false;
				}
			}
			try
			{
				Thread.sleep(500);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	private void readBytesAndTransmitThemOverSocket() throws FileException, ConnectionException
	{
		byte[] buffer = new byte[CHUNK_SIZE];
		boolean hasMore;

		while (hasMore = (fileInput.available() > 0))
		{
			transferChunk(buffer);
			if (transferInput.available() > 0)
			{
				if (hasError())
					break;
			}
		}

		if (hasMore)
		{
			throw new FileException("Could not transmit everything");
		}
	}

	private boolean hasError() throws ConnectionException
	{
		byte value = (byte) transferInput.read();
		if (value == ERROR_CODE)
		{
			return true;
		}
		return false;
	}

	protected int transferChunk(byte[] buffer)
			throws FileException, ConnectionException
	{
		int bytesRead = fileInput.read(buffer, CHUNK_SIZE);
		transferOutput.transmitBytes(buffer, bytesRead);
		return bytesRead;
	}

	private boolean hasTime(DeltaTime dt)
	{
		return dt.getElapsedTimeMillis() <= CONNECTION_TIMEOUT_MILLIS;
	}
}
