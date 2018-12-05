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

//		setDaemon(true);
	}

	@Override
	public void run()
	{
//		attemptConnection();
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
			fileInput.close();
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

//	private void attemptConnection()
//	{
//		ConnectionResolver resolver = new ConnectionResolver(new ConnectionResolver.ConnectionEvent()
//		{
//			@Override
//			public void connectionEstablished(Socket socket, SocketTransmitter socketTransmitter, SocketReceiver socketReceiver)
//			{
//				FileTransmitter.this.socketTransmitter = socketTransmitter;
//				FileTransmitter.this.socketReceiver = socketReceiver;
//				try
//				{
//					socketBufferSize = socket.getSendBufferSize();
//				} catch (SocketException e)
//				{
//					e.printStackTrace();
//				}
//			}
//		});
//		resolver.attemptConnection(socketURL, transmittingPort, transmittingPort);
//	}
}
