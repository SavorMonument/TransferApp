package filetransfer;

import filesistem.FileException;
import filesistem.FileInput;
import filesistem.FileOutput;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import logic.connection.Connection;
import network.ConnectionException;
import network.connection.NetworkConnectionResolver;
import window.ByteMultipleFormatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;

public class FileTransferFunctionalTest
{
	private static final String INPUT_FILE_PATH = "src\\test\\resources\\filetransfer\\test.mkv";
	private static final String OUTPUT_FILE_DIRECTORY = "src\\test\\resources\\filetransfer\\";
	private static final String OUTPUT_FILE_NAME  = "test_output.mkv";

	static Connection connection1;
	static Connection connection2;


	public static void main(String[] args) throws IOException, InterruptedException
	{
		new File(OUTPUT_FILE_DIRECTORY + OUTPUT_FILE_NAME).delete();
		long fileLength = new File(INPUT_FILE_PATH).length();
		resolveConnections();

		FileInput fileInput = new FileInput(INPUT_FILE_PATH);
		FileOutput fileOutput = new FileOutput(OUTPUT_FILE_NAME, OUTPUT_FILE_DIRECTORY);

		FileTransmitter fileTransmitter = new FileTransmitter((TransferOutput) connection1.getMessageTransmitter(),
				(TransferInput) connection1.getMessageReceiver(), fileInput);

		FileReceiver fileReceiver = new FileReceiver((TransferInput) connection2.getMessageReceiver(),
				(TransferOutput) connection2.getMessageTransmitter(), fileOutput,
				fileLength);


		Thread transmitterThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					fileTransmitter.transfer();
				} catch (FileException e)
				{
					e.printStackTrace();
				} catch (ConnectionException e)
				{
					e.printStackTrace();
				} catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}

			}
		});

		Thread receiverThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					fileReceiver.transfer();
				} catch (ConnectionException e)
				{
					e.printStackTrace();
				} catch (FileException e)
				{
					e.printStackTrace();
				}
			}
		});
		DeltaTime dt = new DeltaTime();

		transmitterThread.start();
		receiverThread.start();

		transmitterThread.join();
		receiverThread.join();

		double timePassed = dt.getElapsedTimeSeconds();
		System.out.println(timePassed + " seconds");
		System.out.println(ByteMultipleFormatter.getFormattedBytes((int)(fileLength / timePassed))
				+ " average speed");
	}

//	private static void startCounter()
//	{
//		connection1.getMessageTransmitter().registerBytesCounter(new TransferView(new TransferView.ByteCounterEvent()
//		{
//			@Override
//			public void updateOnBytes(long bytes)
//			{
//				System.out.println(ByteMultipleFormatter.getFormattedBytes(bytes) + "/s");
//			}
//		}, 1000));
//	}

	private static void resolveConnections() throws IOException, InterruptedException
	{
		NetworkConnectionResolver resolver = new NetworkConnectionResolver();

		new Thread(() ->
		{
			try
			{
				connection1 = resolver.listenNextConnection();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}).start();

		connection2 = resolver.attemptNextConnection(InetAddress.getByName("localhost"));

		Thread.sleep(500);
	}

}
