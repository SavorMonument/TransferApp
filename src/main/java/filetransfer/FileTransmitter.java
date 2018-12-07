package filetransfer;

import com.sun.istack.internal.NotNull;
import window.AppLogger;

import java.io.IOException;
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
			LOGGER.log(Level.WARNING, "File transmitting failed " + e.getMessage());
//					e.printStackTrace();
		}
	}

	private void readBytesAndTransmitThemOverSocket(TransferFileInput fileInput) throws IOException
	{
		byte[] buffer = new byte[BUFFER_SIZE];

		int bytesRead = BUFFER_SIZE;
		while (bytesRead == BUFFER_SIZE)
		{
			System.out.println("Here");
			if (socketReceiver.read() > -1)
			{
				bytesRead = fileInput.read(buffer, BUFFER_SIZE);
				System.out.println("Sending: " + bytesRead + "bytes");
				socketTransmitter.transmitBytes(buffer, bytesRead);
			}
		}
	}
}
