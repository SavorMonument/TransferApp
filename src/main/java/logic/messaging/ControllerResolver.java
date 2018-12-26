package logic.messaging;

import logic.ConnectCloseEvent;
import logic.BusinessEvents;
import logic.connection.Connection;
import logic.connection.Connections;
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

	protected TransferView transmittingCounter;
	protected TransferView receivingCounter;

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

		//TODO: move the counter in file transmitter/receiver and register them when download/upload starts
//		registerTransmittingCounter(fTransmittingConnection.getMessageTransmitter());
//		registerReceivingCounter(fReceivingConnection.getMessageReceiver());
	}

//	private void registerTransmittingCounter(Connection.StringTransmitter messageTransmitter)
//	{
//		transmittingCounter = new TransferView(businessEvents::printUploadSpeed, 1000);
//		messageTransmitter.registerBytesCounter(transmittingCounter);
//	}
//
//	private void registerReceivingCounter(Connection.StringReceiver messageReceiver)
//	{
//		receivingCounter = new TransferView(businessEvents::printDownloadSpeed, 1000);
//		messageReceiver.registerBytesCounter(receivingCounter);
//	}

	class UIFileEventsHandler implements UIEvents.FileEvents
	{
		@Override
		public void updateAvailableFiles(List<FileInfo> fileInfos)
		{
			System.out.println("Update");
			new Thread(() -> transmitterController.updateAvailableFileList(fileInfos)).start();
		}

		@Override
		public void requestFileForDownload(FileInfo fileInfo, String downloadPath)
		{
			new Thread(() -> transmitterController.fileDownload(
					fileInfo, downloadPath)).start();
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
