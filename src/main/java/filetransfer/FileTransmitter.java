package filetransfer;

import com.sun.istack.internal.NotNull;
import window.AppLogger;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileTransmitter extends Thread
{
	private static final Logger LOGGER = AppLogger.getInstance();
	private static final int BUFFER_SIZE = 4096;
	private static final int MAX_TRANSFER_AT_ONCE = BUFFER_SIZE * 10;

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
			LOGGER.log(Level.WARNING, "File transmitting failed " + e.getMessage());
//					e.printStackTrace();
		} finally
		{
			System.out.println();
			socketTransmitter.close();
		}
	}

	private void readBytesAndTransmitThemOverSocket(TransferFileInput fileInput) throws IOException
	{
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesSentSinceLastSignal = 0;

		int bytesRead = BUFFER_SIZE;
		while (bytesRead == BUFFER_SIZE)
		{
			if (bytesSentSinceLastSignal + BUFFER_SIZE < MAX_TRANSFER_AT_ONCE)
			{
				bytesRead = fileInput.read(buffer, BUFFER_SIZE);
				socketTransmitter.transmitBytes(buffer, bytesRead);
				bytesSentSinceLastSignal += BUFFER_SIZE;
			}
			if (socketReceiver.available() > 0)
			{
				socketReceiver.read();
				bytesSentSinceLastSignal = 0;
			}
		}
	}
}
