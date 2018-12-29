package logic.messaging;

import logic.ConnectCloseEvent;
import logic.BusinessEvents;
import logic.connection.Connection;
import logic.connection.Connections;
import model.FileInfo;
import window.UIEvents;

import java.io.Closeable;
import java.util.List;

public class ControllerResolver implements Closeable
{
	protected Connections connections;
	protected ConnectCloseEvent mainConnectCloseEvent;
	protected BusinessEvents businessEvents;

	protected MessageTransmitterController transmitterController;
	protected MessageReceiverController receiverController;

	public ControllerResolver(Connections connections, BusinessEvents businessEvents, ConnectCloseEvent mainConnectCloseEvent)
	{
		this.connections = connections;
		this.businessEvents = businessEvents;
		this.mainConnectCloseEvent = mainConnectCloseEvent;
	}

	public void initialize()
	{
		Connection fTransmittingConnection = connections.getFileTransmittingConnection();
		Connection fReceivingConnection = connections.getFileReceivingConnection();

		transmitterController = new MessageTransmitterController(connections, businessEvents, mainConnectCloseEvent);
		receiverController = new MessageReceiverController(connections, businessEvents, mainConnectCloseEvent);
		receiverController.startListening();

		UIFileEventsHandler handler = new UIFileEventsHandler();
		businessEvents.setFileEventHandler(handler);
	}

	class UIFileEventsHandler implements UIEvents.FileEvents
	{
		@Override
		public void updateAvailableFiles(List<FileInfo> fileInfos)
		{
			System.out.println("Update");
			new Thread(() -> transmitterController.updateAvailableFileList(fileInfos)).start();
		}

		@Override
		public void requestFileForDownload(FileInfo fileInfo)
		{
			new Thread(() -> transmitterController.fileDownload(
					fileInfo)).start();
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

		if (null != connections)
		{
			connections.close();
		}

		try
		{
			Thread.sleep(3000);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
