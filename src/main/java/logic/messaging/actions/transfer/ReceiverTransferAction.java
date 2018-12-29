package logic.messaging.actions.transfer;

import filesistem.FileException;
import filesistem.FileInput;
import filetransfer.FileTransmitter;
import filetransfer.ObservableFileTransmitter;
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

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReceiverTransferAction implements Action
{
	private static final Logger LOGGER = AppLogger.getInstance();
	public static final String ID = TransferRequestMessage.MESSAGE_ID;

	private BusinessEvents businessEvents;
	private Connection fileConnection;
	private ConnectCloseEvent connectCloseEvent;
	private FileInfo fileInfo;

	public ReceiverTransferAction(BusinessEvents businessEvents,
								  Connection fileConnection,
								  ConnectCloseEvent connectCloseEvent,
								  FileInfo fileInfo)
	{
		this.businessEvents = businessEvents;
		this.fileConnection = fileConnection;
		this.connectCloseEvent = connectCloseEvent;
		this.fileInfo = fileInfo;
	}

	@Override
	public void performAction()
	{
		initiateFileTransfer();
	}

	private void initiateFileTransfer()
	{
		FileInput fileInput = new FileInput(businessEvents.getLocalFilePath(fileInfo));
		FileHandle fileHandle = businessEvents.getLocalFileHandle(fileInfo);

		TransferUpdater transferUpdater = new TransferUpdater(fileHandle, fileInfo.getSizeInBytes());

		FileTransmitter fileTransmitter = new ObservableFileTransmitter(
				(TransferOutput) fileConnection.getMessageTransmitter(),
				(TransferInput) fileConnection.getMessageReceiver(),
				fileInput,
				transferUpdater);
		LOGGER.log(Level.FINE, String.format("Starting file transmitter with file: %s to address: %s, port%d",
				fileHandle, fileConnection.getRemoteAddress(), fileConnection.getRemotePort()));

		try
		{
			businessEvents.printMessageOnDisplay("Attempting file upload");

			transferUpdater.start();
			fileTransmitter.transfer();

			businessEvents.printMessageOnDisplay("File upload finished");
			LOGGER.log(Level.FINE, "File upload finished");
		} catch (FileNotFoundException | FileException e)
		{
			LOGGER.log(Level.WARNING, e.toString() + e.getMessage());
			businessEvents.printMessageOnDisplay("File error: " + e.getMessage());
		}catch (ConnectionException e)
		{
			LOGGER.log(Level.WARNING, e.toString() + e.getMessage());
			connectCloseEvent.disconnect("Connection error");
		} finally
		{
			transferUpdater.interrupt();
			fileInput.close();
		}
	}
}
