package filetransfer;

import filesistem.FileException;
import filesistem.FileInput;
import filesistem.FileOutput;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import logic.FileHandle;
import logic.connection.Connection;
import logic.messaging.actions.transfer.TransferUpdater;
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

		TransferUpdater transferUpdater = new TransferUpdater(new FileHandle()
		{
			@Override
			public void setTransferSpeed(long bytesPerSecond)
			{
				System.out.println(String.format("Transfer speed: %s/s", ByteMultipleFormatter.getFormattedBytes(bytesPerSecond)));
			}

			@Override
			public void setTransferProgress(double progress)
			{
				System.out.println(String.format("Progress: %.2f", progress));
			}
		}, fileLength);

		FileTransmitter fileTransmitter = new ObservableFileTransmitter((TransferOutput) connection1.getMessageTransmitter(),
				(TransferInput) connection1.getMessageReceiver(), fileInput, transferUpdater);

		FileReceiver fileReceiver = new FileReceiver((TransferInput) connection2.getMessageReceiver(),
				(TransferOutput) connection2.getMessageTransmitter(), fileOutput, fileLength);

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
				} catch (InvalidFilePath invalidFilePath)
				{
					invalidFilePath.printStackTrace();
				}
			}
		});
		DeltaTime dt = new DeltaTime();

		transferUpdater.start();
		transmitterThread.start();
		receiverThread.start();

		transmitterThread.join();
		receiverThread.join();
		transferUpdater.interrupt();

		double timePassed = dt.getElapsedTimeSeconds();
		System.out.println(timePassed + " seconds");
		System.out.println(ByteMultipleFormatter.getFormattedBytes((int)(fileLength / timePassed))
				+ " average speed");
	}

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
