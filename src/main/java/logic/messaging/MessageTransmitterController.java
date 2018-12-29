package logic.messaging;

import org.jetbrains.annotations.NotNull;
import logic.BusinessEvents;
import logic.ConnectCloseEvent;
import logic.connection.Connection.StringTransmitter;
import logic.connection.Connections;
import logic.messaging.actions.Action;
import logic.messaging.actions.ActionFactory;
import logic.messaging.messages.NetworkMessage;
import logic.messaging.messages.TransferRequestMessage;
import logic.messaging.messages.UpdateFileListMessage;
import model.FileInfo;
import network.ConnectionException;
import window.AppLogger;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageTransmitterController
{
	private static final Logger LOGGER = AppLogger.getInstance();

	private StringTransmitter messageTransmitter;
	private ConnectCloseEvent connectCloseEvent;

	private BusinessEvents businessEvents;

	private ActionFactory actionFactory;

	public MessageTransmitterController(@NotNull Connections connections,
										@NotNull BusinessEvents businessEvents,
										@NotNull ConnectCloseEvent connectCloseEvent)
	{
		assert null != connections : "Null connections";
		assert null != businessEvents : "Null BusinessEvents";
		assert null != connectCloseEvent : "Null handler";

		this.businessEvents = businessEvents;
		this.connectCloseEvent = connectCloseEvent;

		this.messageTransmitter = connections.getMainConnection().getMessageTransmitter();

		actionFactory = new ActionFactory(businessEvents, connectCloseEvent, connections.getFileReceivingConnection());
	}

	public void updateAvailableFileList(List<FileInfo> fileInfos)
	{
		LOGGER.log(Level.FINE, "Sending file update..." + fileInfos.toString());

		NetworkMessage networkMessage = new UpdateFileListMessage(fileInfos);
		try
		{
			messageTransmitter.transmitString(networkMessage.getFormattedMessage());
		} catch (ConnectionException e)
		{
			LOGGER.log(Level.WARNING, "Exception while transmitting file list: " + e.getMessage());
			connectCloseEvent.disconnect("Connection error, disconnecting...");
		}

		Action action = actionFactory.getTransmitterAction(networkMessage);
		new Thread(() -> action.performAction()).start();
	}

	public void fileDownload(FileInfo fileInfo)
	{
		NetworkMessage networkMessage = new TransferRequestMessage(fileInfo);
		try
		{
			LOGGER.log(Level.FINE, "Sending file download request: " + fileInfo);
			messageTransmitter.transmitString(networkMessage.getFormattedMessage());
		} catch (ConnectionException e)
		{
			LOGGER.log(Level.WARNING, e.getMessage());
			connectCloseEvent.disconnect("Connection error, disconnecting...");
		}

		Action action = actionFactory.getTransmitterAction(networkMessage);
		new Thread(() -> action.performAction()).start();
	}
}
