package logic.connection;

import logic.ConnectCloseEvent;
import logic.BusinessEvents;
import logic.messaging.FileInformation;
import logic.messaging.MessageReceiverController;
import logic.messaging.MessageTransmitterController;
import window.UIEvents;

import java.io.Closeable;
import java.io.File;
import java.util.Set;

public abstract class Controller implements Closeable
{
	protected BusinessEvents businessEvents;
	protected ConnectCloseEvent mainConnectCloseEvent;

	protected Connection mainConnection;
	protected Connection fileConnectionTwo;
	protected Connection fileConnectionOne;
	protected ByteCounter transmittingCounter;
	protected ByteCounter receivingCounter;

	protected MessageTransmitterController transmitterController;
	protected MessageReceiverController receiverController;

	public Controller(BusinessEvents businessEvents, ConnectCloseEvent mainConnectCloseEvent)
	{
		this.businessEvents = businessEvents;
		this.mainConnectCloseEvent = mainConnectCloseEvent;
	}

	public abstract void go();

	class UIFileEventsHandler implements UIEvents.FileEvents
	{
		@Override
		public void updateAvailableFiles(Set<File> files)
		{
			System.out.println("Update");
			new Thread(() -> transmitterController.updateAvailableFileList(files)).start();
		}

		@Override
		public void requestFileForDownload(FileInformation fileInformation, String downloadPath)
		{
			new Thread(() -> transmitterController.fileDownload(
					fileInformation, downloadPath)).start();
		}
	}

	protected void registerTransmittingCounter(Connection.MessageTransmitter messageTransmitter)
	{
		transmittingCounter = new ByteCounter(businessEvents::printUploadSpeed, 500);
		messageTransmitter.registerBytesCounter(transmittingCounter);
	}

	protected void registerReceivingCounter(Connection.StringReceiver messageReceiver)
	{
		receivingCounter = new ByteCounter(businessEvents::printDownloadSpeed, 500);
		messageReceiver.registerBytesCounter(receivingCounter);
	}

	@Override
	public void close()
	{
		closeConnections();
	}

	private void closeConnections()
	{
		if (null != transmitterController)
			transmitterController = null;

		if (null != receiverController)
		{
			receiverController.stopListening();
			receiverController = null;
		}

		if (null != mainConnection)
		{
			mainConnection.close();
			mainConnection = null;
		}

		if (null != fileConnectionOne)
		{
			fileConnectionOne.close();
			fileConnectionOne = null;
		}

		if (null != fileConnectionTwo)
		{
			fileConnectionTwo.close();
			fileConnectionTwo = null;
		}

		if (null != transmittingCounter)
		{
			transmittingCounter.interrupt();
			transmittingCounter = null;
			businessEvents.printUploadSpeed(0);
		}

		if (null != receivingCounter)
		{
			receivingCounter.interrupt();
			receivingCounter = null;
			businessEvents.printDownloadSpeed(0);
		}

		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
