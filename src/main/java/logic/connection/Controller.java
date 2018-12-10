package logic.connection;

import logic.ConnectCloseEvent;
import logic.api.BusinessEvents;
import logic.api.Connection;
import logic.messaging.FileInformation;
import logic.messaging.MessageReceiverController;
import logic.messaging.MessageTransmitterController;
import window.UIEvents;

import java.io.Closeable;
import java.io.File;
import java.util.List;
import java.util.Set;

public abstract class Controller implements Closeable
{
	protected BusinessEvents businessEvents;
	protected ConnectCloseEvent mainConnectCloseEvent;

	protected Connection mainConnection;
	protected Connection transmittingConnection;
	protected Connection receivingConnection;

	protected MessageTransmitterController transmitterController;
	protected MessageReceiverController receiverController;


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
			new Thread(() -> transmitterController.requestFileForDownload(
					fileInformation, downloadPath)).start();
		}
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

		if (null != receivingConnection)
		{
			receivingConnection.close();
			receivingConnection = null;
		}

		if (null != transmittingConnection)
		{
			transmittingConnection.close();
			transmittingConnection = null;
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
