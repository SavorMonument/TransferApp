package logic.messaging.actions.transfer;

import filesistem.FileException;
import filesistem.FileOutput;
import filetransfer.FileReceiver;
import filetransfer.InvalidFilePath;
import filetransfer.ObservabileFileReceiver;
import filetransfer.api.TransferInput;
import filetransfer.api.TransferOutput;
import logic.BusinessEvents;
import logic.ConnectCloseEvent;
import logic.FileHandle;
import logic.connection.Connection;
import logic.messaging.actions.Action;
import logic.messaging.messages.TransferRequestMessage;
import model.FileInfo;
import network.ConnectionException;
import window.AppLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TransmitterTransferAction implements Action
{
	private static final Logger LOGGER = AppLogger.getInstance();
	public static final String ID = TransferRequestMessage.MESSAGE_ID;

	private BusinessEvents businessEvents;
	private ConnectCloseEvent connectCloseEvent;
	private Connection fileConnection;
	private FileInfo fileInfo;

	public TransmitterTransferAction(BusinessEvents businessEvents,
									 ConnectCloseEvent connectCloseEvent,
									 Connection fileConnection,
									 FileInfo fileInfo)
	{
		this.businessEvents = businessEvents;
		this.connectCloseEvent = connectCloseEvent;
		this.fileConnection = fileConnection;
		this.fileInfo = fileInfo;
	}

	@Override
	public void performAction()
	{
		initiateFileTransfer();
	}

	private void initiateFileTransfer()
	{
		businessEvents.setDownloadingState(true);
		FileOutput fileOutput = new FileOutput(fileInfo.getName(), businessEvents.getDownloadLocation());
		FileHandle fileHandle = businessEvents.getRemoteFileHandle(fileInfo);
		TransferUpdater transferUpdater = new TransferUpdater(fileHandle, fileInfo.getSizeInBytes());

		FileReceiver fileReceiver = new ObservabileFileReceiver(
				(TransferInput) fileConnection.getMessageReceiver(),
				(TransferOutput) fileConnection.getMessageTransmitter(),
				fileOutput,
				transferUpdater,
				fileInfo.getSizeInBytes());

		LOGGER.log(Level.FINE, String.format("Starting file receiver with file: %s from address: %s, port%d",
				fileInfo.getName(), fileConnection.getRemoteAddress(), fileConnection.getRemotePort()));

		try
		{
			businessEvents.printMessageOnDisplay("Attempting file download");

			transferUpdater.start();
			fileReceiver.transfer();
			transferUpdater.interrupt();
			fileHandle.setTransferProgress(1.0);

			businessEvents.printMessageOnDisplay("File downloaded successfully");
			LOGGER.log(Level.FINE, "File download finished");
		} catch (FileException e)
		{
			fileOutput.abort();
			LOGGER.log(Level.WARNING, e.toString() + e.getMessage());
			businessEvents.printMessageOnDisplay("File error: " + e.getMessage());
		} catch (ConnectionException e)
		{
			fileOutput.abort();
			LOGGER.log(Level.WARNING, e.toString() + e.getMessage());
			connectCloseEvent.disconnect(e.getMessage());
		} catch (InvalidFilePath e)
		{
			LOGGER.log(Level.WARNING, e.toString() + e.getMessage());
			businessEvents.printMessageOnDisplay("File error: " + e.getMessage());
		} finally
		{
			transferUpdater.interrupt();
			fileOutput.close();
			businessEvents.setDownloadingState(false);
		}
	}
}
