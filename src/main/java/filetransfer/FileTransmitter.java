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

public class FileTransmitter extends Thread
{
	private static final Logger LOGGER = AppLogger.getInstance();
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

		setDaemon(true);
	}

	@Override
	public void run()
	{
		try
		{
			fileInput.open();
			readBytesAndTransmitThemOverSocket(fileInput);
			LOGGER.log(Level.ALL, "File transmission done");
		} catch (IOException e)
		{
			LOGGER.log(Level.WARNING, "File transmitting disconnect " + e.getMessage());
//					e.printStackTrace();
		}
	}

	private void readBytesAndTransmitThemOverSocket(TransferFileInput fileInput) throws IOException
	{
		byte[] buffer = new byte[BUFFER_SIZE];
		byte[] values = new byte[4];
		int remoteBufferSize = 0;
		boolean hasMore = true;

		while (hasMore)
		{
			if (socketReceiver.available() > 0)
			{
				socketReceiver.read(values);
				remoteBufferSize = ByteBuffer.wrap(values).getInt();
			}
			hasMore = sendBytesUpToLimit(fileInput, buffer, remoteBufferSize);
		}
		clearInputBuffer();
	}

	private boolean sendBytesUpToLimit(TransferFileInput fileInput, byte[] buffer, int limit) throws IOException
	{
		int bytesRead = BUFFER_SIZE;
		while (limit >= BUFFER_SIZE && bytesRead >= BUFFER_SIZE)
		{
			bytesRead = fileInput.read(buffer, BUFFER_SIZE);
			socketTransmitter.transmitBytes(buffer, bytesRead);
			limit -= BUFFER_SIZE;
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
