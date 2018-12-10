package logic.client;

import logic.*;
import window.UIEvents;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public abstract class Controller implements Closeable
{
	protected BusinessEvents businessEvents;
	protected ConnectCloseEvent mainConnectCloseEvent;

	protected Connection mainConnection;
	protected Connection transmittingConnection;
	protected Connection receivingConnection;

	protected TransmittingController transmitterController;
	protected ReceiverController receiverController;


	public abstract void go();

	class UIFileEventsHandler implements UIEvents.FileEvents
	{
		@Override
		public void updateAvailableFileList(List<File> file)
		{
			System.out.println("Update");
			new Thread(() -> transmitterController.updateAvailableFileList(file)).start();
		}

		@Override
		public void requestFileForDownload(String fileName)
		{
			new Thread(() -> transmitterController.requestFileForDownload(
					fileName, businessEvents.getDownloadPath())).start();
		}
	}

	@Override
	public void close()
	{
		closeConnections();
	}

	private void closeConnections()
	{
		if (null != mainConnection)
		{
			mainConnection.close();
			mainConnection = null;
		}

		if (null != receiverController)
		{
			receiverController.stopListening();
			receiverController = null;
		}

		if (null != transmitterController)
			transmitterController = null;

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
