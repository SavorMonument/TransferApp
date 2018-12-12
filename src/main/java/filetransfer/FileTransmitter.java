package filetransfer;

import com.sun.istack.internal.NotNull;
import filetransfer.api.*;
import window.AppLogger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTransmitter
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int CONNECTION_TIMEOUT_MILLIS = 10_000;

	private static final int ERROR_CODE = -1;
	private static final int START_CODE = 1;
	private static final int CHUNK_SIZE = 8192;

	private TransferOutput socketTransmitter;
	private TransferInput socketReceiver;
	private TransferFileInput fileInput;

	private boolean isTransferring = false;
	private boolean isErrorState = false;

	public FileTransmitter(@NotNull TransferOutput socketTransmitter, @NotNull TransferInput socketReceiver,
						   @NotNull TransferFileInput fileInput)
	{
		this.socketTransmitter = socketTransmitter;
		this.socketReceiver = socketReceiver;
		this.fileInput = fileInput;
	}

	public void transfer() throws FileException, TransferException, FileNotFoundException
	{
		LOGGER.log(Level.FINE, "File transmission starting");
		fileInput.open();

		if (listenForStartCode())
		{
			isTransferring = true;
			listenForError();
			readBytesAndTransmitThemOverSocket();
			LOGGER.log(Level.FINE, "File transmission done");
		}
	}

	private boolean listenForStartCode() throws TransferException
	{
		boolean gotStartByte = false;

		DeltaTime dt = new DeltaTime();
		while (!gotStartByte && hasTime(dt))
		{
			if (socketReceiver.available() > 0)
			{
				byte value = (byte) socketReceiver.read();
				if (value == START_CODE)
				{
					gotStartByte = true;
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
		return gotStartByte;
	}

	private void readBytesAndTransmitThemOverSocket() throws FileException, TransferException
	{
		byte[] buffer = new byte[CHUNK_SIZE];
		boolean hasMore;

		listenForError();

		try
		{
			while (hasMore = (fileInput.available() > 0) && !isErrorState)
			{
				transferChunk(fileInput, socketTransmitter, buffer);
			}
		} finally
		{
			isTransferring = false;
		}
		if (hasMore)
		{
			throw new FileException("Could not transmit everything");
		}
	}

	private void transferChunk(TransferFileInput fileInput, TransferOutput transferOutput, byte[] buffer)
			throws FileException, TransferException
	{
		int bytesRead = fileInput.read(buffer, CHUNK_SIZE);
		socketTransmitter.transmitBytes(buffer, bytesRead);
	}

	private boolean hasTime(DeltaTime dt)
	{
		return dt.getElapsedTimeMillis() <= CONNECTION_TIMEOUT_MILLIS;
	}

	private void listenForError()
	{
		Thread listeningThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (isTransferring)
				{
					try
					{
						if (socketReceiver.available() > 0)
						{
							byte value = (byte) socketReceiver.read();
							if (value == ERROR_CODE)
							{
								System.out.println("Setting error state to true");
								isErrorState = true;
							}
						}
						Thread.sleep(500);
					} catch (InterruptedException | IOException e)
					{
//						e.printStackTrace();
					}
				}
			}
		});

		listeningThread.setDaemon(true);
		listeningThread.start();
	}
}
